package DAL;

import ApiObjects.matchFromApi;
import ApiObjects.traitFromApi;
import ApiObjects.traitFromJson;
import BO.gameComp;
import Scripts.riotApiRequester;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

public class matchDetailsDAO {
    protected static boolean jsonSerializeNulls = true;
    private static final String insertMatchDetailsToDetailsGame = "insert into DetailsGame (MatchID,PUUID,Classement,Augment1,Augment2,Augment3,"+
                                                                  "Ace,Admin,Aegis,AnimaSquad,Arsenal,Brawler,Civilian,Corrupted,Defender,Duelist,Forecaster,Gadgeteen,Hacker,Heart,LaserCorps,"+
                                                                  "Mascot,MechPrime,OxForce,Prankster,Recon,Renegade,SpellSlinger,StarGuardian,Supers,Sureshot,Threat,Underground)" +
            "                                                      values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String insertMatchDetailsToChampGame = "insert into ChampGame(MatchID,PUUID,NomAPI,tier,Item1,Item2,Item3)" +
                                                                "values(?,?,?,?,?,?,?)";

/*    private static final String selectGamesWithTraits = "SELECT MatchID,PUUID,Classement,Ace,Admin,Aegis,AnimaSquad,Arsenal,Brawler,Civilian,Corrupted," +
            "Defender,Duelist,Forecaster,Gadgeteen,Hacker,Heart,LaserCorps,Mascot,MechPrime,OxForce,Prankster,Recon,Renegade,SpellSlinger" +
            ",StarGuardian,Supers,Sureshot,Threat,Underground FROM DetailsGame";*/
    private static final String clearMatchsChampGame = "DELETE FROM ChampGame";
    private static final String clearDetailsGame= "DELETE FROM DetailsGame";

    private final HashMap<String,Integer> TabTraits = new LinkedHashMap<>();
    private final HashMap<String,Integer> TabTraitsV2 = new LinkedHashMap<>();
    private static final HashMap<String,String> traitsFromAPIToDatabase = new HashMap<>();
    public void insert(matchFromApi match)throws SQLException {
        insertDetailGameV2(match);
        insertChampGame(match);
    }
    private void insertDetailGame(matchFromApi match) throws SQLException {
        Connection cnx = database.openCo();
        PreparedStatement rqt;
        try {
            rqt = cnx.prepareStatement(buildinsertMatchDetailsToDetailsGame());
            for(int i =0; i<match.info.participants.length ; i++) {
                rqt.setString(1,match.metadata.match_id);

                rqt.setString(2,match.info.participants[i].puuid);

                rqt.setInt(3,match.info.participants[i].placement);
                for(int j =0; j<3 ; j++) {
                    // must insert null if not 3 augments taken
                    if (match.info.participants[i].augments.length >= j) {
                        rqt.setString(4 + j, match.info.participants[i].augments[j]);
                    }
                    else {
                        rqt.setString(4 + j, null);
                    }
                }
                generateTabTrait(match.info.participants[i].traits);
                int x = 7;
                for (String key : TabTraits.keySet()) {
                    rqt.setInt(x,TabTraits.get(key));
                    /*System.out.println("TRAIT N" + (x-6) + "   :");
                    System.out.println(TabTraits.get(key));*/
                    x++;
                }
                rqt.executeUpdate();
                clearTabTrait();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            cnx.close();
        }
    }

    private void insertDetailGameV2(matchFromApi match) throws SQLException {
        Connection cnx = database.openCo();
        PreparedStatement rqt;
        try {
            rqt = cnx.prepareStatement(buildinsertMatchDetailsToDetailsGameV2());
            for(int i =0; i<match.info.participants.length ; i++) {
                rqt.setString(1,match.metadata.match_id);

                rqt.setString(2,match.info.participants[i].puuid);

                rqt.setInt(3,match.info.participants[i].placement);
                for(int j =0; j<3 ; j++) {
                    // must insert null if not 3 augments taken
                    if (match.info.participants[i].augments.length > j) {
                        rqt.setString(4 + j, match.info.participants[i].augments[j]);
                    }
                    else {
                        rqt.setString(4 + j, null);
                    }
                }
                generateTabTrait(match.info.participants[i].traits);
                int x = 7;
                for (String key : TabTraitsV2.keySet()) {
                    rqt.setInt(x,TabTraitsV2.get(key));
                    x++;
                }
                System.out.println("requete : ");
                System.out.println(rqt);
                rqt.executeUpdate();
                clearTabTrait();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            cnx.close();
        }
    }

    private void insertChampGame(matchFromApi match) throws SQLException {
        Connection cnx = database.openCo();
        PreparedStatement rqt2;
        try {
            rqt2 = cnx.prepareStatement(insertMatchDetailsToChampGame);
            for(int i =0; i<match.info.participants.length ; i++) {
                for(int j =0; j<match.info.participants[i].units.length;j++) {
                    rqt2.setString(1,match.metadata.match_id);
                    rqt2.setString(2,match.info.participants[i].puuid);
                    rqt2.setString(3,match.info.participants[i].units[j].character_id);
                    rqt2.setInt(4,match.info.participants[i].units[j].tier);
                    for(int k =0; k<3 ; k++) {
                        if (match.info.participants[i].units[j].itemNames.length-1  >= k) {
                            rqt2.setString(5+k,match.info.participants[i].units[j].itemNames[k]);
                        }
                       else {
                            rqt2.setString(5+k,null);
                        }
                    }
                    rqt2.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            cnx.close();
        }
    }

    public ArrayList<gameComp> selectGameComps (Utils.regionUtils.region r) throws SQLException, IOException {
        Connection cnx = null;
        PreparedStatement rqt;
        Properties prop = new Properties();
        InputStream input = riotApiRequester.class.getResourceAsStream("/traits.properties");
        prop.load(input);
        ArrayList<gameComp> Comps= new ArrayList<>();
        ResultSet rs;
        try {
            cnx = database.openCo();
            rqt = cnx.prepareStatement(buildSelectString());
            rs = rqt.executeQuery();
            while (rs.next()) {
                gameComp g = new gameComp(rs.getString("PUUID"),rs.getString("MatchID"),rs.getInt("Classement"));
                for (int i=0;i<Integer.parseInt(prop.getProperty("nbTraits"));i++) {

                    g.addToTraits(prop.getProperty("trait"+i),rs.getInt(prop.getProperty("trait"+i)));
                }
                /*for (String key : g.Traits.keySet()) {
                    System.out.println(key + " : " + g.Traits.get(key));
                }*/
                g.changeTraitValueWithTreeshhold();
                Comps.add(g);
            }
        } catch (Exception e) {
            System.out.println("error in selectgamecomps  : " + e.getMessage());
        }
        finally {
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
            }
        }
        return Comps;
    }
    public gameComp selectGameCompFromMAtchID (Utils.regionUtils.region r, String matchID) throws SQLException, IOException {
        Connection cnx = null;
        PreparedStatement rqt;
        Properties prop = new Properties();
        InputStream input = riotApiRequester.class.getResourceAsStream("/traits.properties");
        prop.load(input);
        ResultSet rs;
        gameComp g = null;
        try {
            cnx = database.openCo();
            rqt = cnx.prepareStatement(buildSelectString(matchID));
            rs = rqt.executeQuery();
            while (rs.next()) {
                g = new gameComp(rs.getString("PUUID"),rs.getString("MatchID"),rs.getInt("Classement"));
                for (int i=0;i<Integer.parseInt(prop.getProperty("nbTraits"));i++) {

                    g.addToTraits(prop.getProperty("trait"+i),rs.getInt(prop.getProperty("trait"+i)));
                }
                /*for (String key : g.Traits.keySet()) {
                    System.out.println(key + " : " + g.Traits.get(key));
                }*/
            }
        } catch (Exception e) {
            System.out.println("error in selectgamecomps  : " + e.getMessage());
        }
        finally {
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
            }
        }
        g.changeTraitValueWithTreeshhold();
        return g;
    }
    private String buildSelectString() throws IOException {
        Properties prop = new Properties();
        InputStream input = riotApiRequester.class.getResourceAsStream("/traits.properties");
        prop.load(input);
        String select = "SELECT MatchID,PUUID,Classement,";
        int taille = Integer.parseInt(prop.getProperty("nbTraits"));
        for (int i=0;i<taille;i++) {
            select += prop.getProperty("trait" + i);
            if (i != taille-1) {
                select+=",";
            }
        }
        select += " FROM DetailsGame";
        return select;
    }
    private String buildSelectString( String matchID) throws IOException {
        Properties prop = new Properties();
        InputStream input = riotApiRequester.class.getResourceAsStream("/traits.properties");
        prop.load(input);
        String select = "SELECT MatchID,PUUID,Classement,";
        int taille = Integer.parseInt(prop.getProperty("nbTraits"));
        for (int i=0;i<taille;i++) {
            select += prop.getProperty("trait" + i);
            if (i != taille-1) {
                select+=",";
            }
        }
        select += " FROM DetailsGame where MatchID = ";
        select += matchID;
        System.out.println(select);
        return select;
    }
    private String buildinsertMatchDetailsToDetailsGame() throws IOException {
        Properties prop = new Properties();
        InputStream input = riotApiRequester.class.getResourceAsStream("/traits.properties");
        prop.load(input);
        String select = "insert into DetailsGame (MatchID,PUUID,Classement,Augment1,Augment2,Augment3,";
        int taille = Integer.parseInt(prop.getProperty("nbTraits"));
        for (int i=0;i<taille;i++) {
            select += prop.getProperty("trait" + i);
            if (i != taille-1) {
                select+=",";
            }
        }
        select += ") values(?,?,?,?,?,?,";
        for (int i=0;i<taille;i++) {
            select += "?";
            if (i != taille-1) {
                select+=",";
            }
        }
        select += ")";
        System.out.println(select);
        return select;
    }
    private String buildinsertMatchDetailsToDetailsGameV2() throws IOException {
        String insert = "insert into DetailsGame (MatchID,PUUID,Classement,Augment1,Augment2,Augment3,";

        Gson gson = Utils.jsonUtils.gson(jsonSerializeNulls);
        InputStream is = setUpTables.class.getClassLoader().getResourceAsStream("DataDragon/tft-trait.json");
        String json = IOUtils.toString(is, StandardCharsets.UTF_8);
        traitFromJson traits = gson.fromJson(json, traitFromJson.class);
        for (String key : traits.data.keySet()) {
            System.out.println(traits.data.get(key).id);
            insert +=  traits.data.get(key).id;
            if (key != traits.data.lastKey()) {
                insert += ", ";
            } else {
                insert += ")";
            }
        }
        insert += "values (?,?,?,?,?,?,";
        for (String key : traits.data.keySet()) {
            System.out.println(traits.data.get(key).id);
            insert +=  "?";
            if (key != traits.data.lastKey()) {
                insert += ",";
            } else {
                insert += ")";
            }
        }
        System.out.println(insert);
        return insert;
    }
    public void clearMatchsDetails() throws SQLException {
        Connection cnx = database.openCo();
        Statement rqt = cnx.createStatement();
        Statement rqt2 = cnx.createStatement();
        rqt.executeUpdate(clearDetailsGame);
        rqt2.executeUpdate(clearMatchsChampGame);
        cnx.close();
    }
    private void generateTabTrait(traitFromApi[] traits) {
        System.out.println("generateTabTrait");
        for(int k=0; k<traits.length; k++) {

            TabTraits.replace(traitsFromAPIToDatabase.get(traits[k].name),traits[k].num_units);
            System.out.println(traits[k].name);
            System.out.println(traitsFromAPIToDatabase.get(traits[k].name));
            System.out.println(traits[k].num_units);
            TabTraitsV2.replace(traits[k].name,traits[k].num_units);
        }
    }
    private void clearTabTrait() {
        TabTraits.replaceAll((key,old) -> 0);
        TabTraitsV2.replaceAll((key,old) -> 0);
    }
    public matchDetailsDAO() throws IOException {
        Properties prop = new Properties();
        InputStream input = riotApiRequester.class.getResourceAsStream("/traits.properties");
        prop.load(input);
        for (int i=0;i<Integer.parseInt(prop.getProperty("nbTraits"));i++) {
            TabTraits.put(prop.getProperty("trait"+i),0);
        }
        Gson gson = Utils.jsonUtils.gson(jsonSerializeNulls);
        InputStream is = setUpTables.class.getClassLoader().getResourceAsStream("DataDragon/tft-trait.json");
        String json = IOUtils.toString(is, StandardCharsets.UTF_8);
        traitFromJson traits = gson.fromJson(json, traitFromJson.class);
        for (String key : traits.data.keySet()) {
            TabTraitsV2.put(traits.data.get(key).id,0);
        }

    }
    static {
        Properties prop = new Properties();

        try {
            InputStream input = riotApiRequester.class.getResourceAsStream("/traits.properties");
            System.out.println("input matchdetails: " + input);
            prop.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i<Integer.parseInt(prop.getProperty("nbTraits")); i++) {
            traitsFromAPIToDatabase.put(prop.getProperty("traitfromapi"+i),prop.getProperty("trait"+i));
           /* System.out.println("*******************");
            System.out.println("*******************");
            System.out.println("*******************");
            System.out.println(prop.getProperty("trait"+i));
            System.out.println(prop.getProperty("traitfromapi"+i));
            System.out.println("*******************");
            System.out.println("*******************");
            System.out.println("*******************");*/
        }
    }
}
