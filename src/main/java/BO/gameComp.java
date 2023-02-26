package BO;

import Scripts.apiRequester;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        InputStream input = gameComp.class.getResourceAsStream("/traits.properties");
        System.out.println("input gamecomp: " + input);
        prop.load(input);
        this.PUUID = puuid;
        this.MatchID = matchID;
        this.Classement = classement;
        for (int i=0;i<Integer.parseInt(prop.getProperty("nbTraits"));i++) {
            Traits.put(prop.getProperty("trait"+i),0);
        }
    }

    public void addToTraits (String trait,int number) {
        // add number to map
        Traits.merge(trait,number,Integer::sum);
    }
}
