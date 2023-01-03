package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

class Server {
    static ExecutorService executorService  = new ForkJoinPool(20);
    public static void main(String[] args) throws IOException {

        System.out.println("Starting server at port 9999");


        try (ServerSocket ss = new ServerSocket(9999);) {
            while (true) {
                System.out.println("Reading socket");
                Socket s = ss.accept();
                System.out.println("Read and delegate");
                executorService.submit(new SocketHandler(s));
            }

        }

    }

}


