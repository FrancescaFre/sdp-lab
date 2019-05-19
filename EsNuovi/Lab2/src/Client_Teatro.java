import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client_Teatro {
    public static void main (String[] argv){

        String address =  "localhost";
        int port = 6666;

        System.out.print("Servizio prenotazione: ");

        Scanner fromShell = new Scanner(new InputStreamReader(System.in));
        String s = fromShell.nextLine();

        try {
            Socket socket = new Socket(address, port);
            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);

            Scanner fromServer = new Scanner(new InputStreamReader(socket.getInputStream()));

            toServer.println(s);

            String response = fromServer.nextLine();

            String[] responseMulti = response.split(" ");
            if (!response.contains("0"))
                System.out.println( "Hai prenotato il/i posti: "+s );
            else
                System.out.println("Non ci sono posti disponibili");

            socket.close();
        }

        catch (IOException e)
        {
               e.printStackTrace();
        }
    }
}
