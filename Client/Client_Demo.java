 // Imported buffered Reader
import java.io.BufferedReader;
 //bytearrayoutputstream for having a byte array
import java.io.ByteArrayOutputStream;
 // let us inport the socket for socket api
import  java.net.Socket;
 // StandardCharsets to convert he default string to byte array
import    java.nio.charset.StandardCharsets;
 // get the scanner to take the input
import    java.util.Scanner;

class Client_Demo { // client class
     // Read the url and send to the destination wrapped with proxy
    static String req = "PROXY: %s\r\n";

    public static void main(String[] args) throws Exception {

        System.out.println("Enter the url to fetch: \n");
         // this is a console input scanner we need it to read the input from the console
        var consoleInputScanner = new Scanner(System.in);
        var urlInput = consoleInputScanner.next();

        Socket s = new Socket("localhost", 5222); // Define the proxy details here

        s.setSoTimeout(10000); // time out of 10 seconds

        var din = s.getInputStream(); // din is the socket inputstream
        var dout = s.getOutputStream(); // dout is the  socket  outputstream

        dout.write(String.format(req, urlInput)); // writing the payload on input stream
        dout.write(-1); // writing the eof character to mark the stream end
        dout.flush(); // flush the stream manually
        s.shutdownOutput();//shutdown the output so that reciever can stop reading
        System.out.print("\n\nWaiting for output\n");

        var flexibleByteArray = new ByteArrayOutputStream();  // flexible container to read input
        int red=0; // variable to read
        while (red != -1) {
            red = din.read();
            flexibleByteArray.write(red);

        }

        System.out.println(flexibleByteArray);

        dout.close(); // close the output stream as we are done reading the data
        s.close(); // close the socket  completly
        din.close(); // close the inputstream

         sc.close(); //since we are shutting down close the scanner
    
    }
}
