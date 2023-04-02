package Scripts;

import BO.gameComp;
import DAL.compositionDAO;
import DAL.matchDetailsDAO;
import Utils.regionUtils;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

/**
 * This class use DBscan algorithm to analyse all match details stocked in base from selected region and compute the most common
 * compositions used by players and stock in it in base.
 */
public class CompAnalyses {
    private  Map<Integer, String> TRAITS_MAPS = new HashMap<>();
    public CompAnalyses() throws IOException {
        Properties prop = new Properties();
        InputStream input = riotApiRequester.class.getResourceAsStream("/traits.properties");
        prop.load(input);
        for (int i=0;i< Integer.parseInt(prop.getProperty("nbTraits"));i++) {
            TRAITS_MAPS.put(i,prop.getProperty("trait"+i));
        }
    }

    /**
     * Analyse all games from selected region, compute most common compositions and insert them into Composition table.
     * Warning the DBscan Algo can't name the composition, in order to name the composition you need to put their name
     * in the COMPO_MAP in compositionDAO.
     * (Launch it one time with first parameter set to true in insert, see wich comp the algo got => file the MAP clear
     * the table, launch it again with parameter to false)
     * or UPDATE Composition
     * SET Nom = REPLACE(Nom, 'COMPO0', 'Yummi')
     * @param r
     * @throws SQLException
     * @throws IOException
     */
    public void cluster (regionUtils.region r) throws SQLException, IOException {
        matchDetailsDAO matchDetailsDAO = new matchDetailsDAO();
        compositionDAO compositionDAO = new compositionDAO();
        Properties prop = new Properties();
        InputStream input = riotApiRequester.class.getResourceAsStream("/traits.properties");
        prop.load(input);
        DBSCANClusterer clusterer = new DBSCANClusterer(1.4,60);
        List<DoublePoint> points = new ArrayList<>();
        ArrayList<gameComp> Comps = matchDetailsDAO.selectGameComps(r);
       for (gameComp gc : Comps ){
          // System.out.println(gc.Traits);
           double[] d = new double[Integer.parseInt(prop.getProperty("nbTraits"))];
           for(int i = 0; i < Integer.parseInt(prop.getProperty("nbTraits")) ; i ++){
               d[i]= gc.Traits.get(TRAITS_MAPS.get(i));
           }
           points.add(new DoublePoint(d));
       }
        List<Cluster<DoublePoint>> clusters =  clusterer.cluster(points);
        compositionDAO.insertCompositionFromClusters(clusters, true);
    }
    public void clusterWithTreshHold (regionUtils.region r) throws SQLException, IOException {
        matchDetailsDAO matchDetailsDAO = new matchDetailsDAO();
        compositionDAO compositionDAO = new compositionDAO();
        Properties prop = new Properties();
        InputStream input = riotApiRequester.class.getResourceAsStream("/traits.properties");
        prop.load(input);
        DBSCANClusterer clusterer = new DBSCANClusterer(1.1,35);
        List<DoublePoint> points = new ArrayList<>();
        ArrayList<gameComp> Comps = matchDetailsDAO.selectGameComps(r);
        for (gameComp gc : Comps ){
            double[] d = new double[Integer.parseInt(prop.getProperty("nbTraits"))];
            for(int i = 0; i < Integer.parseInt(prop.getProperty("nbTraits")) ; i ++){
                String tresholds[] = prop.getProperty("trait"+i+"treshholds").split("/");
                int tresholdsInt[] = new int[tresholds.length];
                int tmp = gc.Traits.get(TRAITS_MAPS.get(i));
                int value = 0;
                for (int j = 0 ; j<tresholds.length; j++) {
                    tresholdsInt[j] = Integer.parseInt(tresholds[j]);
                }
                for (int k = 0 ; k<tresholdsInt.length; k++) {
                    if (tmp >= tresholdsInt[k]) {
                        value++;
                    }
                }
                d[i]= value;
            }
            points.add(new DoublePoint(d));
        }
        List<Cluster<DoublePoint>> clusters =  clusterer.cluster(points);
        compositionDAO.insertCompositionFromClusters(clusters, false);
    }
}

