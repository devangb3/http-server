package server

import (
	"fmt"
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
}
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
	response.WriteStatusLine(conn, response.StatusOK);
	response.WriteHeaders(conn, headers);
	
}
func Serve(port int) (*Server, error){
	addr := fmt.Sprintf(":%d", port)
	listener, err := net.Listen("tcp", addr)
	if err != nil{
		return nil, err;
	}
	server := &Server{closed: false}
	runServer(server, listener );
	return server, nil;
}

func (s * Server) Close() error{
	s.closed = true;
	return nil;
}