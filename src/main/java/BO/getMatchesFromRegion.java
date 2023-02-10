package BO;

import DAL.matchesDAO;
import Utils.regionUtils;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class getMatchesFromRegion extends apiRequester {

    private final ArrayList<match> matchesAll = new ArrayList<>();

    final static String baseURL = "api.riotgames.com/tft/match/v1/matches/by-puuid/";


    public getMatchesFromRegion() throws IOException {
        super();
    }
    public void getMatchs(regionUtils.region r, int nbMatchsPerPlayer, List<String> PUUIDS) throws MalformedURLException {

        String URL = regionUtils.getURLfromRegion(r,baseURL);
        Gson gson = gson();
        try (WebClient webClient = new WebClient()) {
            WebRequest webRequest;
            String URL2;
            for (String PUUID : PUUIDS){
                URL2 = URL + PUUID + "/ids?start=0&count=" + nbMatchsPerPlayer;
                webRequest =  new WebRequest(new URL(URL2), HttpMethod.GET );
                for(Map.Entry<String, String> entry : headerAPI.entrySet() ){
                    String name = entry.getKey();
                    String value = entry.getValue();
                    // params.add(new NameValuePair(name, value));
                    webRequest.setAdditionalHeader(name, value);
                }

                Page page = webClient.getPage(webRequest);
                System.out.println(webRequest);
                String jsonResponse;
                jsonResponse = page.getWebResponse().getContentAsString();
                System.out.println(jsonResponse);
                Type listType = new TypeToken<ArrayList<String>>(){}.getType();
                ArrayList<String> list = gson.fromJson(jsonResponse, listType);
                for (String match:list) {
                    match m = new match(match,r);
                    System.out.println(m.r.toString());
                    matchesAll.add(m);
                }
                matchesDAO matchesDAO = new matchesDAO();
                matchesDAO.insert(matchesAll);
                matchesAll.clear();
                cptrequest++;
                if (cptrequest%100 == 0){
                    System.out.println(" PAUSE 2 MIN");
                    Thread.sleep(120000);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
