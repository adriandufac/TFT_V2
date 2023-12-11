package ApiObjects;

import java.util.TreeMap;

public class championFromJson {
    public TreeMap<String, champion> data;

    public class champion {
        public String id;
        public String name;
        public int tier;
    }
}
