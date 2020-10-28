import java.io.*;
import java.net.*;


public class MultiServerTCP {
    public static void main (String argv[]) throws Exception{
        ServerSocket welcomeSocket = new ServerSocket(6789);

        while(true) {
            Socket connectionSocket = welcomeSocket.accept();

            //creo un thread
            TCPServerThread theThread = new TCPServerThread(connectionSocket);

            //avvio il thread
            theThread.start();
        }
    }
}
