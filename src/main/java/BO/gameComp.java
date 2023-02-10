package BO;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class gameComp  {
    public String MatchID;
    public String PUUID;
    public int Classement;
    public Map<String, Integer> Traits = new HashMap<>();


    public gameComp(String puuid,String matchID, int classement) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("traits.properties"));
        this.PUUID = puuid;
        this.MatchID = matchID;
        this.Classement = classement;
        for (int i=0;i<(int)prop.get("nbTraits");i++) {
            Traits.put((String)prop.get("trait"+i),0);
        }
    }

    public void addToTraits (String trait,int number) {
        // add number to map
        Traits.merge(trait,number,Integer::sum);
    }
}
