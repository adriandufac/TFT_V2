package Utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;

public class jsonUtils {
    public static Gson gson(boolean jsonSerializeNulls){

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
