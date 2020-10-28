import java.io.*;
import java.net.*;

public class TCPServerThread extends Thread{
    private Socket connectionSocket = null;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;

    //costruttore
    public TCPServerThread (Socket s) {
        connectionSocket = s;
        try {
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String clientSentence;
        String capSentence;
        try{
            clientSentence = inFromClient.readLine();
            capSentence = clientSentence.toUpperCase();
            outToClient.writeBytes(capSentence);
            connectionSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
