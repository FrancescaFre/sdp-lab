import java.net.*;
import java.io.*;

public class Esercizio1ClientTCP {
    public static void main (String[] argv) throws Exception{
        String addr = argv[0];
        int port = Integer.parseInt(argv[1]);

        System.out.println("Dammi due numeri (separati da un \"enter\"" );
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String n1 = in.readLine();
        String n2 = in.readLine();

        Socket clientSocket = new Socket(addr,port);

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        outToServer.writeBytes(n1 + '\n'+ n2 + '\n'); //RICORDIAMOCI STO CAZZO DI \N ALLA FINE DELLA STRINGA DA INVIARE

        //attendo risposta
        String risposta = inFromServer.readLine();
        System.out.println(risposta + "\n");
        clientSocket.close();

    }
}
