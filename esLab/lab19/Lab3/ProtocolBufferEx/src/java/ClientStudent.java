import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import ProtoBuf.ProtoBuf_Esercizio.*;


public class ClientStudent {
    //studente è il tipo di messaggio
    static Studente studente;
    static Scanner fromShell;
    private static InputStream fromServer;
    private static OutputStream toServer;

    public static void main (String[] argv){
        String address =  "localhost";
        int port = 6666;

        fromShell = new Scanner(new InputStreamReader(System.in));


        try {
            Socket socket = new Socket(address, port);
            fromServer = socket.getInputStream();
            toServer = socket.getOutputStream();

            //creazione messaggio
            BuildStudent();

            //invio messaggio - WriteTo
            studente.writeDelimitedTo(toServer);

            //ricezione messaggio - ParseFrom
            Risposta risp_pls= Risposta.parseDelimitedFrom(fromServer);

            //lettura
            System.out.println(risp_pls.getAck());

            socket.close();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void BuildStudent(){
        Studente.Builder builder = Studente.newBuilder();
        Residenza.Builder residenzaBuilder = Residenza.newBuilder();
        String s;

        System.out.println("Inserisci il nome");
        builder.setNome(fromShell.nextLine());
        System.out.println("Inserisci il cognome");
        builder.setCognome(fromShell.nextLine());
        try {

            System.out.println("Inserisci l'anno di nascita");
            s = fromShell.nextLine();


            builder.setAnnoNascita(Integer.parseInt(s));
        }catch (NumberFormatException io){
            System.err.println("errore nell'inserimento di una cifra");
        }

        System.out.println("Inserisci il nome della citta");
        residenzaBuilder.setCitta(fromShell.nextLine());
        System.out.println("Inserisci il nome della via/viale");
        residenzaBuilder.setViaViale(fromShell.nextLine());

        System.out.println("Inserisci il numero civico");
        s = fromShell.nextLine();

        residenzaBuilder.setNumeroCivico(Integer.parseInt(s));

        builder.setResidenza(residenzaBuilder);

        System.out.println("inserire il numero di esami ");
        int index = Integer.parseInt(fromShell.nextLine());
        for (int i = 0; i < index; i++){
            Esame.Builder esameBuilder = Esame.newBuilder();

            System.out.println("inserire il nome dell'esame ");
            esameBuilder.setNomeEsame(fromShell.nextLine());

            System.out.println("inserire il voto dell'esame (numero da 18 a 30) ");
            s = fromShell.nextLine();
            esameBuilder.setVoto(Integer.parseInt(s));

            Data.Builder databuilder = Data.newBuilder();
            String [] s1;

            System.out.println("inserire la data dell'esame da inserire (gg/mm/aaaa) ");
            s1 = fromShell.nextLine().split("/");

            databuilder.setAnno(Integer.parseInt(s1[2]));
            databuilder.setMese(Integer.parseInt(s1[1]));
            databuilder.setGiorno(Integer.parseInt(s1[0]));

            esameBuilder.setVerbalizzazione(databuilder);
            builder.addListaEsami(esameBuilder);
        }

        studente = builder.build();
    }
}