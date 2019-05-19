package callable;

import java.util.concurrent.Callable;

public class FactorialCallable implements Callable<Long> {

    private int number;

    public FactorialCallable(int number){
        this.number = number;
    }

    @Override
    public Long call() {
        Long result = 1L;

        for(int i = 1; i<=number; i++)
            result*=i;

        return result;

    }
}
