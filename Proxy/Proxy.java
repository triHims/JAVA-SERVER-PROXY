package Proxy; //declared proxy package

import  java.io.IOException; //To support the ioexception we will import ioexception class 
//
import java.net.ServerSocket;// Import server socket because we need it to listen the data
import  java.net.Socket;// Import socket to read and write data to socket
//
import java.util.concurrent.ExecutorService;// Import executorservice to have a support for nice multithreading
//import forkjoinpool to have forkjoinpool as the multithreading model
import  java.util.concurrent.ForkJoinPool;


class Proxy {
    static ExecutorService executorService  = new ForkJoinPool(20);
    static public  void main(String[] args) throws IOException { // main class

        System.out.println("Starting Proxy at port 5222");


        try (var ss = new ServerSocket(5222);) {
            while (true) { // unbound while loop
                System.out.println("Reading socket");
                Socket s = ss.accept(); // open the socket
                System.out.println("Read and delegate");
                executorService.submit(new ProxySocketHandler(s));
               }

               }

              }
}


