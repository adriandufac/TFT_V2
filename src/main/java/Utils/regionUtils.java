package Utils;

public class regionUtils {
    static public enum region {
        EUW,KR,NA
    }

    public static String getURLfromRegion(regionUtils.region r, String baseURL) {
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
}
