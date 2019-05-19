import java.io.*;
import java.net.*;

public class BiglietteriaThread extends Thread {

    private Socket connectionSocket = null;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;
    private Prenotazioni prenotazioni;

    /* L’argomento del costruttore e’ una established socket */
    public BiglietteriaThread(Socket s, Prenotazioni p) {

        connectionSocket = s;
        prenotazioni = p;


    }

    public void run() {
        try {
            outToClient =
                    new DataOutputStream(connectionSocket.getOutputStream());


            int p = prenotazioni.controllo();
            outToClient.writeBytes(p+"\n");
            connectionSocket.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}