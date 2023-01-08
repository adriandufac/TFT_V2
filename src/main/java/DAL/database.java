package DAL;
import java.sql.*;


public class database{
   
   static final String DB_URL = "jdbc:mysql://localhost:3306/TFT";
   static final String USER = "root";
   static final String PASS = "Ulkaline35!";
   static {
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
   }
   public static Connection openCo() throws SQLException{

         return DriverManager.getConnection(DB_URL, USER, PASS); 
    
   }
    
}
