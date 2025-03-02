package com.projectscratch.httpserver.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;

public class WebRootHandler {

    private File webroot;
    public WebRootHandler(String webRootPath) throws WebRootNotFoundException {
        webroot = new File(webRootPath);
        if(!webroot.exists() || !webroot.isDirectory()){
            throw new WebRootNotFoundException("Webroot  provided does not exist or is not a folder");
        }
    }
    private boolean checkIfEndsWithSlash(String relativePath){
        return relativePath.endsWith("/");
    }
    private boolean checkIfProvidedRelativePathExists(String relativePath){
        File file = new File(webroot, relativePath);
        if(!file.exists()) return false;
        try{
            if(file.getCanonicalPath().startsWith(webroot.getCanonicalPath())) return true;
        } catch (IOException e) {
            return false;
        }
        return false;
    }
    public String getFileMimeType(String relativePath) throws FileNotFoundException {
        if(checkIfEndsWithSlash(relativePath)){
            relativePath += "index.html";
        }
        if(!checkIfProvidedRelativePathExists(relativePath)){
            throw new FileNotFoundException("File not found" + relativePath);
        }

        File file = new File(webroot, relativePath);
        String mimeType = URLConnection.getFileNameMap().getContentTypeFor(file.getName());
        if(mimeType == null) return "application/octet-stream";
        return mimeType;
    }
    public byte[] getFileByteArrayData(String relativePath) throws FileNotFoundException,ReadFileException{
        if(checkIfEndsWithSlash(relativePath)){
            relativePath += "index.html";
        }
        if(!checkIfProvidedRelativePathExists(relativePath)){
            throw new FileNotFoundException("File not found" + relativePath);
        }
        File file = new File(webroot, relativePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileBytes = new byte[(int)file.length()];
        try{
            fileInputStream.read(fileBytes);
            fileInputStream.close();
        }
        catch(IOException e){
            throw new ReadFileException(e);
        }
        return fileBytes;
    }

}
