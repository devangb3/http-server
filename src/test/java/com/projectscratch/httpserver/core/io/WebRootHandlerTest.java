package com.projectscratch.httpserver.core.io;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WebRootHandlerTest {
    WebRootHandler webRootHandler;
    private Method checkIfEndsWithSlashMethod;
    private Method checkIfProvidedRelativePathExistsMethod;

    @BeforeAll
    public void beforeClass() throws WebRootNotFoundException, NoSuchMethodException {
        webRootHandler = new WebRootHandler("WebRoot");
        Class<WebRootHandler> cls = WebRootHandler.class;
        checkIfEndsWithSlashMethod = cls.getDeclaredMethod("checkIfEndsWithSlash", String.class);
        checkIfEndsWithSlashMethod.setAccessible(true);
        checkIfProvidedRelativePathExistsMethod = cls.getDeclaredMethod("checkIfProvidedRelativePathExists", String.class);
        checkIfProvidedRelativePathExistsMethod.setAccessible(true);
    }
    @Test
    void ConstructorGoodPath(){
        try{
            WebRootHandler webRootHandler = new WebRootHandler("D:\\Projects\\HTTPScratch\\WebRoot");
        } catch (WebRootNotFoundException e) {
            fail(e);
        }
    }
    @Test
    void ConstructorBadPath(){
        try{
            WebRootHandler webRootHandler = new WebRootHandler("D:\\Projects\\HTTPScratch\\WebRoot2");
            fail();
        } catch (WebRootNotFoundException e) {

        }
    }
    @Test
    void ConstructorGoodPath2(){
        try{
            WebRootHandler webRootHandler = new WebRootHandler("WebRoot");

        } catch (WebRootNotFoundException e) {
            fail();
        }
    }
    @Test
    void ConstructorBadPath2(){
        try{
            WebRootHandler webRootHandler = new WebRootHandler("WebRoot2");
            fail();
        } catch (WebRootNotFoundException e) {

        }
    }
    @Test
    void checkIfEndsWithSlashMethodFalse(){
        try{
            boolean result = (Boolean) checkIfEndsWithSlashMethod.invoke(webRootHandler,"index.html");
            assertFalse(result);
        }
        catch(IllegalAccessException e){
            fail();
        }
        catch (InvocationTargetException e){
            fail(e);
        }
    }
    @Test
    void checkIfEndsWithSlashMethodFalse2(){
        try{
            boolean result = (Boolean) checkIfEndsWithSlashMethod.invoke(webRootHandler,"/index.html");
            assertFalse(result);
        }
        catch(IllegalAccessException e){
            fail();
        }
        catch (InvocationTargetException e){
            fail(e);
        }
    }
    @Test
    void checkIfEndsWithSlashMethodFalse3(){
        try{
            boolean result = (Boolean) checkIfEndsWithSlashMethod.invoke(webRootHandler,"/home/index.html");
            assertFalse(result);
        }
        catch(IllegalAccessException e){
            fail();
        }
        catch (InvocationTargetException e){
            fail(e);
        }
    }
    @Test
    void checkIfEndsWithSlashMethodTrue(){
        try{
            boolean result = (Boolean) checkIfEndsWithSlashMethod.invoke(webRootHandler,"/home/index.html/");
            assertTrue(result);
        }
        catch(IllegalAccessException e){
            fail();
        }
        catch (InvocationTargetException e){
            fail(e);
        }
    }
    @Test
    void checkIfEndsWithSlashMethodTrue2(){
        try{
            boolean result = (Boolean) checkIfEndsWithSlashMethod.invoke(webRootHandler,"/");
            assertTrue(result);
        }
        catch(IllegalAccessException e){
            fail();
        }
        catch (InvocationTargetException e){
            fail(e);
        }
    }

    @Test
    void testWebRootFilePathExistsGood(){
        try {
            boolean result = (boolean) checkIfProvidedRelativePathExistsMethod.invoke(webRootHandler, "/index.html");
            assertTrue(result);
        } catch (IllegalAccessException e) {
            fail();
        } catch (InvocationTargetException e) {
            fail();
        }
    }
    @Test
    void testWebRootFilePathExistsGood2(){
        try {
            boolean result = (boolean) checkIfProvidedRelativePathExistsMethod.invoke(webRootHandler, "/./././index.html");
            assertTrue(result);
        } catch (IllegalAccessException e) {
            fail();
        } catch (InvocationTargetException e) {
            fail();
        }
    }
    @Test
    void testWebRootFilePathDoesNotExist(){
        try {
            boolean result = (boolean) checkIfProvidedRelativePathExistsMethod.invoke(webRootHandler, "index1.html");
            assertFalse(result);
        } catch (IllegalAccessException e) {
            fail();
        } catch (InvocationTargetException e) {
            fail();
        }
    }
    @Test
    void testWebRootFilePathDoesNotExist1(){
        try {
            boolean result = (boolean) checkIfProvidedRelativePathExistsMethod.invoke(webRootHandler, "/../LICENSE");
            assertFalse(result);
        } catch (IllegalAccessException e) {
            fail();
        } catch (InvocationTargetException e) {
            fail();
        }
    }
    @Test
    void testGetFileMimeTypeText(){
        try{
            String mimeType = webRootHandler.getFileMimeType("/");
            assertEquals("text/html", mimeType);
        }
        catch(FileNotFoundException e){
            fail(e);
        }
    }
    @Test
    void testGetFileMimeTypePng(){
        try{
            String mimeType = webRootHandler.getFileMimeType("/DevangPhoto.JPG");
            assertEquals("image/jpeg", mimeType);
        }
        catch(FileNotFoundException e){
            fail(e);
        }
    }
    @Test
    void testGetFileMimeTypeDefault(){
        try{
            String mimeType = webRootHandler.getFileMimeType("/favicon.ico");
            assertEquals("application/octet-stream", mimeType);
        }
        catch(FileNotFoundException e){
            fail(e);
        }
    }
    @Test
    void testGetFileByteArrayData(){
        try {
            assertTrue(webRootHandler.getFileByteArrayData("/").length > 0);
        } catch (FileNotFoundException e) {
            fail();
        } catch (ReadFileException e) {
            fail();
        }
    }
    @Test
    void testGetFileByteArrayDataFileAbsent(){
        try {
            webRootHandler.getFileByteArrayData("/test.html");
            fail();
        } catch (FileNotFoundException e) {

        } catch (ReadFileException e) {
            fail();
        }
    }
}