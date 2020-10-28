import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Room_Vet {
    /*
     * Un gatto non può entrare se ci sono già almeno un catto o un cane
     *
     * Un cane non può entrare se c'è un gatto
     *
     * In una sala possono esserci al massimo 4 cani
     *
     * */

    volatile int nCani;
    volatile int nGatti;
    Date date = new Date();

    public synchronized boolean enterRoom(int pet, int nthread) {
        if (pet == 0 && nCani == 0 && nGatti == 0) { //if GATTO
            nGatti += 1;
            PrettyPrintEnter(0, nthread, pet);
            return true;
        } else if (pet == 1 && nGatti == 0 && nCani <= 4) { //If CANE
            nCani += 1;
            PrettyPrintEnter(0, nthread, pet);
            return true;

        } else {
            PrettyPrintEnter(1, nthread, pet);

//Questo wait si può fare sia dall'istanza sincronizzata, sia dal thread
            try {
                wait();

            } catch (InterruptedException ie) {
                System.out.println("++++++++++++++++ Problema della Wait() del pet");
                ie.printStackTrace();
            }

            return false;
        }
    }

    public synchronized void exitRoom(int pet, int nthread) {
        if (pet == 0)
            nGatti-=1;
        else if (pet == 1)
            nCani-=1;
        PrettyPrintEnter(2, nthread, pet);
        notifyAll();
    }

    public void PrettyPrintEnter(int i, int t, int pet) {
        switch (i) {
            case 0:
                System.out.println("\n\n["+new SimpleDateFormat("mm.ss.SS").format(new Timestamp(System.currentTimeMillis())) +"]-----------------------------Thread: " + t +
                        (pet==1? "Cane":"Gatto")+
                        "\nAnimale accettato - Situazione attuale: " +
                        "\n.......Cani presenti: " + nCani +
                        "\n......Gatti presenti: " + nGatti +
                        "\n-----------------------------");
                break;

            case 1:
                System.out.println("\n\n["+new SimpleDateFormat("mm.ss.SSS").format(new Timestamp(System.currentTimeMillis())) +"]-----------------------------Thread: " + t +
                        (pet==1? "Cane":"Gatto")+
                        "\nAnimale Rifiutato - Situazione attuale: " +
                        "\n.......Cani presenti: " + nCani +
                        "\n......Gatti presenti: " + nGatti +
                        "\n-----------------------------");
                break;

            case 2:
                System.out.println("\n\n["+new SimpleDateFormat("mm.ss.SSS").format(new Timestamp(System.currentTimeMillis())) +"]-----------------------------Thread: " + t +
                        (pet==1? "Cane":"Gatto")+
                        "\nAnimale in uscita - Situazione attuale: " +
                        "\n.......Cani presenti: " + nCani +
                        "\n......Gatti presenti: " + nGatti +
                        "\n-----------------------------\n+++++++++ NOTIFY!!!");
                break;
        }
    }
}
