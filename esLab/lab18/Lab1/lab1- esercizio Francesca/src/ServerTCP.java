import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;

public class ServerTCP {
    public static void main (String argv[]) throws Exception {
        String clientSentence;
        String capSentence;

        //crea una listening socket sulla porta specificata
        ServerSocket welcomeSocket = new ServerSocket(6789);

        while(true){
            //viene chiamata l'accept (bloccante), all'arrivo di una nuova connessione viene creata
            //una nuova established socket
            Socket connectionSocket = welcomeSocket.accept();

            //inizializzo lo stream di input dalla socket
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            //inizializza lo stream di output verso la socket
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            clientSentence = inFromClient.readLine();
            capSentence=clientSentence.toUpperCase()+'\n';

            //invia la risposta al client
            outToClient.writeBytes(capSentence);
        }
    }
}
