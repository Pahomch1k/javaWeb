package step.learning.servlets;
import step.learning.dto.entities.CallMe;


import com.google.gson.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dao.CallMeDao;
import step.learning.services.db.DbProvider;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Pattern;

@Singleton
public class DbServlet extends HttpServlet {
    private final DbProvider dbProvider;
    private final String dbPrefix;
    //private int x = 0;
    private final CallMeDao callMeDao;

    @Inject
    public DbServlet(DbProvider dbProvider, @Named("db-prefix") String dbPrefix, CallMeDao callMeDao) {
        this.dbProvider = dbProvider;
        this.dbPrefix = dbPrefix;
        this.callMeDao = callMeDao;

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch ( req.getMethod().toUpperCase() ){
            case "PATCH": doPatch( req, resp ); break;
            case "COPY": doCopy( req, resp );  break;
            default: super.service( req, resp );
        }
    }

    protected void doCopy(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<CallMe> calls = callMeDao.getAll();
        Gson gson = new GsonBuilder().create();
        resp.getWriter().print( gson.toJson( calls ) );

    }
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType( "application/json" );

        String callId = req.getParameter("call-id");
        if (callId == null) {
            resp.setStatus( 400 );
            resp.getWriter().print("\"Missing required parameter 'call-id'\" ");
            return;
        }
        CallMe item = callMeDao.getById(callId);
        if (item == null) {
            resp.setStatus( 404 );
            resp.getWriter().print("\"Item not found for given parameter 'call-id'\" ");
            return;
        }
        if ( item.getCallMoment() != null ) {
            resp.setStatus( 422 );
            resp.getWriter().print("\"Unprocessable Content: Item was processed early\" ");
            return;
        }
        if (callMeDao.updateCallMoment( item )) {
            resp.setStatus(202);
            resp.getWriter().print( new Gson().toJson( item ));
        }
        else {
            resp.setStatus( 500 );
            resp.getWriter().print("\"Server error\" ");
            return;
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String status;
        String message;
        String sql = "CREATE TABLE " + dbPrefix + "call_me (" +
                "`id`   BIGINT PRIMARY KEY," +
                "`name` VARCHAR(64) NULL," +
                "`phone` CHAR(13) NOT NULL COMMENT '+380 95 862 27 08'," +
                "`moment` DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "`call_moment` DATETIME NULL, " +
                "`delete_moment` DATETIME NULL" +
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
        resp.setContentType("application/json");
        String contentType = req.getContentType();
        if (contentType == null || ! contentType.startsWith("application/json") ) {
            resp.setStatus( 415 );
            resp.getWriter().print("\"Uns Media Type: 'application/json' only\" ");
            return;
        }

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
        }
        catch (IOException ex) {
            System.err.println( ex.getMessage());
            resp.setStatus( 500 );
            resp.getWriter().print("\"Server error/ Details on servers logs\" ");
            return;
        }
        JsonObject data;
        try {
            data = JsonParser.parseString(json).getAsJsonObject();
        }
        catch (JsonSyntaxException | IllegalStateException ex) {
            resp.setStatus( 400 );
            resp.getWriter().print("\"Invalid Json. Object required\" ");
            return;
        }
        String name, phone;
        try {
            name = data.get("name").getAsString();
            phone = data.get("phone").getAsString();
        }
        catch ( Exception ignored ) {
            resp.setStatus( 400 );
            resp.getWriter().print("\"Invalid Json data. Orequrid 'name' and 'phone' fields\" ");
            return;
        }
        if (!Pattern.matches("^\\+38\\s?(\\(\\d{3}\\)|\\d{3})\\s?\\d{3}(-|\\s)?\\d{2}(-|\\s)?\\d{2}$", phone)){
            resp.setStatus( 400 );
            resp.getWriter().print("\"Invalid 'phone' fields. required '+\\d{12}' format\" ");
            return;
        }
        phone = phone.replaceAll("[\\s()-]+", "");

        String sql  = "INSERT INTO " + dbPrefix + "call_me ( id, name, phone ) "
                + "VALUES ( UUID_SHORT(), ?, ? )";
        try(PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)) {
            prep.setString( 1, name );
            prep.setString( 2, phone );
            prep.execute();

        }
        catch ( SQLException ex ) {
            System.err.println( ex.getMessage() + " " + sql );
            resp.setStatus( 500 );
            resp.getWriter().print("\"Server error/ Details on servers logs\" ");
            return;
        }
        resp.setStatus( 201 );
        //x += 1;
        //result.addProperty("index", x);
        result.addProperty("name", name);
        result.addProperty("phone", phone);
        result.addProperty("status", "created");

        resp.setContentType("application/json");
        resp.getWriter().print(result.toString());
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType( "application/json" );

        String callId = req.getParameter("call-id");
        if (callId == null) {
            resp.setStatus( 400 );
            resp.getWriter().print("\"Missing required parameter 'call-id'\" ");
            return;
        }
        CallMe item = callMeDao.getById(callId);
        if (item == null) {
            resp.setStatus( 404 );
            resp.getWriter().print("\"Item not found for given parameter 'call-id'\" ");
            return;
        }
        if ( item.getDeleteMoment() != null ) {
            resp.setStatus( 422 );
            resp.getWriter().print("\"Unprocessable Content: Item was processed early\" ");
            return;
        }
        if (callMeDao.delete( item )) {
            resp.setStatus(202);
            resp.getWriter().print("\"Operation completed\" ");
        }
        else {
            resp.setStatus( 500 );
            resp.getWriter().print("\"Server error\" ");
            return;
        }
    }
}