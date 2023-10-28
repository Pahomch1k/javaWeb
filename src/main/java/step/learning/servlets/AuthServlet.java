package step.learning.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dao.AuthTokenDao;
import step.learning.dao.UserDao;
import step.learning.dto.entities.AuthToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

@Singleton
public class AuthServlet extends HttpServlet {

    private final static Gson gson = new GsonBuilder().serializeNulls().create();
    private final UserDao userDao;

    private final AuthTokenDao authTokenDao;
    @Inject
    public AuthServlet(UserDao userDao, AuthTokenDao authTokenDao) {
        this.userDao = userDao;
        this.authTokenDao = authTokenDao;
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ( userDao.install() ) {
            sendResponse( resp, 201, "Created" );
        }
        else {
            sendResponse( resp, 500, "Error, see server log");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        if (login == null || login.isEmpty() || password == null) {
         sendResponse( resp, 400, "Missing required parameters: login and/or password" ) ;
         return;
        }
        AuthToken authToken = authTokenDao.getTokenByCredentials( login, password );
        if ( authToken == null) {
            sendResponse( resp, 401, "Auth Rejected for given login or password");
            return;
        }
        String json = gson.toJson( authToken );
        String base64code = Base64.getUrlEncoder().encodeToString( json.getBytes());
        resp.setContentType( "text/plain" );
        resp.getWriter().print( base64code );
    }

    private void sendResponse(HttpServletResponse resp, int status, Object body) throws IOException{
        resp.setContentType("application/json");
        resp.setStatus( status );
        resp.getWriter().print( gson.toJson( body ));
    }
}
