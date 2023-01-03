package Proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;


class Proxy {
    static ExecutorService executorService  = new ForkJoinPool(20);
    public static void main(String[] args) throws IOException {

        System.out.println("Starting Proxy at port 5222");


        try (ServerSocket ss = new ServerSocket(5222);) {
            while (true) {
                System.out.println("Reading socket");
                Socket s = ss.accept();
                System.out.println("Read and delegate");
                executorService.submit(new ProxySocketHandler(s));
            }

        }

    }

}


