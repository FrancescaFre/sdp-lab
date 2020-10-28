import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class SumServer {
    public static void main (String[] argv){
        try {
            Server server = ServerBuilder.forPort(6060).addService(new SumService()).build();

            server.start();

            System.out.println("Server started");

            server.awaitTermination();
        }

        catch (IOException e){
            System.err.print("Errore nello start server ----------");
            e.printStackTrace();
        }
        catch (InterruptedException e){
            System.err.print("Errore nell'awaitTerminator ----------");
            e.printStackTrace();
        }
    }

}
