package request

import (
	"bytes"
	"fmt"
	"httpfromtcp/httpfromtcp/internal/headers"
	"io"
	"strconv"
)
type ParserStatus string;
const (
	Initialized ParserStatus = "init"
	Done ParserStatus = "done"
	ParsingHeader ParserStatus ="parsing headers"
	ParsingBody ParserStatus = "parsing body"
	Error ParserStatus = "error"
)
type Request struct {
    RequestLine RequestLine
	Headers *headers.Headers
	ParserStatus ParserStatus
	Body string
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
		Headers: headers.NewHeaders(),
	}
}
func getInt(headers *headers.Headers, name string, defaultValue int) int{
	valueStr, exists := headers.Get(name);
	if !exists{
		return defaultValue;
	}
	value, err := strconv.Atoi(valueStr);
	if err != nil{
		return defaultValue;
	}
	return value;
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
		readN, err := req.parse(buf[:bufLen])
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
func( r* Request) hasBody() bool{
	length := getInt(r.Headers, "content-length", 0);
	return length > 0;
}
func (r *Request) parse(data []byte) (int, error){
	read := 0;
outer:
	for{
		currentData := data[read:];
		switch r.ParserStatus {
		case Error:
			return 0, ErrorInvalidParserStatus
		case Initialized:
			rl,n, err := parseRequestLine(currentData)
			if err != nil{
				r.ParserStatus = Error;
				return 0, err;
			}
			if n==0{
				break outer
			}
			r.RequestLine = *rl;
			read += n;
			r.ParserStatus = ParsingHeader;
		case ParsingHeader:
			n, done, err := r.Headers.Parse(currentData);
			if err != nil{
				r.ParserStatus = Error;
				return 0, err;
			}
			if n == 0{
				break outer;
			}
			read += n;
			if done{
				if r.hasBody(){
					r.ParserStatus = ParsingBody;
				}else{
					r.ParserStatus = Done;
				}
			}
		case ParsingBody:
			length := getInt(r.Headers, "Content-Length", 0);
			if length == 0{
				panic("Chunked not implemented")
			}

			remaining := min(length - len(r.Body), len(currentData))
			r.Body += string(currentData[:remaining])
			read += remaining;

			if(len(r.Body) == length){
				r.ParserStatus = Done
			}
			if len(r.Body) > length{
				r.ParserStatus = Error;
				return 0, fmt.Errorf("length of Body is more than Content-Length");
			}
			return read,nil;
		case Done:
			break outer
		default:
			panic("Something went wrong!")
		}
	}
	return read, nil;
}



