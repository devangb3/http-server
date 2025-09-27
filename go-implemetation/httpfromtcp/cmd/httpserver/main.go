package main

import (
	"crypto/sha256"
	"fmt"
	"httpfromtcp/httpfromtcp/internal/headers"
	"httpfromtcp/httpfromtcp/internal/request"
	"httpfromtcp/httpfromtcp/internal/response"
	"httpfromtcp/httpfromtcp/internal/server"
	"log"
	"net/http"
	"os"
	"os/signal"
	"strings"
	"syscall"
)
const port = 42069
func respond400() []byte{
	return []byte(`<html>
		<head>
			<title>400 Bad Request</title>
		</head>
		<body>
			<h1>Bad Request</h1>
			<p>Your request honestly kinda sucked.</p>
		</body>
		</html>`);
}
func respond500() []byte{
	return []byte(`<html>
		<head>
			<title>500 Internal Server Error</title>
		</head>
		<body>
			<h1>Internal Server Error</h1>
			<p>Okay, you know what? This one is on me.</p>
		</body>
		</html>`);
}
func respond200() []byte{
	return []byte(`<html>
		<head>
			<title>200 OK</title>
		</head>
		<body>
			<h1>Success!</h1>
			<p>Your request was an absolute banger.</p>
		</body>
		</html>`);
}
func toStr(bytes []byte) string{
	out := "";
	for _, b := range bytes{
		out += fmt.Sprintf("%02x", b)
	}
	return out;
}

func main() {
	s, err := server.Serve(port, func(w *response.Writer, req *request.Request){
		h := response.GetDefaultHeaders(0);
		body := respond200();
		status := response.StatusOK;

		if req.RequestLine.RequestTarget == "/yourproblem"{
			body = respond400();
			status = response.StatusBadRequest;
			
		}else if req.RequestLine.RequestTarget == "/myproblem"{
			body = respond500();
			status = response.StatusInternalServerError;
		}else if req.RequestLine.RequestTarget == "/video"{
			f, _ := os.ReadFile("assets/vim.mp4")
			h.Replace("content-length", fmt.Sprintf("%d", len(f)));
			h.Replace("content-type", "video/mp4")

			w.WriteStatusLine(response.StatusOK);
			w.WriteHeaders(*h);
			w.WriteBody(f);
			
		}else if strings.HasPrefix(req.RequestLine.RequestTarget, "/httpbin/"){
			target := req.RequestLine.RequestTarget;
			res, err := http.Get("https://httpbin.org/" + target[len("/httpbin/"):])
			if err != nil{
				body = respond500();
				status = response.StatusInternalServerError;
			}else{
				fullBody := []byte{};
				for{
					w.WriteStatusLine(status);

					h.Delete("content-Length");
					h.Put("transfer-encoding", "chunked");
					h.Replace("content-type", "text/plain")
					h.Put("Trailer", "X-Content-SHA256")
					h.Put("Trailer", "X-Content-Length")
					w.WriteHeaders(*h);

					for{
						data := make([]byte, 32);
						n, err := res.Body.Read(data);
						if err != nil{
							break;
						}
						fullBody = append(fullBody, data[:n]...);
						w.WriteBody([]byte(fmt.Sprintf("%x\r\n", n)));
						w.WriteBody(data[:n]);
						w.WriteBody([]byte("\r\n"));
					}
					w.WriteBody([]byte("0\r\n"));
					trailers := headers.NewHeaders();
					out := sha256.Sum256(fullBody);
					trailers.Put("X-Content-SHA256", toStr(out[:]))
					trailers.Put("X-Content-Length", fmt.Sprintf("%d", len(fullBody)))
					w.WriteHeaders(*trailers);
					
					return;
				}
			}
		}
		h.Replace("Content-length", fmt.Sprintf("%d",len(body)))
		h.Replace("Content-type", "text/html")
		w.WriteStatusLine(status);
		w.WriteHeaders(*h);
		w.WriteBody(body);
		
	})
	if err != nil {
		log.Fatalf("Error starting server: %v", err)
	}
	defer s.Close()
	log.Println("Server started on port", port)

	sigChan := make(chan os.Signal, 1)
	signal.Notify(sigChan, syscall.SIGINT, syscall.SIGTERM)
	<-sigChan
	log.Println("Server gracefully stopped")
}