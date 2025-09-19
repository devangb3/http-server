package request

import (
	"fmt"
	"io"
	"strings"
	"errors"
)
type Request struct {
    RequestLine RequestLine
}
type RequestLine struct {
	HttpVersion   string
	RequestTarget string
	Method        string
}
var ERROR_MALFORMED_REQUEST_LINE = fmt.Errorf("Malformed Request Line")
var ERROR_UNSUPPORTED_HTTP_VERSION = fmt.Errorf("Http version is not supported")
var SEPERATOR ="\r\n";

func RequestFromReader(reader io.Reader) (*Request, error){
	data, err := io.ReadAll(reader);
	if err != nil{
		return nil, err;
	}
	
	if err != nil{
		return nil, errors.Join(
			fmt.Errorf("Unable to io.ReadAll"),
			err,
		)
	}

	str := string(data);
	rl, _, err := parseRequestLine(str);
	if err != nil{
		return nil, err;
	}
	return &Request{RequestLine: *rl,}, err;

}

func parseRequestLine(s string) (*RequestLine, string, error){
	idx := strings.Index(s, SEPERATOR);
	if idx == -1 {
		return nil, s, nil;
	}
	startLine := s[:idx];
	restOfMsg := s[idx+len(SEPERATOR):]
	parts := strings.Split(startLine, " ");
	if len(parts) != 3{
		return nil, restOfMsg, ERROR_MALFORMED_REQUEST_LINE;
	}
	httpParts := strings.Split(parts[2], "/")
	if(len(httpParts) != 2 || httpParts[0] != "HTTP" || httpParts[1] != "1.1"){
		return nil, restOfMsg, ERROR_MALFORMED_REQUEST_LINE;
	}
	rl := &RequestLine{
		Method: parts[0],
		RequestTarget: parts[1],
		HttpVersion: httpParts[1],
	}

	return rl, restOfMsg, nil;
}

func(rl *RequestLine) ValidHttp() bool{
	return rl.HttpVersion == "1.1";
}

