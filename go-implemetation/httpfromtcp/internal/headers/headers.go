package headers

import (
	"bytes"
	"fmt"
	"strings"
)
type Headers struct{
	headers map[string]string
}

var rn = []byte("\r\n")
func NewHeaders() *Headers{
	return &Headers{
		map[string]string{},
	}
}
func (h Headers) Get(name string) string{
	return h.headers[strings.ToLower(name)];
}
func (h Headers) Put(key string, value string){
	key = strings.ToLower(key);
	if v,ok := h.headers[key]; ok{
		h.headers[key] = fmt.Sprintf("%s,%s", v, value)
	}else{
		h.headers[key] = value;
	}
}
func (h *Headers) ForEach(cb func(n, v string)){
	 for n,v := range h.headers{
		cb(n,v);
	 }
}
func isToken(str []byte) bool {
	for _, ch := range str {
		if (ch >= 'a' && ch <= 'z') ||
			(ch >= 'A' && ch <= 'Z') ||
			(ch >= '0' && ch <= '9') ||
			ch == '#' || ch == '$' || ch == '%' || ch == '&' || ch == '\'' ||
			ch == '*' || ch == '+' || ch == '-' || ch == '.' || ch == '^' ||
			ch == '_' || ch == '`' || ch == '|' || ch == '~' {
			continue
		}
		return false
	}
	return true
}
func parseHeader(fieldLine []byte) (string, string, error) {
	parts := bytes.SplitN(fieldLine, []byte(":"), 2);
	if len(parts) != 2{
		return "", "", fmt.Errorf("malformed field line");
	}
	name := parts[0];
	value := bytes.TrimSpace(parts[1]);

	if bytes.HasSuffix(name, []byte(" ")){
		return "", "", fmt.Errorf("malformed field name");
	}

	return string(name), string(value), nil;
}
func(h Headers) Parse(data[] byte)(int, bool, error){
	read := 0;
	done := false;
	for{
		idx := bytes.Index(data[read:], rn);
		if idx == -1{
			break
		}
		if idx == 0{
			done = true;
			read += len(rn);
			break;
		}

		name,value, err := parseHeader(data[read:read+idx]);
		if err != nil{ 
			return 0, done, err;
		}
		if(!isToken([]byte(name))){
			return 0, false, fmt.Errorf("invalid token present in field name")
		}
		read += idx + len(rn);
		h.Put(name, value)

	}
	return read,done,nil;
}