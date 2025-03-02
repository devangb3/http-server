package com.projectscratch.http;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpParserTest {
    private HttpParser httpParser;
    @BeforeAll
    public void beforeClass(){
        httpParser = new HttpParser();
    }
    @Test
    void parseHttpRequest() {
        HttpRequest request = null;
        try {
            request = httpParser.parseHttpRequest(generateValidGETTestCase());
        } catch (HttpParsingException | IOException e) {
            fail(e);
        }
        assertNotNull(request);
        assertEquals(request.getMethod(), HttpMethod.GET);
        assertEquals(request.getRequestTarget(), "/");
        assertEquals(request.getOrignalHttpVersion(), "HTTP/1.1");
        assertEquals(request.getBestCompatibleHttpVersion(), HttpVersion.HTTP_1_1);
    }
    @Test
    void parseHttpBadMethodNameRequest1() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(generateBadMethodNameTestCase1());
            fail("No err thrown");
        }

        catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCodes.SERVER_ERROR_501_NOT_IMPLEMENTED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    void parseHttpBadMethodNameRequest2() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(generateBadMethodNameTestCase2());
            fail("No err thrown");
        }

        catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCodes.SERVER_ERROR_501_NOT_IMPLEMENTED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    void  parseHttpBadTestInvalidNumberOfItems() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(generateBadTestInvalidNumberOfItems());
            fail("No err thrown");
        }

        catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCodes.CLIENT_ERROR_400_BAD_REQUEST);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Test
    void  parseHttpBadTestEmptyNumberOfItems() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(generateBadTestEmptyNumberOfItems());
            fail("No err thrown");
        }
        catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCodes.CLIENT_ERROR_400_BAD_REQUEST);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    void  parseHttpBadTestOnlyCRNoLF() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(generateBadTestOnlyCRNoLF());
            fail("No err thrown");
        }
        catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCodes.CLIENT_ERROR_400_BAD_REQUEST);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    void parseBadHTTPVersionRequest() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(generateBadHTTPVersionTestCase());
            fail("No err thrown");
        }

        catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCodes.CLIENT_ERROR_400_BAD_REQUEST);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    void parseUnsupportedHTTPVersionRequest() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(generateUnsupportedHTTPVersionTestCase());
            fail("No err thrown");
        }

        catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCodes.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    void parseSupportedHTTPVersionRequest() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(generateSupportedHTTPVersionTestCase());
            assertNotNull(request);
            assertEquals(request.getBestCompatibleHttpVersion(), HttpVersion.HTTP_1_1);
            assertEquals(request.getOrignalHttpVersion(), "HTTP/1.2");
        }

        catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCodes.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private InputStream generateValidGETTestCase(){
        String rawValidData = "GET / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Not(A:Brand\";v=\"99\", \"Google Chrome\";v=\"133\", \"Chromium\";v=\"133\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "sec-ch-ua-platform: \"Windows\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en-US,en;q=0.9\r\n" + "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawValidData.getBytes(
                        StandardCharsets.US_ASCII
                )
        );
        return inputStream;
    }
    private InputStream generateBadMethodNameTestCase1(){
        String rawValidData = "GeT / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n"
               ;

        InputStream inputStream = new ByteArrayInputStream(
                rawValidData.getBytes(
                        StandardCharsets.US_ASCII
                )
        );
        return inputStream;
    }
    private InputStream generateBadMethodNameTestCase2(){
        String rawValidData = "GeTABC / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n"
                ;

        InputStream inputStream = new ByteArrayInputStream(
                rawValidData.getBytes(
                        StandardCharsets.US_ASCII
                )
        );
        return inputStream;
    }
    private InputStream generateBadTestInvalidNumberOfItems(){
        String rawValidData = "GET / AAAAAAAAA HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n"
                ;

        InputStream inputStream = new ByteArrayInputStream(
                rawValidData.getBytes(
                        StandardCharsets.US_ASCII
                )
        );
        return inputStream;
    }
    private InputStream generateBadTestEmptyNumberOfItems(){
        String rawValidData = "\r\n" +
                "Host: localhost:8080\r\n"
                ;

        InputStream inputStream = new ByteArrayInputStream(
                rawValidData.getBytes(
                        StandardCharsets.US_ASCII
                )
        );
        return inputStream;
    }
    private InputStream generateBadTestOnlyCRNoLF(){
        String rawValidData = "GET / HTTP/1.1\r" +
                "Host: localhost:8080\r\n"
                ;

        InputStream inputStream = new ByteArrayInputStream(
                rawValidData.getBytes(
                        StandardCharsets.US_ASCII
                )
        );
        return inputStream;
    }
    private InputStream generateBadHTTPVersionTestCase(){
        String rawValidData = "GET / HTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Not(A:Brand\";v=\"99\", \"Google Chrome\";v=\"133\", \"Chromium\";v=\"133\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "sec-ch-ua-platform: \"Windows\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en-US,en;q=0.9\r\n" + "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawValidData.getBytes(
                        StandardCharsets.US_ASCII
                )
        );
        return inputStream;
    }

    private InputStream generateUnsupportedHTTPVersionTestCase(){
        String rawValidData = "GET / HTTP/2.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Not(A:Brand\";v=\"99\", \"Google Chrome\";v=\"133\", \"Chromium\";v=\"133\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "sec-ch-ua-platform: \"Windows\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en-US,en;q=0.9\r\n" + "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawValidData.getBytes(
                        StandardCharsets.US_ASCII
                )
        );
        return inputStream;
    }
    private InputStream generateSupportedHTTPVersionTestCase(){
        String rawValidData = "GET / HTTP/1.2\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Not(A:Brand\";v=\"99\", \"Google Chrome\";v=\"133\", \"Chromium\";v=\"133\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "sec-ch-ua-platform: \"Windows\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en-US,en;q=0.9\r\n" + "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawValidData.getBytes(
                        StandardCharsets.US_ASCII
                )
        );
        return inputStream;
    }
}