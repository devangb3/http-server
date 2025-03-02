package com.projectscratch.http;

public enum HttpStatusCodes {
    CLIENT_ERROR_400_BAD_REQUEST(400, "Bad Request"),
    CLIENT_ERROR_405_METHOD_NOT_ALLOWED(405, "Method not Allowed"),
    CLIENT_ERROR_414_URI_TOO_LONG(414, "URI Too Long"),
    SERVER_ERROR_500_INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SERVER_ERROR_501_NOT_IMPLEMENTED(501, "Not Implemented"),
    SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED(505, "HTTP VERSION NOT SUPPORTED");

    public final int STATUS_CODE;
    public final String Message;

    HttpStatusCodes(int STATUS_CODE, String Message){
        this.STATUS_CODE = STATUS_CODE;
        this.Message = Message;
    }
}
