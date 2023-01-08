package BO;
public class player {

    String summonerId;
    String summonerName;
    public String PUUID;
    public String région;

    public void setPUUID(String PUUID){
        this.PUUID = PUUID;
    }

    public String getName(){
        return this.summonerName;
    }

    public void setRégion (String r){
        this.région=r;
    }
}
