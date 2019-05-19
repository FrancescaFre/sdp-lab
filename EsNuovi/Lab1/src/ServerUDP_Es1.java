import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.nio.Buffer;

/*
Il server (iterativo):
deve leggere la porta per mettersi in ascolto da riga di comando

deve stampare a monitor l’indirizzo e la porta dei client che siconnettono

riceve due interi dal client, effettua la somma e risponde colrisultato
*/

public class ServerUDP_Es1 {
    public static void main (String argv[]){

        String clientN1;
        String clientN2;
        int result;

        try {
            DatagramSocket serverSocket = new DatagramSocket(Integer.parseInt(argv[0]));

            while (true){
                byte[] receive_data = new byte [1024];
                byte[] send_data = new byte [1024];

                DatagramPacket receive = new DatagramPacket(receive_data, receive_data.length);

                serverSocket.receive(receive);

                System.out.println("è arrivato qualcuno!!! " );
                InetAddress address = receive.getAddress();
                int port = receive.getPort();
                System.out.println("porta: "+port+"\nindirizzo: " + address.toString());

                String [] receive_info = new String (receive.getData()).split("[\n ]+");

                clientN1 = receive_info[0];
                clientN1 = clientN1.replaceAll("\\D+","");

                clientN2 = receive_info[1];
                clientN2 = clientN2.replaceAll("\\D+","");

                result = (Integer.parseInt(clientN1) + Integer.parseInt(clientN2));

                System.out.println("Somma: "+ Integer.parseInt(clientN1) + " + " + Integer.parseInt(clientN2) + " = " + result);

                //preparo le informazioni
                send_data = new String (result + "\n").getBytes();


                //invio la risposta
                DatagramPacket send_packet = new DatagramPacket(send_data, send_data.length, address, port);

                serverSocket.send(send_packet);
            }
        }
        catch (IOException ioE) {
            System.err.println("IoException nella creazione della socket");
            ioE.printStackTrace();
        }
    }

}
