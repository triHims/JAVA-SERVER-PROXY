import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

class Client_Demo {
    static String req = "PROXY: %s\r\n";

    public static void main(String[] args) throws Exception {

        System.out.println("Enter the url to fetch: \n");
        Scanner sc = new Scanner(System.in);
        var urlInput = sc.next();

        System.out.println("Reset socket time outs");
        Socket s = new Socket("localhost", 5222); // Define the proxy details here

        s.setSoTimeout(10000); // time out of 10 seconds

        var din = s.getInputStream();
        var dout = s.getOutputStream();
        dout.write(String.format(req, urlInput).getBytes(StandardCharsets.UTF_8));
        dout.write(-1);

        dout.flush();
        s.shutdownOutput();
        System.out.print("\n\nWaiting for output\n");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        int red=0;
        while (red != -1) {
            red = din.read();
            bos.write(red);

        }

        System.out.println(bos);
        dout.close();
        din.close();

        s.close();
    }
}
