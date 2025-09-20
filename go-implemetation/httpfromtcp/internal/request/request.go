package request

import (
	"bytes"
	"fmt"
	"io"
)
type ParserStatus string;
const (
	Initialized ParserStatus = "init"
	Done ParserStatus = "done"
	Error ParserStatus = "error"
)
type Request struct {
    RequestLine RequestLine
	ParserStatus ParserStatus
}
type RequestLine struct {
	HttpVersion   string
	RequestTarget string
	Method        string
}
var ErroMalformedRequestLine= fmt.Errorf("malformed request Line")
var ErrorUnsupportedHttpVersion = fmt.Errorf("http version is not supported")
var ErrorInvalidParserStatus = fmt.Errorf("trying to read data in done state")
var SEPERATOR = []byte("\r\n");

func newRequest() *Request{
	return &Request{
		ParserStatus: Initialized,
	}
}
func RequestFromReader(reader io.Reader) (*Request, error){
	req := newRequest()
	buf := make([]byte, 1024)
	bufLen := 0;
	for !req.isDone(){
		n,err := reader.Read(buf[bufLen:])
		if err != nil{
			return nil, err;
		}
		bufLen += n;
		readN, err := req.parse(buf[:bufLen+n])
		if err != nil{
			return nil, err;
		}
		copy(buf, buf[readN:bufLen]);
		bufLen -= readN;
	}
	return req, nil;

}

func parseRequestLine(s []byte) (*RequestLine, int, error){
	idx := bytes.Index(s, SEPERATOR);
	if idx == -1 {
		return nil, 0, nil;
	}
	startLine := s[:idx];
	read := idx+len(SEPERATOR)
	parts := bytes.Split(startLine, []byte(" "));
	if len(parts) != 3{
		return nil, 0, ErroMalformedRequestLine;
	}
	httpParts := bytes.Split(parts[2], []byte("/") )
	if(len(httpParts) != 2 || string(httpParts[0]) != "HTTP" || string(httpParts[1]) != "1.1"){
		return nil, read, ErroMalformedRequestLine;
	}
	rl := &RequestLine{
		Method: string(parts[0]),
		RequestTarget: string(parts[1]),
		HttpVersion: string(httpParts[1]),
	}

	return rl, read, nil;
}
func (r *Request) isDone() bool{
	return r.ParserStatus == Done || r.ParserStatus == Error;
}

func (r *Request) parse(data []byte) (int, error){
	read := 0;
outer:
	for{
		switch r.ParserStatus {
		case Error:
			return 0, ErrorInvalidParserStatus
		case Initialized:
			rl,n, err := parseRequestLine(data[read:])
			if err != nil{
				r.ParserStatus = Error;
				return 0, err;
			}
			if n==0{
				break outer
			}
			r.RequestLine = *rl;
			read += n;
			r.ParserStatus = Done;
		case Done:
			break outer
		}
	}
	return read, nil;
}



