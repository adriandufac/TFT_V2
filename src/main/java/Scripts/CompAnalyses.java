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
import java.sql.SQLException;
import java.util.*;

/**
 * This class use DBscan algorithm to analyse all match details stocked in base from selected region and compute the most common
 * compositions used by players and stock in it in base.
 */
public class CompAnalyses {
    private Properties prop;
    private  Map<Integer, String> TRAITS_MAPS = new HashMap<>();
    public CompAnalyses() {
        prop = new Properties();
        try {
            prop.load(new FileInputStream("traits.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i=0;i<(int)prop.get("nbTraits");i++) {
            TRAITS_MAPS.put(i,(String)prop.get("trait"+i));
        }
    }

    /**
     * Analyse all games from selected region, compute most common compositions and insert them into Composition table.
     * Warning the DBscan Algo can't name the composition, in order to name the composition you need to put their name
     * in the COMPO_MAP in compositionDAO.
     * (Launch it one time with first parameter set to true in insert, see wich comp the algo got => file the MAP clear
     * the table, launch it again with parameter to false)
     * @param r
     * @throws SQLException
     * @throws IOException
     */
    public void cluster (regionUtils.region r) throws SQLException, IOException {
        matchDetailsDAO matchDetailsDAO = new matchDetailsDAO();
        compositionDAO compositionDAO = new compositionDAO();
        DBSCANClusterer clusterer = new DBSCANClusterer(1.4,30);
        List<DoublePoint> points = new ArrayList<>();
        ArrayList<gameComp> Comps = matchDetailsDAO.selectGameComps(r);
       for (gameComp gc : Comps ){
           double[] d = new double[(int)prop.get("nbTraits")];
           for(int i = 0; i < (int)prop.get("nbTraits") ; i ++){
               d[i]= gc.Traits.get(TRAITS_MAPS.get(i));
           }
           points.add(new DoublePoint(d));
       }
        List<Cluster<DoublePoint>> clusters =  clusterer.cluster(points);
        compositionDAO.insertCompositionFromClusters(clusters, false);
    }
}

