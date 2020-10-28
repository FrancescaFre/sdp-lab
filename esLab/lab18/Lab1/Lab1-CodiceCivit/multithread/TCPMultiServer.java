import java.io.*; 
import java.net.*; 

class TCPMultiServer { 

	public static void main(String argv[]) throws Exception 
	{ 
		ServerSocket welcomeSocket = new ServerSocket(6789); 

		while(true) {   
			Socket connectionSocket = welcomeSocket.accept(); 

			/* Creazione di un thread e passaggio della established socket */
			TCPServerThread theThread =  
				new TCPServerThread(connectionSocket);

			/* Avvio del thread */
			theThread.start();
		} 
	} 
}