package Resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Dict {

    @XmlElement(name="dictionary")
    public HashMap<String, String> dict;
    private static Dict instance;

    private Dict(){
        dict = new HashMap<String, String>();
    }

    public synchronized static Dict getInstance(){
        if(instance==null)
            instance=new Dict();
        return instance;
    }

    //post
    public synchronized void add (Word w){
        dict.put(w.key,w.def);
    }

    //get
    public synchronized String getDef (String s){
        if(dict.containsKey(s))
            return dict.get(s);
        else
            return null;
    }

    //put
    public synchronized void update (Word w){
        dict.put(w.key, w.def);
    }

    //delete
    public synchronized void delete (String s){
        dict.remove(s);
    }
}
