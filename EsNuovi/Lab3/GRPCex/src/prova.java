import java.util.*;
import java.util.stream.Collectors;

public class prova {
    public static void main (String[] argv){
        int[] arr = {1, 2, 2};
        Hashtable<Integer, Integer> hashtable = new Hashtable<Integer, Integer>();
//        for (Integer i : arr)
//            hashtable.put(i, 0);
        for (Integer i : arr)
            hashtable.put(i, hashtable.get(i) + 1);

        int max = -1;
        int index = -1;
        for (Integer i: hashtable.keySet())
            if (hashtable.get(i) > max)
            {
                max = hashtable.get(i);
                index = i;
            }

        System.err.println(index);
       // return index;
    }
}
