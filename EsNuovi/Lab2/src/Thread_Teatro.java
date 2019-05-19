import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Thread_Teatro extends Thread{
    Socket activeSocket = null;
    Scanner fromClient;
    PrintWriter toClient;
    int ID;

    int howMany;
    Prenotazioni_Teatro prenotazioni_teatro;

    String response = new String("");

    public Thread_Teatro (Socket s, Prenotazioni_Teatro p, int id){
        activeSocket = s;
        prenotazioni_teatro = p;

        SetStreams();
        howMany=0;

        ID = id;
    }

    public void run() {
        System.out.println("++++++++++ " +ID+":Thread Attivo");
        howMany = Integer.parseInt(fromClient.nextLine());

        for (int i = 0; i < howMany; i++) {
            response += prenotazioni_teatro.RichiestaPosto();
            response += " ";
            System.out.println(response);
        }

        toClient.println(response);

        try {
            activeSocket.close();
        }
        catch (IOException io){
            io.printStackTrace();
        }
    }

    public void SetStreams(){
        try {
           fromClient = new Scanner(new InputStreamReader(activeSocket.getInputStream()));
        }
        catch (IOException io){
            System.out.println("Errore nel set di fromClient");
            io.printStackTrace();
        }

        try {
            toClient = new PrintWriter(activeSocket.getOutputStream(), true);
        }
        catch (IOException io){
            System.out.println("Errore nel set di fromClient");
            io.printStackTrace();
        }
    }

}
