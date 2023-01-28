package BO;

import ApiObjects.playerFromApi;
import Utils.regionUtils;
import com.gargoylesoftware.htmlunit.*;
import com.google.gson.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class getTop{

    private boolean jsonSerializeNulls = true;

    public int cptrequest;
    
    final static String apiKey ="RGAPI-d19ed59e-5d63-41e7-9a2c-4240f8617c6c";

    final static String challengersEUW = "https://euw1.api.riotgames.com/tft/league/v1/challenger";
    final static String challengersNA = "https://na1.api.riotgames.com/tft/league/v1/challenger";
    final static String challengersKR = "https://kr.api.riotgames.com/tft/league/v1/challenger";

    final static String grandmastersEUW = "https://euw1.api.riotgames.com/tft/league/v1/grandmaster";
    final static String mastersEUW = "https://euw1.api.riotgames.com/tft/league/v1/master";

    final static String sumInfoByNameNA = "https://na1.api.riotgames.com/tft/summoner/v1/summoners/by-name/";
    final static String sumInfoByNameEUW = "https://euw1.api.riotgames.com/tft/summoner/v1/summoners/by-name/";
    final static String sumInfoByNameKR = "https://kr.api.riotgames.com/tft/summoner/v1/summoners/by-name/";



    final static Map<String, String> headerAPI = new HashMap<>();

    public getTop(){
        headerAPI.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.114 Safari/537.36");
        headerAPI.put("Accept-Language","fr-FR,fr;q=0.6");
        headerAPI.put("Accept-Charset","application/x-www-form-urlencoded; charset=UTF-8");
        headerAPI.put("Origin","https://developer.riotgames.com");
        headerAPI.put("X-Riot-Token",apiKey);
        cptrequest = 1;
    }

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
        
        Gson gson = gson();
        for(Entry<String, String> entry : headerAPI.entrySet() ){
            String name = entry.getKey();
            String value = entry.getValue();
            // params.add(new NameValuePair(name, value));
            webRequest.setAdditionalHeader(name, value);
        }
        //webRequest.setRequestParameters(params);
        
        
        Page page = webClient.getPage(webRequest);
        String jsonResponse;

        jsonResponse = page.getWebResponse().getContentAsString();
        System.out.println(jsonResponse);
        leagueClass challs = gson.fromJson(jsonResponse, leagueClass.class);
        System.out.println("****************************\n");
        System.out.println(challs.entries.get(challs.entries.size()-1).summonerName);
        for (player player : challs.entries){
            
            switch (r){
                case EUW:
                    webRequest2 = new WebRequest(new URL(sumInfoByNameEUW+player.getName()), HttpMethod.GET );
                    break;

                case NA:
                    webRequest2 = new WebRequest(new URL(sumInfoByNameNA+player.getName()), HttpMethod.GET );
                    break;

                case KR:
                    webRequest2 = new WebRequest(new URL(sumInfoByNameKR+player.getName()), HttpMethod.GET );

                    default:
                    webRequest2 = new WebRequest(new URL(sumInfoByNameEUW+player.getName()), HttpMethod.GET );
                    break;
            }
            for(Entry<String, String> entry : headerAPI.entrySet() ){
                String name = entry.getKey();
                String value = entry.getValue();
                // params.add(new NameValuePair(name, value));
                webRequest2.setAdditionalHeader(name, value);
            }
                
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
                    cptrequest++;
                    if (cptrequest%100 == 0){
                        System.out.println(" PAUSE 2 MIN");
                        Thread.sleep(120000);
                    }

                }
                catch(FailingHttpStatusCodeException e){
                    System.out.println("BUG");
                    
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        }
        /*leagueDAO leagueDAO = new leagueDAO();
        leagueDAO.insert(challs);*/
        System.out.println("\n yooooooooooooooooooo n");
        } catch (IOException e) {
            System.out.println("BUG");
        }



    }

    public void clearTable(){
        //TODO => vide la table joueurs
    }

    protected Gson gson(){

        // serialiazeNulls is required otherwise null values
        // are ommited from maps serialization, which will cause
        // some requests to fail
        GsonBuilder gsonBuilder = new GsonBuilder();
        if (jsonSerializeNulls) {
            gsonBuilder.serializeNulls();
        }

        // Avoid the scientific representation of Doubles.
        // This is coupled with filterResult
        gsonBuilder.registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
            @Override
            public JsonElement serialize(final Double src, final Type typeOfSrc, final JsonSerializationContext context) {
                BigDecimal value = BigDecimal.valueOf(src);

                return new JsonPrimitive(value);
            }
        });

        return gsonBuilder.create();
    }

    public void setJsonSerializeNulls(boolean jsonSerializeNulls) {
        this.jsonSerializeNulls = jsonSerializeNulls;
    }

}
