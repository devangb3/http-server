package main

import (
	"bufio"
	"fmt"
	"log"
	"net"
	"os"
)

func main(){
	serverAddr := "localhost:42069"
	udpAddr, err := net.ResolveUDPAddr("udp", serverAddr);
	if err != nil{
		log.Fatalf("error : %v", err);
	}
	
	conn,err := net.DialUDP("udp", nil, udpAddr)
	if err != nil{
		log.Fatalf("error %v", err);
	}
	defer conn.Close();

	reader := bufio.NewReader(os.Stdin);
	for{
		fmt.Println("> ")
		message, err := reader.ReadString('\n');
		if err != nil{
			log.Fatalf("Error %v", err);
		}
		_, err = conn.Write([]byte(message))
		if err != nil{
			log.Fatalf("Error %v", err)
		}
		fmt.Printf("Message sent %v", message);
	}
}