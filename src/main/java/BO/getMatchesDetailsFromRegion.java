package BO;

import ApiObjects.matchFromApi;
import DAL.matchDetailsDAO;
import DAL.matchesDAO;
import Utils.regionUtils;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.google.gson.*;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class getMatchesDetailsFromRegion extends apiRequester {
    final static String baseURL = "api.riotgames.com/tft/match/v1/matches/";

    static final Map<String, String> headerAPI = new HashMap();
    public getMatchesDetailsFromRegion() throws IOException {
        super();
    }
    public void getMatchDetails(regionUtils.region r) throws IOException {
        String URL = regionUtils.getURLfromRegion(r,baseURL);
        Gson gson = gson();
        matchDetailsDAO matchDetailsDAO = new matchDetailsDAO();
        matchesDAO matchesDAO = new matchesDAO();
        List<String> matchesID = matchesDAO.selectmatchsIDSFromRegion(regionUtils.region.NA);
        try (WebClient webClient = new WebClient()) {
            WebRequest webRequest;
            String URL2;
            for (String matchID : matchesID) {
                URL2 = URL + matchID;
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
                matchFromApi match = gson.fromJson(jsonResponse, matchFromApi.class);
                System.out.println(match.info.participants[0].puuid);
                matchDetailsDAO.insert(match);
                cptrequest++;
                if (cptrequest%100 == 0){
                    System.out.println(" PAUSE 2 MIN");
                    Thread.sleep(120000);
                }
            }
        }
        catch (Exception e) {
        System.err.println(e.getMessage());
        }
    }


}
