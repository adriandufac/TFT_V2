package DAL;
import java.sql.*;


public class database{
   
   static final String DB_URL = "jdbc:sqlserver://db5010961742.hosting-data.io:3306;databasename=dbs9266679;";
   static final String USER = "dbu5506528";
   static final String PASS = "Ulkaline35!";
   static {
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
   }
   public static Connection openCo() throws SQLException{

         return DriverManager.getConnection(DB_URL, USER, PASS); 
    
   }
    
}
