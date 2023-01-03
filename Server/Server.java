package Server;//declared server package

import  java.io.IOException; //To support the ioexception we will import ioexception class 
//
import java.net.*;// because we need it to listen the data so importing socket and server socket
//
import    java.util.concurrent.ExecutorService;// Import executorservice to have a support for nice multithreading
//import forkjoinpool to have forkjoinpool as the multithreading model
import  java.util.concurrent.ForkJoinPool;
// we need a server
class Server {
    static ExecutorService executorService = new ForkJoinPool(20);

    static public  void main(String[] args) throws IOException { // main class

        System.out.println("Starting server at port 9999");


        try (var myserverSocket = new ServerSocket(9999)) {
            while (true) { // unbound while loop
                System.out.println("Reading socket");
                Socket currentSocket = myserverSocket.accept(); // open the socket to read
                System.out.println("Read and delegate");
                executorService.submit(new SocketHandler(currentSocket));
            }

        }

    }

}


