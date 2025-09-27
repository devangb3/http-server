package server

import (
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
type Handler func(w *response.Writer, req *request.Request)
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

	responseWriter := response.NewWriter(conn);
	r, err := request.RequestFromReader(conn);
	if err != nil{
		responseWriter.WriteStatusLine(response.StatusBadRequest);
		responseWriter.WriteHeaders(*response.GetDefaultHeaders(0));
		return;
	}
	s.handler(responseWriter, r);

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