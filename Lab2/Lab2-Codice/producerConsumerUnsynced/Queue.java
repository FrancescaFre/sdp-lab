import java.util.ArrayList;

//Esempio di coda condivisa che non usa wait e notify
//Quindi questo e' un esempio di come le cose NON vanno fatte (vedi busy waiting)
public class Queue {

	public ArrayList<String> buffer = new ArrayList<String>();
	
	
	public void put(String message) {
		buffer.add(message);
	}


	public String take() {
		String message = null;
		if(buffer.size() > 0) {
			
			message = buffer.get(0);
			buffer.remove(0);
		}

		return message;
	}

}
