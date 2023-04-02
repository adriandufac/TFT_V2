package Scripts;

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
import java.util.List;

/**
 * This class is used to perform requests to Riot API to retrieve all information of games in matchsIDCurrent table and
 * inserts those information in DetailsGame and ChampGame tables
 */
public class getMatchesDetailsFromRegion extends riotApiRequester {
    final static String baseURL = "api.riotgames.com/tft/match/v1/matches/";

    public getMatchesDetailsFromRegion() throws IOException {
        super();
    }

    /**
     * Retrieves information of games stocked in matchsIDCurrent  table and inserts in DetailsGame and ChampGame tables
     * @param r
     * @throws IOException
     */
    public void getMatchDetails(regionUtils.region r) throws IOException {
        String URL = regionUtils.getURLfromRegion(r,baseURL);
        Gson gson = Utils.jsonUtils.gson(jsonSerializeNulls);
        matchDetailsDAO matchDetailsDAO = new matchDetailsDAO();
        matchesDAO matchesDAO = new matchesDAO();
        List<String> matchesID = matchesDAO.selectmatchsIDSFromRegion(r);
        try (WebClient webClient = new WebClient()) {
            WebRequest webRequest;
            String URL2;
            for (String matchID : matchesID) {
                URL2 = URL + matchID;
                webRequest =  new WebRequest(new URL(URL2), HttpMethod.GET );
                setHeader(webRequest);
                Page page = webClient.getPage(webRequest);
                //System.out.println(webRequest);
                String jsonResponse;
                jsonResponse = page.getWebResponse().getContentAsString();
                //System.out.println(jsonResponse);
                matchFromApi match = gson.fromJson(jsonResponse, matchFromApi.class);
                //System.out.println(match.info.participants[0].puuid);
                matchDetailsDAO.insert(match);
                cptrequest++;
                if (cptrequest%100 == 0){
                    Utils.pause.pause(2);
                }
            }
        }
        catch (Exception e) {
        System.err.println(e.getMessage());
        }
    }

}
