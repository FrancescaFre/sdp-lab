import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Semaphore;

//Java ci offre utili strumenti per gestire la concorrenza (java.util.concurrent)
//Ecco un esempio, utilizzando l'implementazione dei semafori di questa libreria
//VIETATO USARE QUESTE LIBRERIE NEL PROGETTO
public class Main {
  public static void main(String arg[]) throws Exception {
	  Random r = new Random();
	  Vector<Thread> threads = new Vector<Thread>();
	  Semaphore s = new Semaphore(2); //this is a standard class in Java
	  //create some threads
	  for (int i=0; i<10; i++) {
		  MyThread mt = new MyThread(r, i, s);
		  threads.add(mt);
	  }
	  
	  //start all the threads
	  for (Thread t: threads) {
		  t.start();
	  }
  }
}
