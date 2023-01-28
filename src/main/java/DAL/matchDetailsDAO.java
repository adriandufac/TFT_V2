package DAL;

import ApiObjects.matchFromApi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class matchDetailsDAO {
    private static final String insertMatchDetailsToDetailsGame = "insert into DetailsGame (MatchID,PUUID,Classement,Augment1,Augment2,Augment3)" +
            "                                                      values (?,?,?,?,?,?)";
    private static final String insertMatchDetailsToChampGame = "insert into ChampGame(MatchID,PUUID,NomAPI,tier,Item1,Item2,Item3)" +
            "                                                     values(?,?,?,?,?,?,?)";
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
                }
                rqt.executeUpdate();
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
}
