package com.projectscratch.httpserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread{
    private Socket socket;
    public HttpConnectionWorkerThread(Socket socket){
        this.socket = socket;
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);
    InputStream inputStream = null;
    OutputStream outputStream = null;
    @Override
    public void run(){
        try {

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            final String CRLF = "\n\r";
            String html = "<html><head><title>Simple HTTP Server </title></head><body><h1> test </h1></body></html>";
            String response =
                    "HTTP/1.1 200 OK" + CRLF +
                            "Content-Length: "+ html.getBytes().length + CRLF +
                            CRLF +
                            html +
                            CRLF + CRLF;
            outputStream.write(response.getBytes());
            LOGGER.info("Request Processed success");

        } catch (IOException e) {
            LOGGER.error("Something went wrong in Worker Thread", e);
        }
        finally {
            if(inputStream != null){
                try{
                    inputStream.close();
                }
                catch(Exception ignored){}
            }
            if(outputStream != null){
                try{
                    outputStream.close();
                }
                catch (Exception ignored){}
            }
            if(socket != null){
                try{
                    socket.close();
                }
                catch(Exception ignored){}
            }

        }
    }
}
