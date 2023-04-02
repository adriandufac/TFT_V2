package DAL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import BO.leagueClass;

public class leagueDAO {

    private static final String insertLeague = "insert into Joueurs (PUUID,Region) values (?,?)";
	private static final String selectPUUIDFromRegion = "SELECT PUUID from Joueurs WHERE Region = ?";
	private static final String clearTable = "DELETE FROM Joueurs";

    public void  insert(leagueClass league ) throws SQLException {
        Connection cnx= database.openCo();
		PreparedStatement rqt;
		try {
			rqt = cnx.prepareStatement(insertLeague);
			for (BO.player player : league.entries){
				if (player.PUUID != null && player.region != null) {
					System.out.println(player.getName());
					rqt.setString(1,player.PUUID);
					rqt.setString(2, player.region);
					rqt.executeUpdate();
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
    	}
		finally {
			cnx.close();
		}
	}

	public List<String> selectPUUIDSFromRegion (Utils.regionUtils.region r) throws SQLException {
		Connection cnx = database.openCo();
		PreparedStatement rqt;
		List<String> PUUIDS= new ArrayList<>();
		ResultSet rs;
		try {
			rqt = cnx.prepareStatement(selectPUUIDFromRegion);
			rqt.setString(1, r.toString());
			System.out.println(rqt);
			rs = rqt.executeQuery();

			while (rs.next()){
				System.out.println("****************"+rs.getString(1)+"*********");
				System.out.println('\n');
				PUUIDS.add(rs.getString("PUUID"));
			}
			rs.close();
			rqt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		finally {
			cnx.close();
		}
		return PUUIDS;
	}

	public void clearJoueurs() throws SQLException {
		Connection cnx = database.openCo();
		Statement rqt = cnx.createStatement();
		rqt.executeUpdate(clearTable);
		cnx.close();
	}
    
}
