package DAL;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class compStatsDAO {
    static final String insertCompStats = "INSERT INTO CompoStats values (?,?,?,?,?,?,?,?,?)";
    private static final String clearTable = "DELETE FROM CompoStats";

    public void insertCompStatsFromMap(Map<List<String>,int[]> Stats) throws SQLException {
        Connection cnx = null;
        PreparedStatement rqt;
        try {
            cnx = database.openCo();
            for (Map.Entry<List<String>, int[]> entry : Stats.entrySet()) {
                rqt = cnx.prepareStatement(insertCompStats);
                rqt.setString(1, entry.getKey().get(0));
                rqt.setString(9, entry.getKey().get(1));
                rqt.setInt(2,entry.getValue()[0]);
                rqt.setInt(3,entry.getValue()[1]);
                rqt.setInt(4,entry.getValue()[2]);
                rqt.setInt(5,entry.getValue()[3]);
                Float avg = (float)entry.getValue()[1] / entry.getValue()[0];
                Float Top4 = (float)entry.getValue()[3] *100 / entry.getValue()[0];
                Float Top1 = (float)entry.getValue()[2] *100 / entry.getValue()[0];
                rqt.setFloat(6,avg);
                rqt.setFloat(7,Top4);
                rqt.setFloat(8,Top1);
                System.out.println( " Requete insertion comp stats  :");
                System.out.println(entry.getKey().get(0) + " " + entry.getValue()[0] + " " + entry.getValue()[1] + " " + entry.getValue()[1] + " " + entry.getValue()[2] + " " + entry.getValue()[3]
                                   + " " + avg + " " +Top4 +  " " + Top1 + " " + entry.getKey().get(1));

                rqt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void clearCompoStats() throws SQLException {
        Connection cnx = database.openCo();
        Statement rqt = cnx.createStatement();
        rqt.executeUpdate(clearTable);
        cnx.close();
    }
}
