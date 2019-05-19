public class SalaCuccioli {
    private int cani, gatti;

    public SalaCuccioli (){
        this.cani = 0;
        this.gatti=0;
    }

    public synchronized boolean entrata (int s) {
        if (s==0 && cani < 4 && gatti == 0){
            cani++;
            System.out.println("\n\nEntrato un cane \n cani:"+cani+" gatti:"+gatti);
            return true;
        }
        else if (s==1 && cani == 0 && gatti == 0){
            gatti++;
            System.out.println("\n\nEntrato un gatto \n cani:"+cani+" gatti:"+gatti);
            return true;
        }
        else
            return false;
    }


    public synchronized void uscita (int s){
            switch (s){
                case 0: cani--;
                        System.out.println("\n\nUscito un cane \n cani:"+cani+" gatti:"+gatti);
                        this.notifyAll();
                        break;
                case 1: gatti--;
                        System.out.println("\n\nUscito un gatto \n cani:"+cani+" gatti:"+gatti);
                        this.notifyAll();
                        break;
            }
    }
}
