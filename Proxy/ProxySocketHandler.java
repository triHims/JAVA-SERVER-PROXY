package Proxy;

import java.io.BufferedInputStream; // import BufferedInputStream this is used to decrease the reads
import java.io.BufferedOutputStream; // Bufffered output stream follows the similar suite
import java.io.ByteArrayOutputStream; // Handy dynamic bytearray
// default ioexception import
import java.io.IOException;
// we are using URL therefore we need MalformedURLException to support it
import java.net.MalformedURLException;
// Import socket to read and write data to socket
import java.net.Socket;
// Socket exception can arise when using the socket
import java.net.SocketException; // This is socketexception
// URL container to parse url easily
import java.net.URL;
 // StandardCharsets to convert he default string to byte array
import java.nio.charset.StandardCharsets;
//default java date class to append dates to the request
import java.util.Date;

public class ProxySocketHandler implements Runnable {

    Socket boundSocket;
    static String READ_EXCEPTION = "Read Exception";
    static String BAD_REQUEST = "Bad Request"; // Bad reqeust const

    static int EOF_CHAR = -1;

    ProxySocketHandler(Socket boundSocket) {
        
        this.boundSocket = boundSocket;
        try {
            boundSocket.setSoTimeout(10000);
        } catch (SocketException ex) {
            System.out.printf("Socket Exception While setting the timeout\n %s\n %s", ex.getCause(), ex.getLocalizedMessage());
        }


    }


    @Override
    public void run() { // This function is first run 
        // socket data is read and and translated
 
        BufferedInputStream clientInputStream = null;
        BufferedOutputStream clientOutputStream = new BufferedOutputStream(null);

        try {
            System.out.println(">  Request Received , Initiating process");
            clientInputStream = new BufferedInputStream(boundSocket.getInputStream());


            clientOutputStream = new BufferedOutputStream(boundSocket.getOutputStream());


            var clientInputBuffer = new ByteArrayOutputStream();

            try {
                int readByte = 0;

                while (readByte != -1) {
                    readByte = clientInputStream.read();
                    clientInputBuffer.write(readByte);
                }
            } catch (Exception ex) {
                throw new RuntimeException(READ_EXCEPTION, ex);
            }

            System.out.println(">  Data read Success");
            boundSocket.shutdownInput();
            clientInputBuffer.close();
            ProxyPayload proxyRequest = buildProxyRequest(clientInputBuffer);

            try (Socket requestSocket = new Socket(proxyRequest.host(), proxyRequest.port())) {
                requestSocket.setSoTimeout(10000);
                var requestProxyInputStream = requestSocket.getInputStream();
                var requestProxyOutputStream = requestSocket.getOutputStream();
                requestProxyOutputStream.write(proxyRequest.payload().getBytes(StandardCharsets.UTF_8));
                requestProxyOutputStream.write(EOF_CHAR);
                requestProxyOutputStream.flush();
                requestSocket.shutdownOutput();

                System.out.println(">  Send Request to remote");


                int readByte = 0;
                System.out.println(">  Waiting for output from remote");
                while (readByte != -1) {
                    readByte = requestProxyInputStream.read();
                    clientOutputStream.write(readByte);
                }
                requestProxyOutputStream.close();
                requestProxyInputStream.close();

                System.out.println(">  Output written to client socket");
            }


        } catch (IOException e) {
            System.out.println("Got exception " + e.getMessage());
            e.printStackTrace();
            if (clientOutputStream != null) {
                try {

                    clientOutputStream.write(get502Header().getBytes(StandardCharsets.UTF_8));
                } catch (IOException ex) {
                    System.out.println("Client output stream exists but panic");
                    System.out.println("> ########### Could not write the exception message ");
                    throw new RuntimeException(ex);
                }
            } else {
                System.out.println("Client output stream does not exist ");
                System.out.println("> ###########  Could not write the exception message ");
                throw new RuntimeException(e);
            }
        }

        try {
            clientOutputStream.write(-1); // Write EOF before closing
            clientOutputStream.flush();
            clientOutputStream.close();
            assert clientInputStream != null;
            clientInputStream.close();
            System.out.println(">  Request Completed Successfully");
        } catch (IOException ex) {
            System.out.println("Got error while writing/closing sockets ");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

    }

    static String req_builder = "GET %s HTTP/1.1\r\n" +
                                "Host: %s\r\n" +
                                "User-Agent: curl/7.85.0\r\n" +
                                "Accept: */*\r\n" +
                                "\r\n";

    ProxyPayload buildProxyRequest(ByteArrayOutputStream outputStream) {

        String[] stringList = outputStream.toString(StandardCharsets.UTF_8).split("\n");


        if (stringList.length < 1)
            throw new RuntimeException(BAD_REQUEST);

        String firstLine = stringList[0].strip();

        var posColon = firstLine.indexOf(":");

        var url = firstLine.substring(posColon + 2).trim();


        String host = "";
        int port = 80;
        String path = "";
        try {
            URL requestUrl = new URL(url);
            host = requestUrl.getHost();
            path = requestUrl.getPath();
            port = (requestUrl.getPort() == -1) ? requestUrl.getDefaultPort() : requestUrl.getPort();
        } catch (MalformedURLException e) {
            throw new RuntimeException(BAD_REQUEST, e);
        }

        var payload = String.format(req_builder, path, host);


        return new ProxyPayload(host, port, payload);


    }

    static String get502Header() {
        return String.format("HTTP/1.0 502 Bad Gateway\r\n" +
                             "Content-Type: text/html\r\n" +
                             "Date: %s\r\n" +
                             "\r\n", new Date());
    }

}


record ProxyPayload(String host, int port, String payload) {
}
