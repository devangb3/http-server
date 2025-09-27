package main

import (
	"fmt"
	"httpfromtcp/httpfromtcp/internal/request"
	"log"
	"net"
)

func main(){
	listener, err := net.Listen("tcp", ":42069")
	fmt.Println("Server is running on port 42069")
	if err!= nil{
		log.Fatal("error", "error", err) 
	}
	defer listener.Close();
	for{
		conn, err := listener.Accept()
		if err != nil{
			log.Fatalf("error %v", err)
		}
		
		req, err := request.RequestFromReader(conn);
		if err != nil{
			log.Fatal("error", "error", err)
		}
		fmt.Println("Request Line:")
		fmt.Printf("- Method: %v\n", req.RequestLine.Method);
		fmt.Printf("- Target: %v\n", req.RequestLine.RequestTarget);
		fmt.Printf("- Version: %v\n", req.RequestLine.HttpVersion);
		fmt.Println("Headers:\n")
		req.Headers.ForEach(func(n, v string){
			fmt.Printf("- %v: %v\n", n, v)
		})
		fmt.Println("Connection to ", conn.RemoteAddr(), "closed")
	}
	
}
