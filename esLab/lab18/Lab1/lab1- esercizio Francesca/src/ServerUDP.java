import java.io.*;
import java.net.*;

public class ServerUDP {
    public static void main(String argv[]) throws Exception {
        //inizializo la socket datagram specificandone la porta d'ascolto
        DatagramSocket serverSocket = new DatagramSocket(9876);

        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        while(true){
            //preparo il pacchetto che serve per ricevere i dati in entrata (il pacchetto in entrata)
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            //Riceve un pacchetto da un client
            serverSocket.receive(receivePacket);

            String sentence = new String(receivePacket.getData());
            //Ottiene le info dal pacchetto ricevuto
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            String capSentence = sentence.toUpperCase();

			sendData = capSentence.getBytes();
			
            //prepara il pacchetto da inviare
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

            //invia il pacchetto
            serverSocket.send(sendPacket);

        }

    }
}

