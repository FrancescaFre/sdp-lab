import javax.sound.midi.SysexMessage;
import java.io.*;
import java.net.*;
public class Esercizio1ServerTCP {
    public static void main (String[] argv) throws Exception {
        //prendo da linea di comando la porta che devo aprire
        ServerSocket serverS = new ServerSocket(Integer.parseInt(argv[0]));

        Socket inEntrata = serverS.accept();

        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(inEntrata.getInputStream()));
        String indirizzoClient = (inEntrata.getInetAddress()).toString();
        int port = inEntrata.getPort();
        System.out.println("porta: "+port+"\nindirizzo: " + indirizzoClient);

        int n1= Integer.parseInt(inFromClient.readLine());
        int n2= Integer.parseInt(inFromClient.readLine());
        int sum = n1 + n2;

        System.out.println("numero1: "+n1+" numero2: "+n2+" somma: "+sum+"\n");

        //mi preparo a inviare
        DataOutputStream outToClient = new DataOutputStream(inEntrata.getOutputStream());
        outToClient.writeBytes(sum+"\n");
        inEntrata.close();
    }
}
