import java.util.Random;

public class GeneraCuccioli {
        public static void main(String argv[]) throws Exception {
            SalaCuccioli sala = new SalaCuccioli();
            Random r = new Random();


                for (int i = 0; i < 10 ; i++) {
                    Cuccioli theThread = new Cuccioli(100, sala, r.nextInt(2), i);
                    theThread.start();
                }

        }
}
