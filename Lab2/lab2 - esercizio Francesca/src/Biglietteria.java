import java.io.*;
import java.net.*;

public class Biglietteria {

    public static void main(String argv[]) throws Exception
    {
        ServerSocket welcomeSocket = new ServerSocket(6789);
        Prenotazioni prenotazioni = new Prenotazioni(50);

        while(true) {

            Socket connectionSocket = welcomeSocket.accept();
            /* Creazione di un thread e passaggio della established socket */

            BiglietteriaThread theThread =
                    new BiglietteriaThread(connectionSocket, prenotazioni);

            /* Avvio del thread */
            theThread.start();
        }
    }
}
