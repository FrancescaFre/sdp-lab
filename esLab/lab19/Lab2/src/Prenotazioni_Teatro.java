public class Prenotazioni_Teatro {
    int posti;

    public Prenotazioni_Teatro (int n){
        posti = n;
    }

    public synchronized int RichiestaPosto() {
        if (posti > 0)
            return posti--;
        else
            return 0;
    }
}
