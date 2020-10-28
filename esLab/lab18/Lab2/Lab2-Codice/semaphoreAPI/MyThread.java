import java.util.Random;
import java.util.concurrent.Semaphore;


public class MyThread extends Thread {
	private Random rnd;
	private int id;
	private Semaphore sem;
	
	MyThread(Random r, int i, Semaphore s) {
		rnd = r;
		id = i;
		sem=s;
	}
	
  public void run() {
	wasteSomeTime(); //Simulate the thread is doing something else
	System.out.println("Thread " + id + " wants to enter in the critical region");
	try{
		sem.acquire();
	}
	catch(InterruptedException ie) {
		ie.printStackTrace();
	}

	System.out.println("Thread " + id + " entered in the critical region!");
	wasteSomeTime(); //it takes some times to complete the work in the critical region

	System.out.println("Thread " + id + " is going to get out from the critical region");
	sem.release();
  }//end run
  
  private void wasteSomeTime() {
		int seconds = rnd.nextInt(10) + 1;
		try {
			Thread.sleep(seconds*1000);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
  }
} //end class
