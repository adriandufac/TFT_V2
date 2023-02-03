package BO;

import DAL.CompAnalyses;
import Utils.regionUtils;

import java.sql.SQLException;
import java.util.ArrayList;

public class clusterableClass {

    private ArrayList<gameComp> Comps = new ArrayList<>();

    public clusterableClass() throws SQLException {
        CompAnalyses compAnalyses = new CompAnalyses();
        this.Comps = compAnalyses.selectGameComps(regionUtils.region.NA);
    }


}