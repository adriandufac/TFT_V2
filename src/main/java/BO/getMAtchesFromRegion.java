package BO;

import DAL.leagueDAO;
import Utils.regionUtils;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class getMAtchesFromRegion {
    private boolean jsonSerializeNulls = true;

    final static String apiKey ="RGAPI-92e7ff3c-bfbe-46d7-b65c-069d15d16b5c";
    static final Map<String, String> headerAPI = new HashMap();
    public getMAtchesFromRegion(){
        headerAPI.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.114 Safari/537.36");
        headerAPI.put("Accept-Language","fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
        headerAPI.put("Accept-Charset","application/x-www-form-urlencoded; charset=UTF-8");
        headerAPI.put("Origin","https://developer.riotgames.com");
        headerAPI.put("X-Riot-Token",apiKey);

    }
    private void getMatchs(regionUtils.region r, int nbMatchsPerPlayer){

        try (WebClient webClient = new WebClient()) {
            WebRequest webRequest;
        }
    }

    private List<String> getPUUIDFromDB(regionUtils.region r){
        leagueDAO leagueDAO = new leagueDAO();
        return leagueDAO.selectPUUIDSFromRegion(r);
    }
    public static void main(String[] args){
        getMAtchesFromRegion test = new getMAtchesFromRegion();
        test.getPUUIDFromDB(regionUtils.region.NA);
    }

}
