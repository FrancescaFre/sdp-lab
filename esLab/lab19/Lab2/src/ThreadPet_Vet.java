public class ThreadPet_Vet implements Runnable{

    private int nThread;
    private int pet;
    private Room_Vet room;
    boolean entered;


    public ThreadPet_Vet (int i, int j, Room_Vet r){
        nThread = i;
        pet = j;
        room = r;
        entered = false;
    }

    public void run() {
        if (pet == 0)
            System.out.println("\n******** Thread: " + nThread + " --> Gatto");
        if (pet == 1)
            System.out.println("\n******** Thread: " + nThread + " --> Cane");

        while (!entered) {
            entered = room.enterRoom(pet, nThread);

            if (entered) {
                try {
                    Thread.sleep(10000);

                } catch (InterruptedException ie) {
                    System.out.println("++++++++++++++++ Problema della Sleep() del pet");
                    ie.printStackTrace();
                }
                room.exitRoom(pet, nThread);
            }

/*
            else
                try {
                    //Questo wait si può fare sia dall'istanza sincronizzata, sia dal thread (RICORDARSI SYNCHRONIZE)
                    synchronized (room) {
                        room.wait();
                    }
                }

                catch (InterruptedException ie){
                    System.out.println("++++++++++++++++ Problema della Wait() del pet");
                    ie.printStackTrace();
                }
*/
        }
    }

}
