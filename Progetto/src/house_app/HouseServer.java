package house_app;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import message_measurement.House;

import java.io.IOException;

public class HouseServer implements Runnable {

    Server server;
    public HouseServer(HouseNode node, int port){
        server = ServerBuilder.forPort(port).addService(new HouseService(node)).build();

    }


    @Override
    public void run() {
       try{
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