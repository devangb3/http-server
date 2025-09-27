package server

import (
	"bytes"
	"fmt"
	"httpfromtcp/httpfromtcp/internal/request"
	"httpfromtcp/httpfromtcp/internal/response"
	"io"
	"net"
)

// type ServerState string;
// const(
// 	Initialized ServerState = "init"
// 	Done ServerState = "done"
// )
type Server struct{
	closed bool
	handler Handler
}
type HandlerError struct{
	StatusCode response.StatusCode
	Message string
}
type Handler func(w io.Writer, req *request.Request) *HandlerError

func runServer(s *Server, listener net.Listener){
	go func(){
		for{
			conn, err := listener.Accept();
			if s.closed{
				return;
			}
			if err != nil{
				return;
			}
			go runConnection(s, conn);
		}
	}();
}
func runConnection(s *Server, conn io.ReadWriteCloser){
	defer conn.Close();

	headers := response.GetDefaultHeaders(0)
	r, err := request.RequestFromReader(conn);
	if err != nil{
		response.WriteStatusLine(conn, response.StatusBadRequest);
		response.WriteHeaders(conn, headers);
		return;
	}
	writer := bytes.NewBuffer([]byte{})
	handlerError := s.handler(writer, r);

	var body []byte = nil;
	var status response.StatusCode = response.StatusOK; 
	if handlerError != nil{
		status = handlerError.StatusCode;
		body = []byte(handlerError.Message);
	}else{
		body = writer.Bytes();
	}
	headers.Replace("Content-length", fmt.Sprintf("%d", len(body)));

	response.WriteStatusLine(conn, status);
	response.WriteHeaders(conn, headers);
	conn.Write(body);
}
func Serve(port int, handler Handler) (*Server, error){
	addr := fmt.Sprintf(":%d", port)
	listener, err := net.Listen("tcp", addr)
	if err != nil{
		return nil, err;
	}
	server := &Server{
		closed: false,
		handler: handler,
	}
	runServer(server, listener );
	return server, nil;
}

func (s * Server) Close() error{
	s.closed = true;
	return nil;
}