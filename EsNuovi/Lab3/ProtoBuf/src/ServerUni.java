import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ProtoBuf.ProtoBuf_Esercizio.*;

public class ServerUni {
    public static void main (String[] argv){
       int countThread =0;
        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            Socket activeConnection;

            while (true){
                activeConnection = serverSocket.accept();

                ThreadUni thread = new ThreadUni(activeConnection, countThread++);
                thread.start();
            }
        }

        catch (IOException io){
            io.printStackTrace();
        }
    }
}
