package Scripts;

import BO.gameComp;
import DAL.compositionDAO;
import DAL.matchDetailsDAO;
import Utils.regionUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static DAL.compositionDAO.COMPO_MAP;

public class getCompStats extends apiRequester{
    private ArrayList<Map<String[], int[]>> CompsStats;
    private Map<String[],int[]> tmp;
    public getCompStats() throws IOException {
        super();
        CompsStats = new ArrayList<>();
        // { "nom" ,"region"} => {nbreGames,Total placement , Nbretop1, NbreTop4}
        tmp = new HashMap<>();
        System.out.println("yooooooo");
        for (int i=0; i<9; i++) {
            System.out.println(COMPO_MAP.get(i));
            String[] key = new String[]{COMPO_MAP.get(i),"NA"};
            int[] value = new int[]{0,0,0,0};
            tmp.put(key,value);
            System.out.println(tmp.get(key));
        }
    }


    public void getCompStats(regionUtils.region r) throws SQLException, IOException {
        compositionDAO compositionDAO = new compositionDAO();
        matchDetailsDAO matchDetailsDAO = new matchDetailsDAO();
        ArrayList<String[]> CompsPattern = compositionDAO.selectCompositions();
        ArrayList<gameComp> CompsPlayed = matchDetailsDAO.selectGameComps(r);
        fillTabs(CompsPattern,CompsPlayed);
        String[] key = new String[]{"recons Threat","NA"};
        System.out.println(tmp.get(key));
        int[] reconsThreat = tmp.get(key);
        System.out.println(reconsThreat.length);
        for (int i=0;i<reconsThreat.length;i++) {
            System.out.println(reconsThreat[i]);
        }
    }

    private void fillTabs( ArrayList<String[]> CompsPattern,ArrayList<gameComp> CompsPlayed ) throws IOException {
        for( gameComp c:  CompsPlayed) {
            if (checkCompositionMAtch(c,CompsPattern) != null){
                    String key[] =new String[]{checkCompositionMAtch(c, CompsPattern), "NA"};
                    int value[] = tmp.get(key);
                    value[0] += 1;
                    value[1] += c.Classement;
                    if (c.Classement == 1){
                        value[2] +=1;
                    }
                    if (c.Classement < 5){
                        value[3] +=1;
                    }
                   tmp.put(key,value);
            }
        }
    }

    private String checkCompositionMAtch (gameComp c, ArrayList<String[]> CompsPattern) throws IOException {

        Properties prop = new Properties();
        InputStream input = apiRequester.class.getResourceAsStream("/traits.properties");
        System.out.println("input null ?: " + input);
        prop.load(input);
        for(String[] pattern : CompsPattern) {
            for (int i=1;i< pattern.length;i++) {
                if (!(Integer.parseInt(pattern[i]) == c.Traits.get(prop.get("trait"+(i-1))))) {
                    break;
                }
                if (i == pattern.length-1) {
                    return pattern[0];
                }
            }
        }
        return null;
    }
}
