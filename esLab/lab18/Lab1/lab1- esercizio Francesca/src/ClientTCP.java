import java.io.*;
import java.net.*;

public class ClientTCP {
    public static void main (String argv[]) throws Exception {
        String sentence;
        String sentenceFix;

        //inizializzo l'input stream
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        //inizializza una socket client connessa al server
        Socket clientSocket = new Socket ("localhost", 6789);

        //inizializza lo stream di output verso la socket
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        //inizializza lo stream di input della socket
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        //legge da tastiera
        sentence = inFromUser.readLine();

        //invia la sentence al server
        outToServer.writeBytes(sentence + '\n');

        //legge la risposta del server
        sentenceFix = inFromServer.readLine();
        System.out.println("From Server: "+ sentenceFix);
        clientSocket.close();
    }
}

