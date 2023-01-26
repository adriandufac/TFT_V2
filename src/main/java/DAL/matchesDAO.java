package DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class matchesDAO {

    private static final String insertMatch = "insert into MatchsIDCurrent (MatchID) values (?)";
    private static final String selectMatchs ="SELECT matchID from matchsIDCurrent WHERE Region = ?";
    public void  insert(ArrayList<String> matches ) throws SQLException {
        Connection cnx = database.openCo();
        PreparedStatement rqt;
        try {

            rqt = cnx.prepareStatement(insertMatch);
            for (String match : matches){
                rqt.setString(1,match);
                rqt.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            cnx.close();
        }
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
}
