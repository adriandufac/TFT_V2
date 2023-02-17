package DAL;

import Scripts.apiRequester;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class compositionDAO {
    static final String insertComposition = "INSERT INTO Composition values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    static final String selectComposition = "SELECT Nom,Ace,Admin,AnimaSquad,Arsenal,Brawler,Civilian,Corrupted,Defender,Duelist,Forecaster," +
            "Gadgeteen,Hacker,Heart,LaserCorps,Mascot,MechPrime,OxForce,Prankster,Recon,Renegade,SpellSlinger,StarGuardian," +
            "Supers,Sureshot,Threat,Underground FROM Composition";
    public void insertCompositionFromClusters(List<Cluster<DoublePoint>> clusters ,boolean first) throws SQLException {
        Connection cnx = null;
        PreparedStatement rqt;
        try {
            cnx = database.openCo();
            int j=0;
            int k = 0;
            for (Cluster<DoublePoint> c : clusters) {
                rqt = cnx.prepareStatement(insertComposition);
                for (DoublePoint point : c.getPoints()) {
                    if (first) {
                        rqt.setString(1, "COMPO"+j+k);
                    } else {
                        rqt.setString(1, COMPO_MAP.get(j)+k);
                    }
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
    public  ArrayList<String[]> selectCompositions() throws IOException, SQLException {
        Connection cnx = null;
        PreparedStatement rqt;
        Properties prop = new Properties();
        InputStream input = apiRequester.class.getResourceAsStream("/apikey.properties");
        System.out.println("input: " + input);
        prop.load(input);
        ArrayList<String[]> Comps= new ArrayList<>();
        ResultSet rs;
        try {
            cnx = database.openCo();
            rqt = cnx.prepareStatement(selectComposition);
            rs = rqt.executeQuery();
            while (rs.next()) {
                String comp[] = new String[(int)prop.get("nbTraits")+1];
                comp[0] = rs.getString("Nom");
                for (int i=1;i<(int)prop.get("nbTraits");i++) {
                    comp[i] = String.valueOf(rs.getInt((String)prop.get("Traits"+(i-1))));
                }
                Comps.add(comp);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
            }
        }
        return Comps;
    }

    static final public Map<Integer, String> COMPO_MAP = new HashMap<>();
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
