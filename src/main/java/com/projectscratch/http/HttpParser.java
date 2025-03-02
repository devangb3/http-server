package com.projectscratch.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParser {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);

    private static final int CR = 0x0D;
    private static final int SP = 0x20;
    private static final int LF = 0x0A ;

    public HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException, IOException {
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
        HttpRequest request = new HttpRequest();

        parseRequestLine(reader,request);
        parseHeaders(reader,request);
        parseBody(reader,request);

        return request;
    }
    private void parseRequestLine(InputStreamReader inputStreamReader, HttpRequest request) throws IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder();
        boolean methodParsed = false;
        boolean requestTargetParsed = false;
        int _byte;
        while((_byte = inputStreamReader.read()) >= 0){
            if(_byte == CR){
                _byte = inputStreamReader.read();
                if(_byte == LF){
                    LOGGER.debug("Request Line VERSION to Process : {}", processingDataBuffer.toString());
                    if(!methodParsed || !requestTargetParsed) throw new HttpParsingException(HttpStatusCodes.CLIENT_ERROR_400_BAD_REQUEST);
                    try{
                        request.setHttpVersion(processingDataBuffer.toString());
                    }
                    catch(BadHttpVersionException e){
                        throw new HttpParsingException(HttpStatusCodes.CLIENT_ERROR_400_BAD_REQUEST);
                    }
                    return;
                }
                else {
                    throw new HttpParsingException(HttpStatusCodes.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }
            if(_byte == SP){
                if(!methodParsed){
                    LOGGER.debug("Request Line Method to Process : {}", processingDataBuffer.toString());
                    request.setMethod(processingDataBuffer.toString());
                    methodParsed = true;
                }
                else if(!requestTargetParsed){
                    LOGGER.debug("Request Request Target to Process : {}", processingDataBuffer.toString());
                    request.setRequestTarget(processingDataBuffer.toString());
                    requestTargetParsed = true;
                }
                else{
                    throw new HttpParsingException(HttpStatusCodes.CLIENT_ERROR_400_BAD_REQUEST);
                }
                processingDataBuffer.delete(0, processingDataBuffer.length());
            }
            else{
                processingDataBuffer.append((char)_byte);
                if(!methodParsed){
                    if(processingDataBuffer.length() > HttpMethod.MAX_LENGTH){
                        throw new HttpParsingException(HttpStatusCodes.SERVER_ERROR_501_NOT_IMPLEMENTED);
                    }
                }
            }
        }
    }
    private void parseHeaders(InputStreamReader inputStreamReader, HttpRequest request) throws IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder();
        boolean crlfFound = false;

        int _byte;
        while ((_byte = inputStreamReader.read()) >= 0) {
            if (_byte == CR) {
                _byte = inputStreamReader.read();
                if (_byte == LF) {
                    if (crlfFound) {
                        return;
                    }
                    processingSingleHeaderField(processingDataBuffer, request);
                    processingDataBuffer.setLength(0);
                    crlfFound = true;
                } else {
                    throw new HttpParsingException(HttpStatusCodes.CLIENT_ERROR_400_BAD_REQUEST);
                }
            } else {
                processingDataBuffer.append((char) _byte);
                crlfFound = false;
            }
        }

        if (processingDataBuffer.length() > 0) {
            processingSingleHeaderField(processingDataBuffer, request);
        }
    }
    private void processingSingleHeaderField(StringBuilder processingDataBuffer, HttpRequest request) throws HttpParsingException {
        String rawHeaderField = processingDataBuffer.toString();

        // Updated regex to allow spaces and more complex field values
        Pattern pattern = Pattern.compile(
                "^(?<fieldName>[!#$%&'*+\\-./^_`|~\\w]+):\\s*(?<fieldValue>.+?)\\s*$"
        );

        Matcher matcher = pattern.matcher(rawHeaderField);

        if (matcher.matches()) {
            String fieldName = matcher.group("fieldName");
            String fieldValue = matcher.group("fieldValue");
            request.addHeaders(fieldName, fieldValue.trim());
        } else {
            throw new HttpParsingException(HttpStatusCodes.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }
    private void parseBody(InputStreamReader inputStreamReader, HttpRequest request){

    }
}
