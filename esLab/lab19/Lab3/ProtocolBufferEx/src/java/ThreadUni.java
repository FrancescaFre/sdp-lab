import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

//importo il .proto
import ProtoBuf.ProtoBuf_Esercizio.*;

public class ThreadUni extends Thread{
    Socket activeSocket = null;
    InputStream fromClient;
    OutputStream toClient;
    int id;
    Risposta risposta;

    public ThreadUni (Socket s,  int id){
        activeSocket = s;
        SetStreams();
        this.id = id;
    }

    public void run() {

        try {
            //ricezione del messaggio
            Studente ricezione = Studente.parseDelimitedFrom(fromClient);

            //lettura del messaggio
            PrettyPrint(ricezione);

            //creazione del messaggio di risposta
            BuildRisposta();

            //invio risposta
            risposta.writeDelimitedTo(toClient);
            activeSocket.close();
        } catch (SocketException so){
            so.printStackTrace();
        } catch (IOException io){
            io.printStackTrace();
        }
    }

    public void SetStreams(){
        try {
            fromClient = activeSocket.getInputStream();
        }
        catch (IOException io){
            System.err.println("Errore nel set di fromClient");
            io.printStackTrace();
        }

        try {
            toClient = activeSocket.getOutputStream();
        }
        catch (IOException io){
            System.err.println("Errore nel set di toClient");
            io.printStackTrace();
        }
    }

    private  void PrettyPrint (Studente ricezione){

        System.out.println(
                "Informazioni studente id " + id +
                        "\n...........Nome studente: "+ricezione.getNome()+
                        "\n........Cognome studente: "+ricezione.getCognome()+
                        "\nAnno di nascita studente: "+ricezione.getAnnoNascita()+
                        "\n......Residenza studente: "+ricezione.getResidenza().getCitta()+
                        " in via "+ricezione.getResidenza().getViaViale()+
                        " n° " + ricezione.getResidenza().getNumeroCivico()
        );

        for (int i = 0; i<ricezione.getListaEsamiCount(); i++)
            System.out.println("Esami studente id "+id+
                    " esame n° "+i+"------------------------"+
                    "\n Nome esame: "+ricezione.getListaEsami(i).getNomeEsame()+
                    "\n Voto esame: "+ricezione.getListaEsami(i).getVoto()+"/30"+
                    "\n Data esame: "+PrettyDate(ricezione.getListaEsami(i).getVerbalizzazione().getGiorno(),ricezione.getListaEsami(i).getVerbalizzazione().getMese(), ricezione.getListaEsami(i).getVerbalizzazione().getAnno() )
            );

    }

    private String PrettyDate (int g, int m, int a){
        return new String(g+"/"+m+"/"+a);
    }

    private void BuildRisposta(){
        Risposta.Builder rispostaBuilder = Risposta.newBuilder();
        rispostaBuilder.setAck("Tutto ok! From_Server mlem");
        risposta = rispostaBuilder.build();
    }
}
