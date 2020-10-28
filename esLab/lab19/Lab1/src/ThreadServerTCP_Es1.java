import java.io.*;
import java.net.*;
import java.nio.Buffer;
import java.security.cert.TrustAnchor;

public class ThreadServerTCP_Es1 implements Runnable{

    Socket connectionSocket = null;
    BufferedReader inFromClient;
    DataOutputStream outToClient;

    String clientN1;
    String clientN2;
    int result;

    //costruttore
    public ThreadServerTCP_Es1 (Socket socket){
        connectionSocket = socket;
        try{
            //inizializzo il buffer di input
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            //inizializzo il buffer per l'output
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        }

        catch (IOException io){
            io.printStackTrace();
        }
    }

    public void run(){
        System.out.println("Thread in run" );
        String indirizzoClient = (connectionSocket.getInetAddress()).toString();
        int port = connectionSocket.getPort();
        System.out.println("Client connesso \nporta: "+port+"     indirizzo: " + indirizzoClient);

        try {
            System.out.println("hei1");


            clientN1 = inFromClient.readLine();
            clientN2 = inFromClient.readLine();


            System.out.println("hei2");
            result = (Integer.parseInt(clientN1) + Integer.parseInt(clientN2));

            System.out.println("Somma: " + Integer.parseInt(clientN1) + " + " + Integer.parseInt(clientN2) + " = " + result);

            //invio la risposta
            outToClient.writeBytes(result+"\n");

            connectionSocket.close();
        }

        catch (IOException io){
            io.printStackTrace();
        }
    }
}
