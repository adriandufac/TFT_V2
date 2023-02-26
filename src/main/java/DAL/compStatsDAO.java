package DAL;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class compStatsDAO {
    static final String insertCompStats = "INSERT INTO CompoStats values (?,?,?,?,?,?,?,?,?)";

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
                rqt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
