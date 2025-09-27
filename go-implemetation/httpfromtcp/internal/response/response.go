package response

import (
	"fmt"
	"httpfromtcp/httpfromtcp/internal/headers"
	"io"
)

type StatusCode int;

type Response struct{
}
type Writer struct{
	writer io.Writer
}

func NewWriter(writer io.Writer) *Writer{
	return &Writer{writer: writer}
}
const(
	StatusOK StatusCode = 200
	StatusBadRequest StatusCode = 400
	StatusInternalServerError StatusCode = 500
)

func GetDefaultHeaders(contentLen int) *headers.Headers{
	h := headers.NewHeaders();

	h.Put("Content-Length", fmt.Sprintf("%d", contentLen));
	h.Put("Connection", "close");
	h.Put("Content-Type", "text/plain");

	return h;
}
func (w *Writer) WriteStatusLine(statusCode StatusCode) error{
	var statusLine []byte;
	switch statusCode{
		case StatusOK: statusLine = []byte("HTTP/1.1 200 OK\r\n")
		case StatusBadRequest: statusLine = []byte("HTTP/1.1 400 Bad Request\r\n")
		case StatusInternalServerError: statusLine = []byte("HTTP/1.1 500 Internal Server Error\r\n")
	default:
		return fmt.Errorf("unrecognized error code")
	}
	_, err := w.writer.Write(statusLine);
	return err;
}
func (w *Writer) WriteHeaders(h headers.Headers) error{
	b := []byte{};
	h.ForEach(func(n,v string){
		b = fmt.Appendf(b, "%s: %s\r\n", n, v);
	})
	b = fmt.Append(b, "\r\n");
	_, err := w.writer.Write(b); 
	return err;
}
func (w *Writer) WriteBody(p []byte) (int, error){
	n, err := w.writer.Write(p);
	return n, err;
}