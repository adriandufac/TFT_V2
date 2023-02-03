import BO.*;
import DAL.CompAnalyses;
import Utils.regionUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Scripts {
    public static void main(String[] args){

        if ( "getTop".equals(args[0])) {
            getTop test = new getTop();
            try {
                test.getChallengers(Utils.regionUtils.region.NA);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if ( "getMatches".equals(args[0])) {
            getMatchesFromRegion test2 = new getMatchesFromRegion();
            try {
                List<String> PUUIDS = test2.getPUUIDFromDB(regionUtils.region.NA);
                test2.getMatchs(regionUtils.region.NA,20,PUUIDS);
            } catch (MalformedURLException | SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if ( "getMatchesDetails".equals(args[0])) {
            getMatchesDetailsFromRegion test3 = new getMatchesDetailsFromRegion();
            try {
                test3.getMatchDetails(regionUtils.region.NA);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        if ( "getComps".equals(args[0])) {
            CompAnalyses test4 = new CompAnalyses();
            try {
                test4.cluster(regionUtils.region.NA);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
