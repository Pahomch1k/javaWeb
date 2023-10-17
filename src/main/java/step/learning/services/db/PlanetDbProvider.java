package step.learning.services.db;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class PlanetDbProvider implements DbProvider{

    private Connection connection;
    @Override
    public Connection getConnection() {
        if (connection == null){
            JsonObject config ;
            try( Reader reader =  new InputStreamReader( Objects.requireNonNull( this.getClass().getClassLoader().getResourceAsStream("db_config.json") ) ) ) {
                config = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            catch (NullPointerException ex) {
                throw new RuntimeException("Resource not found");
            }
            JsonObject planetScale = config.get("DataProviders").getAsJsonObject().get("planetScale").getAsJsonObject();

            try {
                //Class.forName("com.mysql.cj.jdbc.Driver");
                DriverManager.registerDriver( new com.mysql.cj.jdbc.Driver() );
                this.connection = DriverManager.getConnection(
                        planetScale.get("url").getAsString(),
                        planetScale.get("user").getAsString(),
                        planetScale.get("password").getAsString());
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        }
        return this.connection;
    }
}
