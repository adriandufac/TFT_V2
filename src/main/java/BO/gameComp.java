package BO;

import org.apache.commons.math3.ml.clustering.Clusterable;

import java.util.HashMap;
import java.util.Map;

public class gameComp  {
    public String MatchID;
    public String PUUID;
    public int Classement;
    public Map<String, Integer> Traits = new HashMap<String, Integer>();


    public gameComp(String puuid,String matchID, int classement) {
        this.PUUID=puuid;
        this.MatchID=matchID;
        this.Classement = classement;
        Traits.put("Ace",0);
        Traits.put("Admin",0);
        Traits.put("Aegis",0);
        Traits.put("AnimaSquad",0);
        Traits.put("Arsenal",0);
        Traits.put("Brawler",0);
        Traits.put("Civilian",0);
        Traits.put("Corrupted",0);
        Traits.put("Defender",0);
        Traits.put("Duelist",0);
        Traits.put("Forecaster",0);
        Traits.put("Gadgeteen",0);
        Traits.put("Hacker",0);
        Traits.put("Heart",0);
        Traits.put("LaserCorps",0);
        Traits.put("Mascot",0);
        Traits.put("MechPrime",0);
        Traits.put("OxForce",0);
        Traits.put("Prankster",0);
        Traits.put("Recon",0);
        Traits.put("Renegade",0);
        Traits.put("SpellSlinger",0);
        Traits.put("StarGuardian",0);
        Traits.put("Supers",0);
        Traits.put("Sureshot",0);
        Traits.put("Threat",0);
        Traits.put("Underground",0);
    }

    public void addToTraits (String trait,int number) {
        // add number to map
        Traits.merge(trait,number,Integer::sum);
    }
}
