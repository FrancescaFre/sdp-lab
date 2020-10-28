import java.util.Random;

public class Prenotazioni {

    private int n ;
    public Prenotazioni (int n)
    {
        this.n=n;
    }
    public synchronized int controllo(){

        if (n>0){
            Random r = new Random();
            int c = r.nextInt(n);
            n--;
            System.out.print(n+"\n");
            return c;
        }else{
            return 0;
        }
    }
}
