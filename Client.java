import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

class Client {
    static String req = "PROXY: %s\r\n";

    static String boka ="GET / HTTP/1.1\r\n" +
                        "Host: info.cern.ch\r\n" +
                        "User-Agent: curl/7.85.0\r\n" +
                        "Accept: */*\r\n" +
                        "\r\n";
    public static void main(String[] args) throws Exception {


        System.out.println("Enter the url to fetch: \n");
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        var urlInput = consoleReader.readLine();


//        Socket s = new Socket("localhost", 5222);  //Define the proxy details here
        Socket s = new Socket("info.cern.ch",80);
//        s.setSoTimeout(10000); // time out of 10 seconds

//        var din = new BufferedReader(new InputStreamReader(s.getInputStream()));
        var din =  s.getInputStream();
//        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
         var dout = s.getOutputStream();

//        dout.write(String.format(req,urlInput).getBytes(StandardCharsets.UTF_8));
        dout.write(boka.getBytes(StandardCharsets.UTF_8));
        dout.write(-1);
        dout.flush();
        System.out.print("\n\nWaiting for output\n");

//        int blankLines =0;
//        while (blankLines<2) {
//            var line = din.readLine();
//            if(line==null)
//                break;
//            if (line.trim().length()==0)
//                ++blankLines;
//
//
//            System.out.println(line);
//        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        while(true){
               int red =   din.read();
                bos.write(red);

                if(red == -1 )
                    break;

        }

        System.out.println(bos);




        dout.close();
        s.close();
    }
}



