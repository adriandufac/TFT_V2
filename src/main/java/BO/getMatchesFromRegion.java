package BO;

import DAL.leagueDAO;
import DAL.matchesDAO;
import Utils.regionUtils;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class getMatchesFromRegion {
    private boolean jsonSerializeNulls = true;
    private ArrayList<String> matchesAll = new ArrayList<>();
    final static String apiKey ="RGAPI-d19ed59e-5d63-41e7-9a2c-4240f8617c6c";
    final static String baseURL = "api.riotgames.com/tft/match/v1/matches/by-puuid/";
    public int cptrequest;
    static final Map<String, String> headerAPI = new HashMap();
    public getMatchesFromRegion(){
        headerAPI.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.114 Safari/537.36");
        headerAPI.put("Accept-Language","fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
        headerAPI.put("Accept-Charset","application/x-www-form-urlencoded; charset=UTF-8");
        headerAPI.put("Origin","https://developer.riotgames.com");
        headerAPI.put("X-Riot-Token",apiKey);
        cptrequest = 1;
        System.out.println("header construit");
    }
    public void getMatchs(regionUtils.region r, int nbMatchsPerPlayer, List<String> PUUIDS) throws MalformedURLException {

        String URL = getURLfromRegion(r);
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
                this.matchesAll.clear();
                this.matchesAll.addAll(list);
                matchesDAO matchesDAO = new matchesDAO();
                matchesDAO.insert(matchesAll);
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

    public List<String> getPUUIDFromDB(regionUtils.region r) throws SQLException {
        leagueDAO leagueDAO = new leagueDAO();
        return leagueDAO.selectPUUIDSFromRegion(r);
    }

    private String getURLfromRegion(regionUtils.region r) {
        switch (r) {
            case EUW :
                return "https://europe." + baseURL;
            case NA :
                return "https://americas." + baseURL;
            case KR :
                return "https://asia." + baseURL;
            default:
                return "https://europe." + baseURL;
        }
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
