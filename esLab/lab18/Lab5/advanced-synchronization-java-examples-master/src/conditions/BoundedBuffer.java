package conditions;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer<T> {

    //defining a lock. a reentrant lock has the same behavior of intrinsic monitor
    private final Lock lock = new ReentrantLock();

    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    private T items[];

    private int count, putptr, takeptr;

    public BoundedBuffer(int size){
        items = (T[]) new Object[size];
        count = 0;
        putptr = 0;
        takeptr = 0;
    }

    public void put(T t) throws InterruptedException{

        //for a thread it has the same behavior of entering a synchronized area
        lock.lock();

        try{

            //until the buffer is full
            while(count == items.length) {
                System.out.println("Buffer pieno. In attesa di consumatori");
                //wait on the condition related to full buffer
                //this will release the lock similarly to wait()
                notFull.await();
            }

            //when we reach here, we are sure that the buffer is not full
            items[putptr] = t;

            //the put pointer is circular
            if(++putptr == items.length)
                putptr = 0;

            //we added an element
            ++count;

            //this is similar to notify()
            //if someone is waiting because the buffer is empty, let's wake it up
            notEmpty.signal();


        }finally {
            //to guarantee fairness, we always need to relase the lock
            //equivalent to exiting a synchronized area
            lock.unlock();
        }

    }

    public T take() throws InterruptedException{

        //the lock HAS to be the same as before, like for synchronized blocks
        lock.lock();

        try{

            //if there are no elements to read, wait
            while(count==0){

                System.out.println("Buffer vuoto. In attesa di produttori.");
                notEmpty.await();

            }

            //take the item pointed by takeptr
            T item = items[takeptr];

            //also this pointer is circular
            if(++takeptr == items.length)
                takeptr = 0;

            //we retrieved an element
            --count;

            //notify the threads sleeping on the full buffer condition
            notFull.signal();

            return item;

        }
        finally {
            lock.unlock();
        }



    }

}
