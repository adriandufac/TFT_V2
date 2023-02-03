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
    static final String bigSELECT = "SELECT DetailsGame.MatchID, DetailsGame.PUUID, Classement,Trait1,COUNT(Trait1),Trait2,COUNT(Trait2)," +
                                    "Trait3,COUNT(Trait3) FROM DetailsGame JOIN ChampGame ON DetailsGame.MatchID = " +
                                    "ChampGame.MatchID AND DetailsGame.PUUID= ChampGame.PUUID JOIN Champion ON ChampGame.NomAPI" +
                                    " = Champion.NomAPI GROUP BY MatchID,PUUID,Trait1";

    public void cluster (regionUtils.region r) throws SQLException {
        DBSCANClusterer clusterer = new DBSCANClusterer(1.2,100);
        List<DoublePoint> points = new ArrayList<DoublePoint>();
        ArrayList<gameComp> Comps = selectGameComps(r);
       for (gameComp gc : Comps ){
           double[] d = new double[19];
           for(int i = 0; i < 19 ; i ++){
               d[i]= gc.Traits.get(TRAITS_MAPS.get(i));
           }
           points.add(new DoublePoint(d));
       }

        List<Cluster<DoublePoint>> cluster =  clusterer.cluster(points);
        System.out.println(" AVEC 0 : ");
        for(Cluster<DoublePoint> c: cluster){
            System.out.println(c.getPoints().get(0));
            System.out.println(c.getPoints().size());
        }
    }
    public ArrayList<gameComp> selectGameComps (Utils.regionUtils.region r) throws SQLException {
        Connection cnx = null;
        PreparedStatement rqt;
        ArrayList<gameComp> Comps= new ArrayList<>();
        ResultSet rs;
        try {
            cnx = database.openCo();
            rqt = cnx.prepareStatement(bigSELECT);
            System.out.println("executing big select");
            rs = rqt.executeQuery();
            System.out.println("end of executing big select");
            rs.next();
            String PUUIDANDMatchID = (rs.getString("PUUID")+ rs.getString("matchID"));
            gameComp g = new gameComp(rs.getString("PUUID"),rs.getString("matchID"),rs.getInt("Classement"));
            g.addToTraits(rs.getString("Trait1"),rs.getInt("COUNT(Trait1)"));
            g.addToTraits(rs.getString("Trait2"),rs.getInt("COUNT(Trait2)"));
            if (rs.getString("Trait3") != null) {
                g.addToTraits(rs.getString("Trait3"),rs.getInt("COUNT(Trait3)"));
            }
            while (rs.next()){
                String Current = (rs.getString("PUUID")+ rs.getString("matchID"));
                if (Current != PUUIDANDMatchID) {
                    Comps.add(g);
                     g = new gameComp(rs.getString("PUUID"),rs.getString("matchID"),rs.getInt("Classement"));
                }
                g.addToTraits(rs.getString("Trait1"),rs.getInt("COUNT(Trait1)"));
                g.addToTraits(rs.getString("Trait2"),rs.getInt("COUNT(Trait2)"));
                if (rs.getString("Trait3") != null) {
                    g.addToTraits(rs.getString("Trait3"),rs.getInt("COUNT(Trait3)"));
                }
                PUUIDANDMatchID = (rs.getString("PUUID")+ rs.getString("matchID"));
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
    static {
        TRAITS_MAPS.put(0,"Ace");
        TRAITS_MAPS.put(1,"Admin");
        TRAITS_MAPS.put(2,"Aegis");
        TRAITS_MAPS.put(3,"AnimaSquad");
        TRAITS_MAPS.put(4,"Brawler");
        TRAITS_MAPS.put(5,"Civilian");
        TRAITS_MAPS.put(6,"Defender");
        TRAITS_MAPS.put(7,"Duelist");
        TRAITS_MAPS.put(8,"Gadgeteen");
        TRAITS_MAPS.put(9,"Forecaster");
        TRAITS_MAPS.put(10,"OxForce");
        TRAITS_MAPS.put(11,"Supers");
        TRAITS_MAPS.put(12,"Recon");
        TRAITS_MAPS.put(13,"SpellSlinger");
        TRAITS_MAPS.put(14,"Mascot");
        TRAITS_MAPS.put(15,"Hearth");
        TRAITS_MAPS.put(16,"LaserCorps");
        TRAITS_MAPS.put(17,"MechPrime");
        TRAITS_MAPS.put(18,"Underground");

    }
}

