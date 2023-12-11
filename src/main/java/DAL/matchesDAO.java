package DAL;

import BO.match;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class matchesDAO {

    private static final String insertMatch = "insert into MatchsIDCurrent (MatchID, Region) values (?,?)";
    private static final String selectMatchs ="SELECT matchID from MatchsIDCurrent WHERE Region = ?";
    private static final String clearTable = "DELETE FROM MatchsIDCurrent";
    public void  insert(ArrayList<match> matches ) throws SQLException {
        Connection cnx = database.openCo();
        PreparedStatement rqt;
        rqt = cnx.prepareStatement(insertMatch);
        for (match match : matches){
            try {
                rqt.setString(1, match.matchID);
                rqt.setString(2, match.r.toString());
                System.out.println("Inserting " + match.matchID);
                rqt.executeUpdate();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        cnx.close();
    }
    public List<String> selectmatchsIDSFromRegion (Utils.regionUtils.region r){
        Connection cnx;
        PreparedStatement rqt;
        List<String> matches= new ArrayList<>();
        ResultSet rs;
        try {
            cnx = database.openCo();
            rqt = cnx.prepareStatement(selectMatchs);
            rqt.setString(1, r.toString());
            System.out.println(rqt);
            rs = rqt.executeQuery();

            while (rs.next()){
                System.out.println("****************"+rs.getString(1)+"*********");
                System.out.println('\n');
                matches.add(rs.getString("matchID"));
            }
            rs.close();
            rqt.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return matches;
    }
    public void clearMatchs() throws SQLException {
        Connection cnx = database.openCo();
        Statement rqt = cnx.createStatement();
        rqt.executeUpdate(clearTable);
        cnx.close();
    }
}
