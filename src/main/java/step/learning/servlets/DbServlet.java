package step.learning.servlets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.services.db.DbProvider;
import step.learning.services.validation.DataValidation;
import step.learning.services.validation.ValidationResult;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Statement;

@Singleton
public class DbServlet extends HttpServlet {
    private final DbProvider dbProvider;
    private final String dbPrefix;

    @Inject
    public DbServlet(DbProvider dbProvider, @Named("db-prefix") String dbPrefix) {
        this.dbProvider = dbProvider;
        this.dbPrefix = dbPrefix;
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String status;
        String message;
        String sql = "CREATE TABLE " + dbPrefix + "call_me (" +
                "id   BIGINT PRIMARY KEY," +
                "name VARCHAR(64) NULL," +
                "phone CHAR(13) NOT NULL COMMENT '+380 95 862 27 08'," +
                "moment DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE =  InnoDB DEFAULT CHARSET = UTF8";
        try( Statement statement = dbProvider.getConnection().createStatement() ){
            statement.executeUpdate( sql );
            status = "OK";
            message = "Table created";
        }
        catch (SQLException ex){
            status = "error";
            message = ex.getMessage();
        }
        JsonObject result = new JsonObject();
        result.addProperty("status", status);
        result.addProperty("message", message);
        resp.getWriter().print( result.toString() );
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String connectionStatus;
        try {
            dbProvider.getConnection();
            connectionStatus = "Connection OK";
        }
        catch (RuntimeException ex) {
            connectionStatus = "Connection error " + ex.getMessage();
        }
        req.setAttribute("connectionStatus", connectionStatus);

        req.setAttribute("page-body", "db.jsp");
        req.getRequestDispatcher("WEB-INF/_layout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        String json;
        JsonObject result = new JsonObject();
        try(InputStream body = req.getInputStream()) {
            while ((len = body.read(buffer)) > 0) {
                bytes.write(buffer, 0, len);
            }
            json = bytes.toString(StandardCharsets.UTF_8.name());
            JsonObject data = JsonParser.parseString(json).getAsJsonObject();

            // Validate data using our DataValidation class
            ValidationResult validationResult = DataValidation.validate(data);
            if (!validationResult.isValid()) {
                result.addProperty("status", "validation error");
                result.addProperty("message", validationResult.getMessage());
                resp.getWriter().print(result.toString());
                return;
            }
            // If there's an error, set the result accordingly
            if (!validationResult.isValid()) {
                result.addProperty("status", "validation error");
                result.addProperty("message", validationResult.getMessage());
            } else {
                result.addProperty("name", data.get("name").getAsString());
                result.addProperty("phone", data.get("phone").getAsString());
            }
        }
        catch (IOException ex) {
            json = ex.getMessage();
            result.addProperty("status", "error");
            result.addProperty("message", ex.getMessage());
        }
        resp.setContentType("application/json");
        resp.getWriter().print(result.toString());
    }
}
