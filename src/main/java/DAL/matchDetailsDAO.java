package DAL;

import ApiObjects.matchFromApi;
import ApiObjects.traitFromApi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class matchDetailsDAO {
    private static final String insertMatchDetailsToDetailsGame = "insert into DetailsGame (MatchID,PUUID,Classement,Augment1,Augment2,Augment3,"+
                                                                  "Ace,Admin,Aegis,AnimaSquad,Arsenal,Brawler,Civilian,Corrupted,Defender,Duelist,Forecaster,Gadgeteen,Hacker,Heart,LaserCorps,"+
                                                                  "Mascot,MechPrime,OxForce,Prankster,Recon,Renegade,SpellSlinger,StarGuardian,Supers,Sureshot,Threat,Underground)" +
            "                                                      values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String insertMatchDetailsToChampGame = "insert into ChampGame(MatchID,PUUID,NomAPI,tier,Item1,Item2,Item3)" +
                                                                "values(?,?,?,?,?,?,?)";
    private HashMap<String,Integer> TabTraits = new LinkedHashMap<>();
    private static HashMap<String,String> traitsFromAPIToDatabase = new HashMap<>();
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

    private void generateTabTrait(traitFromApi[] traits) {
        for(int k=0; k<traits.length; k++) {
            // must insert null if not 3 augments taken
            TabTraits.replace(traitsFromAPIToDatabase.get(traits[k].name),traits[k].num_units);
        }
    }
    private void clearTabTrait() {
        for (String key : TabTraits.keySet()) {
            TabTraits.put(key,0);
        }
    }
    public matchDetailsDAO() {
        TabTraits.put("Ace",0);
        TabTraits.put("Admin",0);
        TabTraits.put("Aegis",0);
        TabTraits.put("AnimaSquad",0);
        TabTraits.put("Arsenal",0);
        TabTraits.put("Brawler",0);
        TabTraits.put("Civilian",0);
        TabTraits.put("Corrupted",0);
        TabTraits.put("Defender",0);
        TabTraits.put("Duelist",0);
        TabTraits.put("Forecaster",0);
        TabTraits.put("Gadgeteen",0);
        TabTraits.put("Hacker",0);
        TabTraits.put("Heart",0);
        TabTraits.put("LaserCorps",0);
        TabTraits.put("Mascot",0);
        TabTraits.put("MechPrime",0);
        TabTraits.put("OxForce",0);
        TabTraits.put("Prankster",0);
        TabTraits.put("Recon",0);
        TabTraits.put("Renegade",0);
        TabTraits.put("SpellSlinger",0);
        TabTraits.put("StarGuardian",0);
        TabTraits.put("Supers",0);
        TabTraits.put("Sureshot",0);
        TabTraits.put("Threat",0);
        TabTraits.put("Underground",0);
    }
    static {
        traitsFromAPIToDatabase.put("Set8_Ace","Ace");
        traitsFromAPIToDatabase.put("Set8_Admin","Admin");
        traitsFromAPIToDatabase.put("Set8_Aegis","Aegis");
        traitsFromAPIToDatabase.put("Set8_AnimaSquad","AnimaSquad");
        traitsFromAPIToDatabase.put("Set8_Arsenal","Arsenal");
        traitsFromAPIToDatabase.put("Set8_Brawler","Brawler");
        traitsFromAPIToDatabase.put("Set8_Civilian","Civilian");
        traitsFromAPIToDatabase.put("Set8_Corrupted","Corrupted");
        traitsFromAPIToDatabase.put("Set8_Defender","Defender");
        traitsFromAPIToDatabase.put("Set8_Duelist","Duelist");
        traitsFromAPIToDatabase.put("Set8_Forecaster","Forecaster");
        traitsFromAPIToDatabase.put("Set8_GenAE","Gadgeteen");
        traitsFromAPIToDatabase.put("Set8_Hacker","Hacker");
        traitsFromAPIToDatabase.put("Set8_Heart","Heart");
        traitsFromAPIToDatabase.put("Set8_SpaceCorps","LaserCorps");
        traitsFromAPIToDatabase.put("Set8_Mascot","Mascot");
        traitsFromAPIToDatabase.put("Set8_ExoPrime","MechPrime");
        traitsFromAPIToDatabase.put("Set8_OxForce","OxForce");
        traitsFromAPIToDatabase.put("Set8_Prankster","Prankster");
        traitsFromAPIToDatabase.put("Set8_Recon","Recon");
        traitsFromAPIToDatabase.put("Set8_Renegade","Renegade");
        traitsFromAPIToDatabase.put("Set8_Channeler","SpellSlinger");
        traitsFromAPIToDatabase.put("Set8_StarGuardian","StarGuardian");
        traitsFromAPIToDatabase.put("Set8_Supers","Supers");
        traitsFromAPIToDatabase.put("Set8_Deadeye","Sureshot");
        traitsFromAPIToDatabase.put("Set8_Threat","Threat");
        traitsFromAPIToDatabase.put("Set8_UndergroundThe","Underground");
    }
}
