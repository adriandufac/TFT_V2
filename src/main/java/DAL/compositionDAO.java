package DAL;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class compositionDAO {
    static final String insertComposition = "INSERT INTO Composition values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public void insertCompositionFromClusters(List<Cluster<DoublePoint>> clusters) throws SQLException {
        Connection cnx = null;
        PreparedStatement rqt;
        try {
            cnx = database.openCo();
            int j=0;
            int k = 0;
            for (Cluster<DoublePoint> c : clusters) {
                rqt = cnx.prepareStatement(insertComposition);
                for (DoublePoint point : c.getPoints()) {
                    rqt.setString(1, COMPO_MAP.get(j)+k);
                    for (int i = 0; i < point.getPoint().length; i++) {
                        rqt.setInt(i+2, (int)(point.getPoint()[i]));
                    }
                    System.out.println(point.getPoint());
                    k++;
                    rqt.executeUpdate();

                }
                k=0;
                System.out.println(" *********************************** \n");
                j++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            cnx.close();
        }
    }
    static final private Map<Integer, String> COMPO_MAP = new HashMap<>();
    static {
        COMPO_MAP.put(0,"recons Threat");
        COMPO_MAP.put(1,"8 brawlers");
        COMPO_MAP.put(2,"6 brawlers");
        COMPO_MAP.put(3,"8 duelists");
        COMPO_MAP.put(4,"recons starguardian");
        COMPO_MAP.put(5,"4Ace mech");
        COMPO_MAP.put(6,"Surshot mech");
        COMPO_MAP.put(7,"6 duelists");
        COMPO_MAP.put(8,"6laserCorps");
    }

}
