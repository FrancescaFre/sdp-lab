import java.net.*;
import java.io.*;


/*Il client:
deve leggere l’indirizzo e la porta del server da riga di comando
deve leggere due numeri da standard input e inviarli al server
riceve e stampa la risposta del server*/

public class ClientTCPEs1 {
    public static void main (String argv[]){



        //inizializzo l'input stream
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        //inizializzo la socket client connessa al server

        String addr = argv[0];
        int port = Integer.parseInt(argv[1]);
        Socket clientSocket;

        try {
            clientSocket = new Socket(addr, port);

            //inizializzo lo stream di output verso la socket del server
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

            //inizializza lo stream di input della socket
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //legge da tastiera
            System.out.println("Dammi due numeri separati da un un invio");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String n1 = in.readLine();
            String n2 = in.readLine();

            String s = new String (n1+"\n"+n2+"\n");

            //invio i numeri al server
            //outToServer.writeBytes(n1+" "+n2);
            outToServer.write(s.getBytes(), 0, s.length() );

            String risposta = inFromServer.readLine();
            System.out.println("From server: "+risposta);

            clientSocket.close();

        }

        catch (IOException ioE){
            System.err.println("IoException nella creazione della socket");
            ioE.printStackTrace();
        }
    }
}


