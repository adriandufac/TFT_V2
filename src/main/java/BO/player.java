package BO;
public class player {

    String summonerId;
    String summonerName;
    public String PUUID;
    public String region;

    public void setPUUID(String PUUID){
        this.PUUID = PUUID;
    }

    public String getName(){
        return this.summonerName;
    }

    public void setRegion(String r){
        this.region =r;
    }
}
