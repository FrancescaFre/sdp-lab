package countdownlatches;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        int numberOfThreads = 5;

        //creating an executor with a fixed number of threads
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        //creating a countdown which has exactly the number of threads we want to launch
        CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

        //let's start all the threads with the executor
        for(int i = 0; i < numberOfThreads; i++){

            executorService.submit(new Worker(countDownLatch));

        }

        System.out.println("Waiting for thread termination...");

        try {
            //blocking until all the threads executed countDown()
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("All the threads concluded their work.");

        executorService.shutdown();
    }


}
