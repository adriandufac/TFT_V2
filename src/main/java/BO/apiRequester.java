package BO;

import com.google.gson.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class apiRequester {
    protected boolean jsonSerializeNulls = true;
    protected int cptrequest;
    static final Map<String, String> headerAPI = new HashMap();

    public apiRequester() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("src/main/java/apikey.properties"));
        headerAPI.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.114 Safari/537.36");
        headerAPI.put("Accept-Language","fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
        headerAPI.put("Accept-Charset","application/x-www-form-urlencoded; charset=UTF-8");
        headerAPI.put("Origin","https://developer.riotgames.com");
        headerAPI.put("X-Riot-Token",prop.getProperty("key"));
        cptrequest = 1;
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
}
