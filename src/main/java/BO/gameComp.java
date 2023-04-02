package BO;

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
            System.out.println(Traits.get(prop.getProperty("trait"+i)));
        }
    }

    public void addToTraits (String trait,int number) {
        // add number to map
        Traits.merge(trait,number,Integer::sum);
    }

    public void changeTraitValueWithTreeshhold() throws IOException {
        Properties prop = new Properties();
        InputStream input = gameComp.class.getResourceAsStream("/traits.properties");
        System.out.println("input gamecomp: " + input);
        prop.load(input);
        for (int i=0;i<Integer.parseInt(prop.getProperty("nbTraits"));i++) {
           // Traits.put(prop.getProperty("trait"+i),0);
            int current = Traits.get(prop.getProperty("trait"+i));
            int withTreshhold = 0;
            String tresholds[] = prop.getProperty("trait"+i+"treshholds").split("/");
            int tresholdsInt[] = new int[tresholds.length];
            for (int j = 0 ; j<tresholds.length; j++) {
                tresholdsInt[j] = Integer.parseInt(tresholds[j]);
            }
            for (int k = 0 ; k<tresholdsInt.length; k++) {
                if (current >= tresholdsInt[k]) {
                    withTreshhold++;
                }
            }
            Traits.put(prop.getProperty("trait"+i),withTreshhold);
        }
    }
}
