package DAL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import BO.leagueClass;
import BO.player;

public class leagueDAO {

    private static final String insertLeague = "insert into Joueurs (PUUID,Region) values (?,?)";
	private static final String selectPUUIDFromRegion = "select PUUID from Joueurs WHERE Region = ?";

    public void  insert(leagueClass league ){
        Connection cnx = null;
		PreparedStatement rqt = null;
		//ResultSet rs = null;
		try {
			cnx = database.openCo();
			rqt = cnx.prepareStatement(insertLeague);
			//TODO Boucler sur ttes les entrées
			for (BO.player player : league.entries){
				rqt.setString(1,player.PUUID);
				rqt.setString(2, player.region);
				rqt.executeUpdate();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
    	}
	}

	public List<String> selectPUUIDSFromRegion (Utils.regionUtils.region r){
		Connection cnx = null;
		PreparedStatement rqt = null;
		List<String> PUUIDS= null;
		ResultSet rs = null;
		try {
			cnx = database.openCo();
			rqt = cnx.prepareStatement(selectPUUIDFromRegion);
			rqt.setString(1, r.toString());
			rs = rqt.executeQuery();
			while (rs.next()){
				System.out.println(rs.getString(1));
				PUUIDS.add(rs.getString("PUUID"));
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return PUUIDS;
	}
    
}
