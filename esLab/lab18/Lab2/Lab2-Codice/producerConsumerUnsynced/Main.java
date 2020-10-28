public class Main {
	public static void main(String[] args) {
		Queue q = new Queue();
		Producer p = new Producer("p1", q);
		Consumer c1 = new Consumer("c1", q);
		Consumer c2 = new Consumer("c2", q);
		new Thread(p).start();
		new Thread(c1).start();
		new Thread(c2).start();
	}
}