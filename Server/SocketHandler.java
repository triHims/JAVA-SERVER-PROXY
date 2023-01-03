package Server;

import java.io.*;
import  java.net.Socket;// Import socket to read and write data to socket
import java.net.SocketException; // Socket exception the exception thown by the socket
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


  public class SocketHandler implements Runnable {  // Socket handler class  supports multithreading
    

    Socket boundSocket;

    static String RESOURCE_FOLDER = "Resources";
    static String BAD_REQUEST = "Bad Request";
    static String READ_EXCEPTION = "Read Exception";

    static int EOF_CHAR = -1;

    SocketHandler(Socket boundSocket) {

        this.boundSocket = boundSocket;
        try {
            boundSocket.setSoTimeout(2000);
        } catch (SocketException ex) {
            System.out.printf("Socket Exception While setting the timeout\n %s\n %s", ex.getCause(), ex.getLocalizedMessage());
        }


    }

    /**
     * This function reads the request and services it
     */
    @Override
    public void run() { // This funtion is first run 
        // socket data is read and and translated
        BufferedReader inSocketInputStream = null;
        BufferedOutputStream outputStream = new BufferedOutputStream(null);
        try {

            //This inputstream is wrapped in bufferedreader to minimise the reads
            inSocketInputStream = new BufferedReader(new InputStreamReader(boundSocket.getInputStream()));


            outputStream = new BufferedOutputStream(boundSocket.getOutputStream());

            String line;


            var stringList = new ArrayList<String>();

            try { // TRy to read the data from the input stream

                while ( true) { // Use unbound while loop managed through brake
                    line = inSocketInputStream.readLine();
                    if (line == null || line.trim().length() == 0) {
                        System.out.println("Request Ended");
                        break;
                    }
                    System.out.println(line);
                    stringList.add(line);


                }
            } catch (Exception ex) { // catch all the exceptions and throw RuntimeException instead 
                // this will effectivily stop the reads
                throw new RuntimeException(READ_EXCEPTION, ex);
            }


            File f = handleRequest(stringList);


            if (!f.isFile()) {
                handleFileNotFoundCase(f, outputStream);
            } else {

                pushFileToClient(f, outputStream);
            }


        } catch (RuntimeException ex) {
            if (BAD_REQUEST.equalsIgnoreCase(ex.getMessage()) || READ_EXCEPTION.equalsIgnoreCase(ex.getMessage())) {

                try {
                    handleBadRequest(outputStream);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            outputStream.write(EOF_CHAR);
            outputStream.flush();
            outputStream.close();
            assert inSocketInputStream != null;
            inSocketInputStream.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

    }

    void handleFileNotFoundCase(File f, OutputStream outputStream) throws IOException {
        String header = Utitlity.get404Header();
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        String data = String.format("The file %s was not found", f.getPath());
        outputStream.write(data.getBytes(StandardCharsets.UTF_8));
    }

    void handleBadRequest(OutputStream outputStream) throws IOException {
        String header = Utitlity.get403Header();
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        String data = "Bad Request: Request is malformed";
        outputStream.write(data.getBytes(StandardCharsets.UTF_8));
    }

    void pushFileToClient(File f, OutputStream outputStream) throws IOException {
        String header = Utitlity.get200Header(f.getName());

        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        try (InputStream readFileInputStream = new FileInputStream(f)) {


            readFileInputStream.transferTo(outputStream);
        }
    }


    /**
     * Read the request
     * For get Requests :
     * Try to get the file
     * If file does not exist throw 404
     * Others throw bad request
     *
     * @param arrayList
     * @return
     */
    File handleRequest(ArrayList<String> arrayList) {
        var firstLine = arrayList.get(0);

        if (!firstLine.startsWith("GET")) {
            System.out.println("Sever does not support any request other than GET");
            throw new RuntimeException(BAD_REQUEST);
        }

        var secondWOrdStart = firstLine.indexOf(' ') + 1;
        var secondWOrdEnd = firstLine.indexOf(' ', secondWOrdStart);
        var substr = firstLine.substring(secondWOrdStart, secondWOrdEnd);

        System.out.println(substr);


        File f = new File(RESOURCE_FOLDER + substr);


        return f;


    }

}
