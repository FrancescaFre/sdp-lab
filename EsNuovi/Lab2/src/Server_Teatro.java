import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_Teatro {
    public static void main (String[] argv){
        Prenotazioni_Teatro prenotazioni_teatro = new Prenotazioni_Teatro(Integer.parseInt(argv[0]));
        int countThread =0;
        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            Socket activeConnection;

            while (true){
                activeConnection = serverSocket.accept();

                Thread_Teatro thread = new Thread_Teatro(activeConnection, prenotazioni_teatro, countThread++);
                thread.start();
            }
        }
        catch (IOException io){
            io.printStackTrace();
        }


    }

}
