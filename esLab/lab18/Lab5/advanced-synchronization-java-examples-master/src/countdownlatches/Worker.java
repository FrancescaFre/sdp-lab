package countdownlatches;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Worker implements Runnable {

    private Random rnd;
    private CountDownLatch countDownLatch;

    public Worker(CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
        this.rnd = new Random();
    }

    @Override
    public void run() {

        System.out.println("["+Thread.currentThread()+"] Thread started!");
        wasteSomeTime();
        System.out.println("["+Thread.currentThread()+"] Goodbye.");
        //decrease the countdown
        countDownLatch.countDown();

    }

    private void wasteSomeTime() {
        int seconds = rnd.nextInt(10) + 1;
        try {Thread.sleep(seconds*1000);}
        catch(Exception ex) {ex.printStackTrace();}
    }
}
