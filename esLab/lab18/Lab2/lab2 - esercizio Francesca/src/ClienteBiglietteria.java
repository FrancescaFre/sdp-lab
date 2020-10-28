import java.net.*;
import java.io.*;

public class ClienteBiglietteria {
    public static void main(String argv[]) throws Exception {

        String sentence;
        String modifiedSentence;

        /* Inizializza l’input stream (da tastiera) */
        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(System.in));

        /* Inizializza una socket client, connessa al server */
        Socket clientSocket = new Socket("localhost", 6789);

        BufferedReader inFromServer =
                new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

        /* Legge la risposta inviata dal server (linea terminata da \n) */
        String c = inFromServer.readLine();
        System.out.println("FROM SERVER: " + c);
        clientSocket.close();
    }


}
