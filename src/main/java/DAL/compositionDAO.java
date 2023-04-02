package DAL;

import Scripts.riotApiRequester;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class compositionDAO {
    /*static final String insertComposition = "INSERT INTO Composition values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";*/

   /* static final String selectComposition = "SELECT Nom,Ace,Admin,Aegis,AnimaSquad,Arsenal,Brawler,Civilian,Corrupted,Defender,Duelist,Forecaster," +
            "Gadgeteen,Hacker,Heart,LaserCorps,Mascot,MechPrime,OxForce,Prankster,Recon,Renegade,SpellSlinger,StarGuardian," +
            "Supers,Sureshot,Threat,Underground FROM Composition";*/
    private static final String clearTable = "DELETE FROM Composition";
    public void insertCompositionFromClusters(List<Cluster<DoublePoint>> clusters ,boolean first) throws SQLException {
        Connection cnx = null;
        PreparedStatement rqt;
        try {
            cnx = database.openCo();
            int j=0;
            int k = 0;
            for (Cluster<DoublePoint> c : clusters) {
                rqt = cnx.prepareStatement(buildInsertString());
                for (DoublePoint point : c.getPoints()) {
                    if (first) {
                        rqt.setString(1, "COMPO"+j+"-"+k);
                        System.out.println("COMPO" +j+"-"+k);
                    } else {
                        rqt.setString(1, COMPO_MAP.get(j)+k);
                        System.out.println(COMPO_MAP.get(j)+j+"-"+k);
                    }
                    for (int i = 0; i < point.getPoint().length; i++) {
                        rqt.setInt(i+2, (int)(point.getPoint()[i]));
                    }
                    k++;
                    rqt.executeUpdate();

                }
                k=0;
                System.out.println(" *********************************** \n");
                j++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            cnx.close();
        }
    }
    private String buildSelectString() throws IOException {
        Properties prop = new Properties();
        InputStream input = riotApiRequester.class.getResourceAsStream("/traits.properties");
        prop.load(input);
        String select = "SELECT Nom,";
        int taille = Integer.parseInt(prop.getProperty("nbTraits"));
        for (int i=0;i<taille;i++) {
            select += prop.getProperty("trait" + i);
            if (i != taille-1) {
                select+=",";
            }
        }
        select += " FROM Composition";
        System.out.println(select);
        return select;
    }
    private String buildInsertString() throws IOException {
        Properties prop = new Properties();
        InputStream input = riotApiRequester.class.getResourceAsStream("/traits.properties");
        prop.load(input);
        String select = "INSERT INTO Composition values (?,";
        int taille = Integer.parseInt(prop.getProperty("nbTraits"));
        for (int i=0;i<taille;i++) {
            select += "?";
            if (i != taille-1) {
                select+=",";
            } else {
                select += ")";
            }
        }
        return select;
    }
    public  ArrayList<String[]> selectCompositions() throws IOException, SQLException {
        Connection cnx = null;
        PreparedStatement rqt;
        Properties prop = new Properties();
        InputStream input = riotApiRequester.class.getResourceAsStream("/traits.properties");
        prop.load(input);
        ArrayList<String[]> Comps= new ArrayList<>();
        ResultSet rs;
        try {
            cnx = database.openCo();
            rqt = cnx.prepareStatement(buildSelectString());
            rs = rqt.executeQuery();
            System.out.println("apres le select");
            while (rs.next()) {
                int taille = Integer.parseInt(prop.getProperty("nbTraits"));
                String comp[] = new String[taille+1];
                comp[0] = rs.getString("Nom");
                for (int i=1;i<taille+1;i++) {
                    String property = "trait" + (i-1);
                    String trait = prop.getProperty(property);
                    comp[i] = String.valueOf(rs.getInt(trait));
                }
                Comps.add(comp);
            }
        } catch (Exception e) {
            System.out.println("error in select composition + : " + e.getStackTrace());
            System.exit(0);
        }
        finally {
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
            }
        }
        return Comps;
    }
    public void clearComposition() throws SQLException {
        Connection cnx = database.openCo();
        Statement rqt = cnx.createStatement();
        rqt.executeUpdate(clearTable);
        cnx.close();
    }

    static final public Map<Integer, String> COMPO_MAP = new HashMap<>();
    static {
        COMPO_MAP.put(0,"Vex Mascot");
        COMPO_MAP.put(1,"Renegade Jhin Viego");
        COMPO_MAP.put(2,"Anima squad");
        COMPO_MAP.put(3,"Spellslinger OX force TF neeko");
        COMPO_MAP.put(4,"Duelist");
        COMPO_MAP.put(5,"InfiniTeam Sureshot");
        COMPO_MAP.put(6,"Draven Hacker");
        COMPO_MAP.put(7,"Heart Supers");
        COMPO_MAP.put(8,"Brawlers Admin");
        COMPO_MAP.put(9,"Brawlers Admoins hacker");
        COMPO_MAP.put(10,"Hearth Lee sin sona");
        COMPO_MAP.put(11,"Quickdraw lucian MF");
    }

}
