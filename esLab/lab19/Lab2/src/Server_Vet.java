public class Server_Vet {
    public static void main (String[] argv) {

        int howMany = Integer.parseInt(argv[0]);
        int prob = Integer.parseInt(argv[1]);
        Room_Vet room = new Room_Vet();

        for (int i = 0; i < howMany; i++){
            if (i%prob == 0) {
                Thread thread = new Thread(new ThreadPet_Vet(i,0, room));
                thread.start();
            }
            else {
                Thread thread = new Thread(new ThreadPet_Vet(i,1, room));
                thread.start();
            }
        }
    }
}

