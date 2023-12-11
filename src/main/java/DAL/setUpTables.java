package DAL;


import ApiObjects.championFromJson;
import ApiObjects.matchFromApi;
import ApiObjects.traitFromJson;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class setUpTables {
    protected static boolean jsonSerializeNulls = true;
    private static final String insertChampion = "insert into Champion (NomAPI, NomSITE,Cost) values (?,?,?)";
    public void  createDetailsGame() throws SQLException, IOException, URISyntaxException {
        Connection cnx = database.openCo();
        Statement stmt = cnx.createStatement();
        stmt.executeUpdate(buildDetailsGameRequest());
    }

    public static String buildDetailsGameRequest() throws IOException, URISyntaxException {

        String request = "CREATE TABLE DetailsGame (MatchID VARCHAR(256),PUUID VARCHAR(256),Classement INTEGER,Augment1 VARCHAR(256),Augment2 VARCHAR(256),Augment3 VARCHAR(256), ";

        Gson gson = Utils.jsonUtils.gson(jsonSerializeNulls);
        InputStream is = setUpTables.class.getClassLoader().getResourceAsStream("DataDragon/tft-trait.json");
        String json = IOUtils.toString(is, StandardCharsets.UTF_8);
        traitFromJson traits = gson.fromJson(json, traitFromJson.class);
        for (String key : traits.data.keySet()) {
            System.out.println(traits.data.get(key).id);
            request +=  traits.data.get(key).id + " VARCHAR(128)";
            if (key != traits.data.lastKey()) {
                request += ", ";
            } else {
                request += ")";
            }
        }
        System.out.println(request);
        return request;
    }

    public void fillChampion() throws SQLException, IOException {
        Connection cnx = database.openCo();
        Gson gson = Utils.jsonUtils.gson(jsonSerializeNulls);
        InputStream is = setUpTables.class.getClassLoader().getResourceAsStream("DataDragon/tft-champion.json");
        String json = IOUtils.toString(is, StandardCharsets.UTF_8);
        championFromJson champions = gson.fromJson(json, championFromJson.class);
        PreparedStatement rqt;
        for (String key : champions.data.keySet()) {
            rqt = cnx.prepareStatement(insertChampion);
            rqt.setString(1, champions.data.get(key).id);
            rqt.setString(2, champions.data.get(key).name);
            rqt.setInt(3, champions.data.get(key).tier);
            rqt.executeUpdate();
        }
        cnx.close();

    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        setUpTables.buildDetailsGameRequest();
    }
}

