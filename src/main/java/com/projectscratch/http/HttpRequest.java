package com.projectscratch.http;

import java.util.HashMap;
import java.util.Set;

public class HttpRequest extends HttpMessage{
    private HttpMethod method;

    private String requestTarget;
    private String orignalHttpVersion;
    private HttpVersion bestCompatibleHttpVersion;
    private HashMap<String, String> headers = new HashMap<>();
    HttpRequest(){

    }
    public HttpMethod getMethod() {
        return method;
    }

     void setMethod(String methodName) throws HttpParsingException {
        for(HttpMethod httpMethod : HttpMethod.values()){
            if(methodName.equals(httpMethod.name())){
                this.method = httpMethod;
                return;
            }
        }
        throw new HttpParsingException(
                HttpStatusCodes.SERVER_ERROR_501_NOT_IMPLEMENTED
        );
    }
    public String getRequestTarget() {
        return requestTarget;
    }
    public HttpVersion getBestCompatibleHttpVersion(){
        return bestCompatibleHttpVersion;
    }
    public String getOrignalHttpVersion(){
        return orignalHttpVersion;
    }
    public String getHeader(String headerName){
        return headers.get(headerName.toLowerCase());
    }
    public Set<String> getHeaderNames(){
        return headers.keySet();
    }

    void setRequestTarget(String requestTarget) throws HttpParsingException {
        if(requestTarget == null || requestTarget.length() ==0) throw new HttpParsingException(HttpStatusCodes.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        this.requestTarget = requestTarget;
    }
    void setHttpVersion(String inputHttpVersion) throws BadHttpVersionException, HttpParsingException {
        this.orignalHttpVersion = inputHttpVersion;
        this.bestCompatibleHttpVersion = HttpVersion.getBestCompatibleVersion(orignalHttpVersion);
        if(this.bestCompatibleHttpVersion == null) throw new HttpParsingException(
                HttpStatusCodes.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED
        );
    }
    void addHeaders(String headerName, String headerField){
        headers.put(headerName.toLowerCase(), headerField);
    }
}
