package callable;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) {

        //data structure to store mapping between the computed value and the input value
        HashMap<Future<Long>, Integer> futureMap = new HashMap<>();

        ExecutorService executorService = Executors.newCachedThreadPool();

        //let's compute factorial of numbers from 2 to 10
        for(int i = 2; i<=10; i++){

            System.out.println("Computing factorial of "+i);
            //we insert into the map the association between future and input value
            futureMap.put(executorService.submit(new FactorialCallable(i)),i);

        }

        //for each future
        for(Future<Long> future: futureMap.keySet()){

            try {
                //get the answer
                Long answer = future.get();

                System.out.println("The factorial of "+futureMap.get(future)+" is: "+answer);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        //always shutdown executors
        executorService.shutdown();


    }

}
