package forkjoinpool;

import java.util.concurrent.ForkJoinPool;

public class Main {


    public static void main(String[] args) {

        //initializing forkJoinPool. 4 is the number of threads, the better parameter is the number of available cores.
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);

        //initializing a recursive task
        MyRecursiveTask myRecursiveTask = new MyRecursiveTask(1024);

        //invoking the task. it is blocking until the final result is computed
        long mergedResult = forkJoinPool.invoke(myRecursiveTask);

        System.out.println("merged result = "+mergedResult);

    }




}
