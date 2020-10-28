package forkjoinpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class MyRecursiveTask extends RecursiveTask<Long> {

    //this variable simulates the amount of work of a specific task
    private long workload = 0;

    public MyRecursiveTask(long workload){
        this.workload = workload;
    }

    //it is important to override this method
    //it defines what a task does
    @Override
    protected Long compute() {

        //recursive condition
        //if the workload is "too much"
        if(this.workload > 16){

            //we create subtasks to split the work
            List<MyRecursiveTask> subtasks = new ArrayList<>();
            //add subtasks to the list of subtasks
            subtasks.addAll(createSubtasks());

            //for each subtask we call the fork() method.
            for(MyRecursiveTask subtask: subtasks){

                subtask.fork();

            }

            //after each subtask is forked, we need to define what to do to merge things
            long result = 0;

            //joining the results of subtasks
            for(MyRecursiveTask subtask: subtasks){
                result += subtask.join();
            }

            return result;

        }

        //base step of recursion: if the workload is <16
        return workload*3;
    }

    //method to split work in subtaks
    private List<MyRecursiveTask> createSubtasks() {

        List<MyRecursiveTask> subtasks = new ArrayList<>();

        //we simply divide the workload to two subtasks
        MyRecursiveTask subtask1 = new MyRecursiveTask(this.workload/2);
        MyRecursiveTask subtask2 = new MyRecursiveTask(this.workload/2);

        subtasks.add(subtask1);
        subtasks.add(subtask2);

        return subtasks;

    }
}
