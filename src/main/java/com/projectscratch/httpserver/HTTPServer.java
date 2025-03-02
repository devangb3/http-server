package com.projectscratch.httpserver;

import com.projectscratch.httpserver.config.Configuration;
import com.projectscratch.httpserver.config.ConfigurationManager;
import com.projectscratch.httpserver.core.ServerListenerThread;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPServer {
    private final static Logger LOGGER=  LoggerFactory.getLogger(HTTPServer.class);
    public static void main(String[] args) {
        LOGGER.info("Server Starting ...");

        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();


        LOGGER.info("Using port ... "+ conf.getPort());
        LOGGER.info("Using web root ... "+ conf.getWebRoot());

        try {
            ServerListenerThread serverListenerThread = new ServerListenerThread(conf.getPort(), conf.getWebRoot());
            serverListenerThread.start();
        } catch (IOException e) {
            LOGGER.error("Something went wrong in server ..." + e);
        }
    }
}
