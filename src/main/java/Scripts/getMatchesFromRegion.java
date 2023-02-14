package Scripts;

import BO.match;
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

/**
 * This class is used to perform requests to Riot API to retrieve MatchID of games played by the best players from
 * different regions. The matchId are then inserted to the MatchsIDcurrent.
 *
 */
public class getMatchesFromRegion extends apiRequester {

    private final ArrayList<match> allMatchesFromCurrentPlayer = new ArrayList<>();

    final static String baseURL = "api.riotgames.com/tft/match/v1/matches/by-puuid/";

    public getMatchesFromRegion() throws IOException {
        super();
    }

    /**
     *Retrive matchIDs of nbMatchsPerPlayer last match played by players in PUUIDS list from Region r and insert them into MatchsIDcurrent
     * @param r
     * @param nbMatchsPerPlayer
     * @param PUUIDS
     * @throws MalformedURLException
     */
    public void getMatchs(regionUtils.region r, int nbMatchsPerPlayer, List<String> PUUIDS) throws MalformedURLException {

        String URL = regionUtils.getURLfromRegion(r,baseURL);
        Gson gson = gson();
        try (WebClient webClient = new WebClient()) {
            WebRequest webRequest;
            String URL2;
            for (String PUUID : PUUIDS){
                URL2 = URL + PUUID + "/ids?start=0&count=" + nbMatchsPerPlayer;
                webRequest =  new WebRequest(new URL(URL2), HttpMethod.GET );
                setHeader(webRequest);

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
                    allMatchesFromCurrentPlayer.add(m);
                }
                matchesDAO matchesDAO = new matchesDAO();
                matchesDAO.insert(allMatchesFromCurrentPlayer);
                allMatchesFromCurrentPlayer.clear();
                cptrequest++;
                if (cptrequest%100 == 0){
                    Utils.pause.pause(2);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
