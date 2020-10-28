package Resource;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Word {

    public String key;
    public String def;

    public Word(String word, String def){
        key = word;
        this.def = def;
    }

   public Word(){}

}
