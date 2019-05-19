import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;

public class Thread_Chat extends Thread{
    ArrayList<Socket> users = new ArrayList<Socket>();
    Buffer_Chat buffer;

    public Thread_Chat(Buffer_Chat bc){
        buffer = bc;
    }

    public void AddMember (Socket s){
        users.add(s);
    }

    public int getUser(){
        return users.size();
    }



}
