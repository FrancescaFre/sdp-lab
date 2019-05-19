package conditions;

public class Consumer implements Runnable {

    private final BoundedBuffer<String> boundedBuffer;
    private final String id;

    public Consumer(String id, BoundedBuffer<String> boundedBuffer) {
        this.id = id;
        this.boundedBuffer = boundedBuffer;
    }

    public void run() {
        try {
            while(true) {
                consume(boundedBuffer.take());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void consume(String message) {
        System.out.println("Cons. " + id + ": prelevato " + message);
    }
}