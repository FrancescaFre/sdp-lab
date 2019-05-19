import java.io.*;
import java.net.*;
import java.nio.Buffer;

/*
Il server (iterativo):
deve leggere la porta per mettersi in ascolto da riga di comando

deve stampare a monitor l’indirizzo e la porta dei client che siconnettono

riceve due interi dal client, effettua la somma e risponde colrisultato
*/

public class ServerTCPEs1 {
    public static void main(String argv[]) {

        String clientN1;
        String clientN2;
        int result;

        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(argv[0]));

            while (true) {
                Socket connectionSocket = serverSocket.accept();

                System.out.println("è arrivato qualcuno!!! ");
                String indirizzoClient = (connectionSocket.getInetAddress()).toString();
                int port = connectionSocket.getPort();
                System.out.println("porta: " + port + "\nindirizzo: " + indirizzoClient);


                //inizializzo il buffer di input
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                //inizializzo il buffer per l'output
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                clientN1 = inFromClient.readLine();
                clientN2 = inFromClient.readLine();

                result = (Integer.parseInt(clientN1) + Integer.parseInt(clientN2));

                System.out.println("Somma: " + Integer.parseInt(clientN1) + " + " + Integer.parseInt(clientN2) + " = " + result);

                //invio la risposta
                outToClient.writeBytes(result + "\n");
            }
        } catch (IOException ioE) {
            System.err.println("IoException nella creazione della socket");
            ioE.printStackTrace();
        }
    }
}
