package DAL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import BO.leagueClass;

public class leagueDAO {

    private static final String insertLeague = "insert into Joueurs (PUUID,Région) values (?,?)";

    public void  insert(leagueClass league ){
        Connection cnx = null;
		PreparedStatement rqt = null;
		ResultSet rs = null;
		try {
			cnx = database.openCo();
			rqt = cnx.prepareStatement(insertLeague);
			rqt.setString(1,league.entries.get(0).PUUID);
			rqt.setString(2, league.entries.get(0).région);

			rs = rqt.executeQuery();

		} catch (Exception e) {
			System.out.println(e.getMessage());

    	}
	}
    
}
