package conditions;

public class Producer implements Runnable {

    private final String id;
    private BoundedBuffer<String> boundedBuffer;

    public Producer(String id, BoundedBuffer<String> boundedBuffer) { this.id = id; this.boundedBuffer = boundedBuffer; }

    public void run() {
        while (true) {
            try {

                String message = produce();
                System.out.println("Prod. " + id + ": inserisco " + message);
                boundedBuffer.put(message);
                //Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int counter = 0;

    public String produce() {
        counter++;
        return "Messaggio da " + id + " n. " + counter;
    }

}
