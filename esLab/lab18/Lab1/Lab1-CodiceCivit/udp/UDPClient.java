import java.io.*; 
import java.net.*; 

class UDPClient {
	public static void main(String args[]) throws Exception {
		/* Inizializza l'input stream (da tastiera) */
		BufferedReader inFromUser =                             
			new BufferedReader(new InputStreamReader(System.in));

		/* Crea una datagram socket */
		DatagramSocket clientSocket = new DatagramSocket();
		
		/* Ottiene l'indirizzo IP dell'hostname specificato
		 * (contattando eventualmente il DNS) */
		InetAddress IPAddress = InetAddress.getByName("localhost");

		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];

		String sentence = inFromUser.readLine();
		sendData = sentence.getBytes();

		/* Prepara il pacchetto da spedire specificando
		 * contenuto, indirizzo e porta del server */
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, 
					                                   IPAddress, 9876); 

		/* Invia il pacchetto attraverso la socket */
		clientSocket.send(sendPacket); 

		/* Prepara la struttura dati usata per contenere il pacchetto in ricezione */
		DatagramPacket receivePacket = 
			new DatagramPacket(receiveData, receiveData.length); 

		/* Riceve il pacchetto dal server */
		clientSocket.receive(receivePacket); 

		String modifiedSentence = new String(receivePacket.getData()); 

		System.out.println("FROM SERVER:" + modifiedSentence); 
		clientSocket.close(); 
	} 
} 

