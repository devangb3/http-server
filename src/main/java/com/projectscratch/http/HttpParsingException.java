package com.projectscratch.http;

public class HttpParsingException  extends Exception{
    private final HttpStatusCodes errorCode;

    public HttpParsingException(HttpStatusCodes errorCode) {
        super(errorCode.Message);
        this.errorCode = errorCode;
    }
    public HttpStatusCodes getErrorCode(){
        return errorCode;
    }


}
