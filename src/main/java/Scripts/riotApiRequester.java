package Scripts;

import com.gargoylesoftware.htmlunit.WebRequest;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class riotApiRequester {
    protected boolean jsonSerializeNulls = true;
    protected int cptrequest;
    static final Map<String, String> headerAPI = new HashMap();

    public riotApiRequester() throws IOException {
        Properties prop = new Properties();
        InputStream input = riotApiRequester.class.getResourceAsStream("/apikey.properties");
        System.out.println("input api requester: " + input);
        prop.load(input);
        headerAPI.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.114 Safari/537.36");
        headerAPI.put("Accept-Language","fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
        headerAPI.put("Accept-Charset","application/x-www-form-urlencoded; charset=UTF-8");
        headerAPI.put("Origin","https://developer.riotgames.com");
        headerAPI.put("X-Riot-Token",prop.getProperty("key"));
        cptrequest = 1;
    }

    public void setHeader (WebRequest request) {
        for(Map.Entry<String, String> entry : headerAPI.entrySet() ){
            String name = entry.getKey();
            String value = entry.getValue();
            request.setAdditionalHeader(name, value);
        }
    }

}
