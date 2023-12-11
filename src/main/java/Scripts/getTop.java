package Scripts;

import ApiObjects.playerFromApi;
import BO.leagueClass;
import DAL.leagueDAO;
import Utils.regionUtils;
import com.gargoylesoftware.htmlunit.*;
import com.google.gson.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;


/**
 * This class is used to perform requests to Riot API to retrive PUUIDs of the best players across the diferents regions
 * The PUUID are then inserted into the "Joueurs" table
 */
public class getTop extends riotApiRequester {


    final static String challengersEUW = "https://euw1.api.riotgames.com/tft/league/v1/challenger";
    final static String challengersNA = "https://na1.api.riotgames.com/tft/league/v1/challenger";
    final static String challengersKR = "https://kr.api.riotgames.com/tft/league/v1/challenger";

    final static String grandmastersEUW = "https://euw1.api.riotgames.com/tft/league/v1/grandmaster";
    final static String mastersEUW = "https://euw1.api.riotgames.com/tft/league/v1/master";

    final static String sumInfoByNameNA = "https://na1.api.riotgames.com/tft/summoner/v1/summoners/by-name/";
    final static String sumInfoByNameEUW = "https://euw1.api.riotgames.com/tft/summoner/v1/summoners/by-name/";
    final static String sumInfoByNameKR = "https://kr.api.riotgames.com/tft/summoner/v1/summoners/by-name/";



    public getTop() throws IOException {
        super();
    }

    /**
     * Retrieves PUUID of challengers players from the selected Region and insert into "Joueurs" table
     * 1 request to get the list of challs and then 1 request for each player to get the PUUID
     *
     * @param r
     * @throws IOException
     */
    public void getChallengers(regionUtils.region r) throws IOException {

    try (WebClient webClient = new WebClient()) {
        WebRequest webRequest;
        WebRequest webRequest2;
        switch (r){
            case EUW:
                webRequest = new WebRequest(new URL(challengersEUW), HttpMethod.GET );
                break;

            case NA:
                webRequest = new WebRequest(new URL(challengersNA), HttpMethod.GET );
                break;

            case KR:
                webRequest = new WebRequest(new URL(challengersKR), HttpMethod.GET );

                default:
                webRequest = new WebRequest(new URL(challengersEUW), HttpMethod.GET );
                break;
        }
        
        Gson gson = Utils.jsonUtils.gson(jsonSerializeNulls);
        setHeader(webRequest);
        Page page = webClient.getPage(webRequest);
        String jsonResponse;

        jsonResponse = page.getWebResponse().getContentAsString();
        System.out.println(jsonResponse);
        cptrequest++;
        leagueClass challs = gson.fromJson(jsonResponse, leagueClass.class);
        System.out.println("****************************\n");
        System.out.println(challs.entries.get(challs.entries.size()-1).summonerName);
        for (BO.player player : challs.entries){
            
            switch (r){
                case EUW:
                    webRequest2 = new WebRequest(new URL(sumInfoByNameEUW+player.getName()), HttpMethod.GET );
                    break;

                case NA:
                    webRequest2 = new WebRequest(new URL(sumInfoByNameNA+player.getName()), HttpMethod.GET );
                    break;

                case KR:
                    webRequest2 = new WebRequest(new URL(sumInfoByNameKR+player.getName()), HttpMethod.GET );
                    break;

                    default:
                    webRequest2 = new WebRequest(new URL(sumInfoByNameEUW+player.getName()), HttpMethod.GET );
                    break;
            }
            setHeader(webRequest2);
                
                try{

                    Page page2 = webClient.getPage(webRequest2);
                    String jsonResponse2;

                    playerFromApi pla;
                    jsonResponse2 = page2.getWebResponse().getContentAsString();
                    System.out.println("REQUETE NUM : " +cptrequest);
                    System.out.println(webRequest2);
                    System.out.println(jsonResponse2);
                    pla = gson.fromJson(jsonResponse2, playerFromApi.class);
                    player.setPUUID(pla.puuid);
                    player.setRegion(r.toString());
                    System.out.println(player.getName());
                    System.out.println(player.PUUID);
                    System.out.println(player.region);

                }
                catch (FailingHttpStatusCodeException e) {
                    if (e.getMessage().contains("404")) {
                        System.out.println("Summoner not found (rename) ? ");
                        cptrequest ++;
                    }
                }
            cptrequest++;
            if (cptrequest%100 == 0){
                Utils.pause.pause(2);
            }
        }
        leagueDAO leagueDAO = new leagueDAO();
        leagueDAO.insert(challs);
        } catch (IOException e) {
            System.out.println("BUG");
        } catch (SQLException | InterruptedException e) {
        throw new RuntimeException(e);
        }
    }
}
