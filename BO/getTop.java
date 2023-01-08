package BO;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.*;

import DAL.leagueDAO;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

public class getTop{

    private boolean jsonSerializeNulls = true;
    
    final static String apiKey ="RGAPI-92e7ff3c-bfbe-46d7-b65c-069d15d16b5c";

    final static String challengersEUW = "https://euw1.api.riotgames.com/tft/league/v1/challenger";
    final static String challengersNA = "https://na1.api.riotgames.com/tft/league/v1/challenger";
    final static String challengersKR = "https://kr.api.riotgames.com/tft/league/v1/challenger";

    final static String grandmastersEUW = "https://euw1.api.riotgames.com/tft/league/v1/grandmaster";
    final static String mastersEUW = "https://euw1.api.riotgames.com/tft/league/v1/master";

    final static String sumInfoByNameNA = "https://na1.api.riotgames.com/tft/summoner/v1/summoners/by-name/";
    final static String sumInfoByNameEUW = "https://euw1.api.riotgames.com/tft/summoner/v1/summoners/by-name/";
    final static String sumInfoByNameKR = "https://kr.api.riotgames.com/tft/summoner/v1/summoners/by-name/";

    static public enum region {
        EUW,KR,NA
    }

    final static Map<String, String> headerAPI = new HashMap<String, String>();

    public getTop(){
        headerAPI.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.114 Safari/537.36");
        headerAPI.put("Accept-Language","fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
        headerAPI.put("Accept-Charset","application/x-www-form-urlencoded; charset=UTF-8");
        headerAPI.put("Origin","https://developer.riotgames.com");
        headerAPI.put("X-Riot-Token",apiKey);

    }

    private void getChallengers(region r) throws IOException {

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
                    pla = gson.fromJson(jsonResponse2, playerFromApi.class);
                    player.setPUUID(pla.puuid);
                    player.setRégion(r.toString());
                    System.out.println(player.PUUID);
                    System.out.println(player.région);
                }
                catch(FailingHttpStatusCodeException e){
                    System.out.println("BUG");
                    
                }             
        }
        leagueDAO leagueDAO = new leagueDAO();
        leagueDAO.insert(challs);
        System.out.println("\n yooooooooooooooooooo n");
        } catch (IOException e) {
            //
        }



    }

    public void clearTable(region r){
        
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



    public static void main(String[] args){
        getTop test = new getTop();
        try {
            test.getChallengers(region.NA);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
