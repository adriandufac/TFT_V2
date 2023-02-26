package Scripts;

import BO.gameComp;
import DAL.compStatsDAO;
import DAL.compositionDAO;
import DAL.matchDetailsDAO;
import Utils.regionUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static DAL.compositionDAO.COMPO_MAP;

public class getCompStats extends apiRequester{
    private ArrayList<Map<String[], int[]>> CompsStats;
    private Map<List<String>,int[]> tmp;
    public getCompStats() throws IOException {
        super();
        CompsStats = new ArrayList<>();
        // { "nom" ,"region"} => {nbreGames,Total placement , Nbretop1, NbreTop4}
        tmp = new HashMap<>();
        System.out.println("yooooooo");
        for (int i=0; i<9; i++) {
            List<String> key = new ArrayList<String>();
            key.add(COMPO_MAP.get(i));
            key.add("NA");
            int[] value = new int[]{0,0,0,0};
            tmp.put(key,value);
            System.out.println(key.get(0) + " " + key.get(1));
            String sout = "";
            for (int j : tmp.get(key)) {
                sout += String.valueOf(j);
            }

            System.out.println(sout);
        }
    }


    public void getCompStats(regionUtils.region r) throws SQLException, IOException {
        compositionDAO compositionDAO = new compositionDAO();
        matchDetailsDAO matchDetailsDAO = new matchDetailsDAO();
        System.out.println("GETCOMPSTATS");
        ArrayList<String[]> CompsPattern = compositionDAO.selectCompositions();
        ArrayList<gameComp> CompsPlayed = matchDetailsDAO.selectGameComps(r);
        fillTabs(CompsPattern,CompsPlayed);
        List<String> key = new ArrayList<String>();
        key.add("recons Threat");
        key.add("NA");
        System.out.println("key " + tmp.get(key));

        for (Map.Entry<List<String>, int[]> entry : tmp.entrySet()) {
            System.out.println( entry.getKey().get(0) + " " + entry.getKey().get(1) + " : " + entry.getValue()[0] + "," +entry.getValue()[1]
                    + "," +entry.getValue()[2] + "," + entry.getValue()[3]);
        }
        compStatsDAO compStatsDAO = new compStatsDAO();
        compStatsDAO.insertCompStatsFromMap(tmp);
    }

    private void fillTabs( ArrayList<String[]> CompsPattern,ArrayList<gameComp> CompsPlayed ) throws IOException {
        for( gameComp c:  CompsPlayed) {
            if (checkCompositionMAtch(c, CompsPattern) != null) {
                List<String> key = new ArrayList<String>();
                key.add(checkCompositionMAtch(c, CompsPattern));
                key.add("NA");
                System.out.println(key.get(0) + " " + key.get(1));
                System.out.println(tmp.get(key));
                int value[] = Arrays.copyOf(tmp.get(key), tmp.get(key).length);
                value[0] += 1;
                value[1] += c.Classement;
                if (c.Classement == 1) {
                    value[2] += 1;
                }
                if (c.Classement < 5) {
                    value[3] += 1;
                }
                tmp.put(key, value);
                System.out.println(" PRingting : ");
                for (Map.Entry<List<String>, int[]> entry : tmp.entrySet()) {
                    String cleString = "";
                    String valueString = "";
                    for (String lacle : entry.getKey()) {
                        cleString += (lacle + " ");
                    }
                    for (int lavalue : entry.getValue()) {
                        cleString += (lavalue + " ");
                    }

                    System.out.println(cleString + ":" + valueString);
                }
            }
        }
    }

    private String checkCompositionMAtch (gameComp c, ArrayList<String[]> CompsPattern) throws IOException {

        Properties prop = new Properties();
        InputStream input = apiRequester.class.getResourceAsStream("/traits.properties");
        System.out.println("input null ?: " + input);
        prop.load(input);
        System.out.println("**************  Comp   ***************");
        int z = 0;
        for(String[] pattern : CompsPattern) {
            //System.out.println("Checking pattern : ");
            System.out.println("************" + z);
            z++;
            String compString =    "Comp    : ";
            String patternString = "Pattern : ";
            for (int i=1;i< pattern.length;i++) {
                compString+= c.Traits.get(prop.getProperty("trait"+(i-1))) + " ";
                patternString +=  pattern[i] + " ";
                System.out.println(" i = " + i);
                System.out.println(prop.getProperty("trait"+(i-1)));
                System.out.println(Integer.parseInt(pattern[i]));
                if (!(Integer.parseInt(pattern[i]) == c.Traits.get(prop.getProperty("trait"+(i-1))))) {
                    System.out.println(compString);
                    System.out.println(patternString);
                    break;

                }
                if (i == pattern.length-1) {
                    System.out.println(compString);
                    System.out.println(patternString);
                    System.out.println(" Composition detected : " + pattern [0]);
                    Pattern regex = Pattern.compile(".*[0-9]");
                    Matcher m = regex.matcher(pattern[0]);
                    // removing number to have just main categorie of composition
                    System.out.println(" AVANT LE CUT  : " + pattern[0]);
                    System.out.println(pattern[0].substring(pattern[0].length() - 1));
                    while (pattern[0].substring(pattern[0].length() - 1).matches("[0-9]")) {
                        System.out.println("ON CUT : ");
                        pattern[0] = pattern[0].substring(0, pattern[0].length() - 1);
                    }
                    System.out.println(" APRES LE CUT  : " + pattern[0]);
                    return pattern[0];
                }
            }
        }
        return null;
    }
}
