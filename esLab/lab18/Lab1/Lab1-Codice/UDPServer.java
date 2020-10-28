import java.io.*;
import java.net.*;

class UDPServer {

	public static void main(String args[]) throws Exception {
	
		/* Inizializza la datagram socket specificando la porta di ascolto */
		DatagramSocket serverSocket = new DatagramSocket(9876);
	
		byte[] receiveData = new byte[1024];
	
		byte[] sendData;
	
		while(true)
		{
			/* Prepara la struttura dati usata per contenere il pacchetto in ricezione */
			DatagramPacket receivePacket =
				new DatagramPacket(receiveData, receiveData.length);
	
			/* Riceve un pacchetto da un client */
			serverSocket.receive(receivePacket);

			String sentence = new String(receivePacket.getData());

			/* Pulisce l'array di bytes dopo la lettura*/
			receiveData = new byte[1024];
	
			/* Ottiene dal pacchetto informazioni sul mittente */
			InetAddress IPAddress = receivePacket.getAddress();
	
			int port = receivePacket.getPort();
	
			String capitalizedSentence = sentence.toUpperCase();
	
			sendData = capitalizedSentence.getBytes();
	
			/* Prepara il pacchetto da spedire specificando
			* contenuto, indirizzo e porta del destinatario */
			DatagramPacket sendPacket =
				new DatagramPacket(sendData, sendData.length, IPAddress, port);
			
			/* Invia il pacchetto attraverso la socket */
			serverSocket.send(sendPacket);
		}
	}
}