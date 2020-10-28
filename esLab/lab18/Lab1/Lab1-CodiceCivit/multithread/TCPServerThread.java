import java.io.*; 
import java.net.*;

public class TCPServerThread extends Thread {
	private     Socket connectionSocket = null;
	private     BufferedReader inFromClient;
	private     DataOutputStream  outToClient;

	/* L'argomento del costruttore e' una established socket */
	public TCPServerThread(Socket s) {
		connectionSocket = s;

		try{
			inFromClient =	
				new BufferedReader(
					new InputStreamReader(connectionSocket.getInputStream())); 

			outToClient = 
				new DataOutputStream(connectionSocket.getOutputStream()); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		String clientSentence; 
		String capitalizedSentence; 

		try {

			clientSentence = inFromClient.readLine(); 

			capitalizedSentence = clientSentence.toUpperCase() + '\n'; 

			outToClient.writeBytes(capitalizedSentence); 	    

			connectionSocket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
