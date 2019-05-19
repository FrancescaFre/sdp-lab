import java.util.*;

public class Cuccioli extends Thread {

    int sleep;
    SalaCuccioli sala;
    int cosaSono;
    int i;

    public Cuccioli (int n, SalaCuccioli salaC, int c, int i){
        System.out.println("Generato un cucciolo "+c+"\n");
        Random r = new Random();
        sleep = r.nextInt(n);
        sala = salaC;
        cosaSono = c;
        this.i=i;
    }

    public void run(){

            if (sala.entrata(cosaSono)){
                try {
				    Thread.sleep(sleep);
                } catch (InterruptedException e) {e.printStackTrace();}
                sala.uscita (cosaSono);

            }
            else{
                System.out.println("\nSono "+cosaSono+" "+i+" e sono appena entrato nel wait");
                try {
                    synchronized (sala) {
                        sala.wait();
                    }

                    System.out.println("\nSono "+cosaSono+" "+i+" e sono dopo wait");

                } catch (InterruptedException e) {e.printStackTrace();}
                System.out.println("\nSono "+cosaSono+" "+i+" e sono appena uscito dal wait");
                run();

            }

    }
}



