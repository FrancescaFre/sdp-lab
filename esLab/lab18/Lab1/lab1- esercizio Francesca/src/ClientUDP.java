import java.io.*;
import java.net.*;
public class ClientUDP {
    public static void main (String argv[]) throws Exception {
        //inizializza l'input stream
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        //crea un datagram socket
        DatagramSocket clientSocket = new DatagramSocket();

        //ottiene l'indirizzo ip e hostname specificato
        InetAddress IPAddress = InetAddress.getByName("localhost");

        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];

        String sentence = inFromUser.readLine();
        sendData = sentence.getBytes();

        //prepara il pacchtto da spedire specidicando i contenuto, indirizzo e porta
        DatagramPacket sendPacket = new DatagramPacket (sendData, sendData.length, IPAddress, 9876);

        //invia il pacchetto
        clientSocket.send(sendPacket);

        //prepara la struttura dati per contenere il pacchetto in ricezione
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        //riceve il pacchetto dal server
        clientSocket.receive((receivePacket));

        String sentenceFix = new String(receivePacket.getData());
        System.out.println("From Server:"+sentenceFix);
        clientSocket.close();
    }
}
