package DAL;

import BO.gameComp;
import Utils.regionUtils;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CompAnalyses {
    static final String selectGamesWithTraits = "SELECT MatchID,PUUID,Classement,Ace,Admin,Aegis,AnimaSquad,Arsenal,Brawler,Civilian,Corrupted," +
            "Defender,Duelist,Forecaster,Gadgeteen,Hacker,Heart,LaserCorps,Mascot,MechPrime,OxForce,Prankster,Recon,Renegade,SpellSlinger" +
            ",StarGuardian,Supers,Sureshot,Threat,Underground FROM DetailsGame";
    static final String insertComposition = "INSERT INTO Composition values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public void cluster (regionUtils.region r) throws SQLException {
        DBSCANClusterer clusterer = new DBSCANClusterer(1.4,30);
        List<DoublePoint> points = new ArrayList<DoublePoint>();
        ArrayList<gameComp> Comps = selectGameComps(r);
       for (gameComp gc : Comps ){
           double[] d = new double[27];
           for(int i = 0; i < 27 ; i ++){
               d[i]= gc.Traits.get(TRAITS_MAPS.get(i));

           }
           points.add(new DoublePoint(d));
       }
        List<Cluster<DoublePoint>> clusters =  clusterer.cluster(points);
        Connection cnx = null;
        PreparedStatement rqt;
        ResultSet rs;
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
               /* */
                System.out.println(" *********************************** \n");
                j++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<gameComp> selectGameComps (Utils.regionUtils.region r) throws SQLException {
        Connection cnx = null;
        PreparedStatement rqt;
        ArrayList<gameComp> Comps= new ArrayList<>();
        ResultSet rs;
        try {
            cnx = database.openCo();
            rqt = cnx.prepareStatement(selectGamesWithTraits);
            System.out.println("executing big select");
            rs = rqt.executeQuery();
            System.out.println("end of executing big select");
            while (rs.next()) {
                gameComp g = new gameComp(rs.getString("PUUID"),rs.getString("matchID"),rs.getInt("Classement"));
                g.addToTraits("Ace",rs.getInt("Ace"));
                g.addToTraits("Admin",rs.getInt("Admin"));
                g.addToTraits("Aegis",rs.getInt("Aegis"));
                g.addToTraits("AnimaSquad",rs.getInt("AnimaSquad"));
                g.addToTraits("Arsenal",rs.getInt("Arsenal"));
                g.addToTraits("Brawler",rs.getInt("Brawler"));
                g.addToTraits("Civilian",rs.getInt("Civilian"));
                g.addToTraits("Corrupted",rs.getInt("Corrupted"));
                g.addToTraits("Defender",rs.getInt("Defender"));
                g.addToTraits("Duelist",rs.getInt("Duelist"));
                g.addToTraits("Forecaster",rs.getInt("Forecaster"));
                g.addToTraits("Gadgeteen",rs.getInt("Gadgeteen"));
                g.addToTraits("Hacker",rs.getInt("Hacker"));
                g.addToTraits("Heart",rs.getInt("Heart"));
                g.addToTraits("LaserCorps",rs.getInt("LaserCorps"));
                g.addToTraits("Mascot",rs.getInt("Mascot"));
                g.addToTraits("MechPrime",rs.getInt("MechPrime"));
                g.addToTraits("OxForce",rs.getInt("OxForce"));
                g.addToTraits("Prankster",rs.getInt("Prankster"));
                g.addToTraits("Recon",rs.getInt("Recon"));
                g.addToTraits("Renegade",rs.getInt("Renegade"));
                g.addToTraits("SpellSlinger",rs.getInt("SpellSlinger"));
                g.addToTraits("StarGuardian",rs.getInt("StarGuardian"));
                g.addToTraits("Supers",rs.getInt("Supers"));
                g.addToTraits("Sureshot",rs.getInt("Sureshot"));
                g.addToTraits("Threat",rs.getInt("Threat"));
                g.addToTraits("Underground",rs.getInt("Underground"));
                Comps.add(g);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            if (!cnx.isClosed()) {
                cnx.close();
            }
        }
        return Comps;
    }
    static final private  Map<Integer, String> TRAITS_MAPS = new HashMap<Integer, String>();

    static final private  Map<Integer, String> COMPO_MAP = new HashMap<Integer, String>();
    static {
        TRAITS_MAPS.put(0,"Ace");
        TRAITS_MAPS.put(1,"Admin");
        TRAITS_MAPS.put(2,"Aegis");
        TRAITS_MAPS.put(3,"AnimaSquad");
        TRAITS_MAPS.put(4,"Arsenal");
        TRAITS_MAPS.put(5,"Brawler");
        TRAITS_MAPS.put(6,"Civilian");
        TRAITS_MAPS.put(7,"Corrupted");
        TRAITS_MAPS.put(8,"Defender");
        TRAITS_MAPS.put(9,"Duelist");
        TRAITS_MAPS.put(10,"Forecaster");
        TRAITS_MAPS.put(11,"Gadgeteen");
        TRAITS_MAPS.put(12,"Hacker");
        TRAITS_MAPS.put(13,"Heart");
        TRAITS_MAPS.put(14,"LaserCorps");
        TRAITS_MAPS.put(15,"Mascot");
        TRAITS_MAPS.put(16,"MechPrime");
        TRAITS_MAPS.put(17,"OxForce");
        TRAITS_MAPS.put(18,"Prankster");
        TRAITS_MAPS.put(19,"Recon");
        TRAITS_MAPS.put(20,"Renegade");
        TRAITS_MAPS.put(21,"SpellSlinger");
        TRAITS_MAPS.put(22,"StarGuardian");
        TRAITS_MAPS.put(23,"Supers");
        TRAITS_MAPS.put(24,"Sureshot");
        TRAITS_MAPS.put(25,"Threat");
        TRAITS_MAPS.put(26,"Underground");

        COMPO_MAP.put(0,"recons Threat");
        COMPO_MAP.put(1,"8 brawlers");
        COMPO_MAP.put(2,"6 brawlers");
        COMPO_MAP.put(3,"8 duelists");
        COMPO_MAP.put(4,"recons starguardian");
        COMPO_MAP.put(5,"4Ace mech");
        COMPO_MAP.put(6,"Surshot mech");
        COMPO_MAP.put(7,"Yummi");
        COMPO_MAP.put(8,"6laserCorps");
    }
}

