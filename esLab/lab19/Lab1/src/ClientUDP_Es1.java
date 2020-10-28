import javax.xml.crypto.Data;
import java.net.*;
import java.io.*;


/*Il client:
deve leggere l’indirizzo e la porta del server da riga di comando
deve leggere due numeri da standard input e inviarli al server
riceve e stampa la risposta del server*/

public class ClientUDP_Es1 {
    public static void main (String argv[]){

        //inizializzo l'input stream
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        //inizializzo la socket client connessa al server

        String addr = argv[0];
        int port = Integer.parseInt(argv[1]);
        DatagramSocket clientSocket;

        try {
            clientSocket = new DatagramSocket();

            //leggo da tastiera
            System.out.println("Dammi due numeri separati da un un invio");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String n1 = in.readLine();
            String n2 = in.readLine();

            String packet = new String(n1 + '\n'+n2);

            byte[] send_data = new byte[1024];
            send_data = packet.getBytes();

            //preparo il pacchetto
            DatagramPacket send_packet = new DatagramPacket(send_data, send_data.length, InetAddress.getByName(argv[0]), Integer.parseInt(argv[1]) );

            //invio il pacchetto
            clientSocket.send(send_packet);

            //ricevo la risposta e preparo le strutture dati
            byte[] receive_data = new byte[1024];
            DatagramPacket receive_packet = new DatagramPacket(receive_data, receive_data.length);

            clientSocket.receive(receive_packet);

            String receive_info = new String(receive_packet.getData());

            System.out.println("From server: "+receive_info);

            clientSocket.close();

        }

        catch (IOException ioE){
            System.err.println("IoException nella creazione della socket");
            ioE.printStackTrace();
        }
    }
}



