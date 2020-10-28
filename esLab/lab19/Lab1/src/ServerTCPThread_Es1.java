import java.io.*;
import java.net.*;
import java.nio.Buffer;

public class ServerTCPThread_Es1 {
    public static void main (String argv[]){

        String clientN1;
        String clientN2;
        int result;

        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(argv[0]));

            while (true){
                Socket connectionSocket = serverSocket.accept();

                Thread thread = new Thread(new ThreadServerTCP_Es1(connectionSocket));
                //avvio del thread
                thread.start();

                //oppure
                /*
                * public class MyThread exends Thread{
                *   public void run (){}
                * }
                *
                * Si chiama con
                * MyThread thread = new MyThread()
                * thread.start();
                * */
            }
        }

        catch (IOException ioE) {
            System.err.println("IoException nella creazione della socket");
            ioE.printStackTrace();
        }


    }

}

