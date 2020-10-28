import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

//il client si connette al server

//il server lo ridirige ad un thread che gli permetterà di scegliere se creare
//una nuova lobby o unirsi ad una esistente

//poi questo thread aggiornerà le informazioni di lobbies (una struttura dati da sincronizzare)

public class Server_Chat {
    public static void main (String[] argv){
        ArrayList<Pair> lobbies = new ArrayList<Pair>();

        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            Socket activeSocket;

            while (true){
                activeSocket = serverSocket.accept();




            }


        }

        catch (IOException io){
            System.out.println("Problemi nella serversocket");
            io.printStackTrace();
        }


    }




    public class Pair {
        Lobby_Chat lobby;
        Buffer_Chat buffer;

        public Pair(Buffer_Chat b_C, Lobby_Chat l_c) {
            lobby = l_c;
            buffer = b_C;
        }
    }
}
