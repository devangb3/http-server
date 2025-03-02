package com.projectscratch.httpserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListenerThread extends Thread{
    private int port;
    private String webRoot;
    private ServerSocket serverSocket;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);
    public ServerListenerThread(int port, String webRoot) throws IOException {
        this.port = port;
        this.webRoot = webRoot;
        this.serverSocket = new ServerSocket(this.port);
    }
    @Override
    public void run(){

            try{
                while(!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    LOGGER.info(" * Connection Accepted " + socket.getInetAddress());

                    HttpConnectionWorkerThread workerThread = new HttpConnectionWorkerThread(socket);
                    workerThread.start();
                }
            }
            catch(Exception e){
                LOGGER.error("Something went wrong in Thread ... " + e.toString());
            }
            finally{
                if(serverSocket != null){
                    try {
                        serverSocket.close();
                    }
                    catch(Exception ignored){}
                }
            }


    }

}
