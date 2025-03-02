package com.projectscratch.httpserver.core.io;

import java.io.IOException;

public class ReadFileException extends Throwable {
    public ReadFileException(IOException e) {
    }
    public ReadFileException(String message){
        super(message);
    }
}
