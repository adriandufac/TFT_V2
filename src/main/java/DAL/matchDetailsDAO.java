package DAL;

import ApiObjects.matchFromApi;
import ApiObjects.traitFromApi;
import BO.gameComp;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

public class matchDetailsDAO {
    private static final String insertMatchDetailsToDetailsGame = "insert into DetailsGame (MatchID,PUUID,Classement,Augment1,Augment2,Augment3,"+
                                                                  "Ace,Admin,Aegis,AnimaSquad,Arsenal,Brawler,Civilian,Corrupted,Defender,Duelist,Forecaster,Gadgeteen,Hacker,Heart,LaserCorps,"+
                                                                  "Mascot,MechPrime,OxForce,Prankster,Recon,Renegade,SpellSlinger,StarGuardian,Supers,Sureshot,Threat,Underground)" +
            "                                                      values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String insertMatchDetailsToChampGame = "insert into ChampGame(MatchID,PUUID,NomAPI,tier,Item1,Item2,Item3)" +
                                                                "values(?,?,?,?,?,?,?)";

    private static final String selectGamesWithTraits = "SELECT MatchID,PUUID,Classement,Ace,Admin,Aegis,AnimaSquad,Arsenal,Brawler,Civilian,Corrupted," +
            "Defender,Duelist,Forecaster,Gadgeteen,Hacker,Heart,LaserCorps,Mascot,MechPrime,OxForce,Prankster,Recon,Renegade,SpellSlinger" +
            ",StarGuardian,Supers,Sureshot,Threat,Underground FROM DetailsGame";
    private final HashMap<String,Integer> TabTraits = new LinkedHashMap<>();
    private static final HashMap<String,String> traitsFromAPIToDatabase = new HashMap<>();
    public void insert(matchFromApi match)throws SQLException {
        insertDetailGame(match);
        insertChampGame(match);
    }
    private void insertDetailGame(matchFromApi match) throws SQLException {
        Connection cnx = database.openCo();
        PreparedStatement rqt;
        try {
            rqt = cnx.prepareStatement(insertMatchDetailsToDetailsGame);
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
                    generateTabTrait(match.info.participants[i].traits);
                    int x = 7;
                    for (String key : TabTraits.keySet()) {
                        rqt.setInt(x,TabTraits.get(key));
                        x++;
                    }
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
        prop.load(new FileInputStream("traits.properties"));
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
                for (int i=0;i<(int)prop.get("nbTraits");i++) {
                    g.addToTraits((String)prop.get("trait"+i),rs.getInt((String)prop.get("trait"+i)));
                }
                Comps.add(g);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
            }
        }
        return Comps;
    }
    private void generateTabTrait(traitFromApi[] traits) {
        for(int k=0; k<traits.length; k++) {
            // must insert null if not 3 augments taken
            TabTraits.replace(traitsFromAPIToDatabase.get(traits[k].name),traits[k].num_units);
        }
    }
    private void clearTabTrait() {
        TabTraits.replaceAll((key,old) -> 0);
    }
    public matchDetailsDAO() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("traits.properties"));
        for (int i=0;i<(int)prop.get("nbTraits");i++) {
            TabTraits.put((String)prop.get("trait"+i),0);
        }
    }
    static {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("traits.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i=0;i<(int)prop.get("nbTraits");i++) {
            traitsFromAPIToDatabase.put((String)prop.get("trait"+i),(String)prop.get("traitfromapi"+i));
        }
    }
}
