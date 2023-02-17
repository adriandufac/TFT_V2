package DAL;
import Scripts.apiRequester;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;


public class database{
   private static final Properties prop = new Properties();
   static {
       try {
           InputStream input = apiRequester.class.getResourceAsStream("/DB.properties");
           System.out.println("input: " + input);
           prop.load(input);
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
		try {
			Class.forName(prop.getProperty("DRIVER"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
   }
   protected static Connection openCo() throws SQLException {
       return DriverManager.getConnection(prop.getProperty("URL"),prop.getProperty("USER") ,prop.getProperty("PASS"));
   }
    
}
