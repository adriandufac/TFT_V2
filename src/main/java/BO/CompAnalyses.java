package BO;

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


public class CompAnalyses {

    public void cluster (regionUtils.region r) throws SQLException, IOException {
        matchDetailsDAO matchDetailsDAO = new matchDetailsDAO();
        compositionDAO compositionDAO = new compositionDAO();
        DBSCANClusterer clusterer = new DBSCANClusterer(1.4,30);
        List<DoublePoint> points = new ArrayList<>();
        ArrayList<gameComp> Comps = matchDetailsDAO.selectGameComps(r);
       for (gameComp gc : Comps ){
           double[] d = new double[27];
           for(int i = 0; i < 27 ; i ++){
               d[i]= gc.Traits.get(TRAITS_MAPS.get(i));
           }
           points.add(new DoublePoint(d));
       }
        List<Cluster<DoublePoint>> clusters =  clusterer.cluster(points);
        compositionDAO.insertCompositionFromClusters(clusters);
    }
    static final private  Map<Integer, String> TRAITS_MAPS = new HashMap<>();


    static {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("traits.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i=0;i<(int)prop.get("nbTraits");i++) {
            TRAITS_MAPS.put(i,(String)prop.get("trait"+i));
        }
    }
}

