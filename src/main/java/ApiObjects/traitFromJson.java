package ApiObjects;


import java.util.Map;
import java.util.TreeMap;

public class traitFromJson {
    public TreeMap<String, trait> data;

    public class trait {
        public String id;
        public String name;
    }
}
