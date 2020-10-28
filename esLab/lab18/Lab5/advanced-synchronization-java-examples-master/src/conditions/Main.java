package conditions;

public class Main {

    public static void main(String[] args) {

        int producers = 5;
        int consumers = 5;

        BoundedBuffer<String> boundedBuffer = new BoundedBuffer<>(5);

        for(int i = 0; i<consumers; i++)
            new Thread(new Consumer("c"+i, boundedBuffer)).start();

        for(int i = 0; i<producers; i++)
            new Thread(new Producer("p"+i, boundedBuffer)).start();
    }
}
