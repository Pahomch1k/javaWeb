package step.learning.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dto.entities.User;
import step.learning.dto.models.RegFormModel;
import step.learning.services.db.DbProvider;
import step.learning.services.hash.HashService;
import step.learning.services.kdf.KdfService;
import step.learning.services.random.RandomService;

import javax.inject.Named;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class UserDao {
    private final DbProvider dbProvider;
    private final String dbPrefix;
    private final Logger logger;
    private final RandomService randomService;
    private final HashService hashService;
    private final KdfService kdfService;



    @Inject
    public UserDao(DbProvider dbProvider, @Named("db-prefix") String dbPrefix, Logger logger, RandomService randomService,
                   @Named("Digest-Hash") HashService hashService, KdfService kdfService) {
        this.dbProvider = dbProvider;
        this.dbPrefix = dbPrefix;
        this.logger = logger;
        this.randomService = randomService;
        this.hashService = hashService;
        this.kdfService = kdfService;
    }

    public boolean install() {
        String sql = "CREATE TABLE " + dbPrefix + "users (" +
                "`id` BIGINT UNSIGNED PRIMARY KEY DEFAULT ( UUID_SHORT() )," +
                "`login` VARCHAR(64) NOT NULL UNIQUE, " +
                "`name` VARCHAR(64) NOT NULL, " +
                "`salt` CHAR(16) NOT NULL COMMENT 'RFC 2898 -- Salt', " +
                "`pass_dk` CHAR(32) NOT NULL COMMENT 'RFC 2898 -- DK: Derived key', " +
                "`email` VARCHAR(96) NOT NULL, " +
                "`email_code` CHAR(6) NULL, " +
                "`avatar_url` VARCHAR(64) NOT NULL, " +
                "`birthdate` DATE NOT NULL, " +
                "`reg_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "`del_at` DATETIME NULL " +
                ") ENGINE = INNODB, DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_unicode_ci";

        try (Statement statement = dbProvider.getConnection().createStatement()) {
            statement.executeUpdate( sql );
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage() + " --- " + sql);
        }
        return false;
    }

    public boolean addFromForm( RegFormModel model ) {
        String salt = randomService.randomHex(16);
        String passDk = kdfService.getDerivedKey(model.getPass(), salt );
        String emailCode = randomService.randomHex(6);

        String sql = "INSERT INTO " + dbPrefix + "users(" +
                "`name`, `salt`, `pass_dk`, `email`, `email_code`, `avatar_url`, `birthdate`, login) " +
                "VALUES(?,?,?,?,?,?,?,?) ";

        try (PreparedStatement prep = dbProvider.getConnection().prepareStatement( sql )){
         prep.setString( 1, model.getName() );
         prep.setString( 2, salt );
         prep.setString( 3, passDk );
         prep.setString( 4, model.getEmail() );
         prep.setString( 5, emailCode );
         prep.setString( 6, model.getAvatar() );
         prep.setString( 7, model.getBirthdateAsString() );
         prep.setString( 8, model.getLogin() );
         prep.executeUpdate();
         return true;
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage() + " --- " + sql);
        }
        return false;
    }

    public User getUserByCredentials(String login, String password ) {
        if ( login == null || password == null ){
            return null;
        }
        String sql = "SELECT u.* FROM " + dbPrefix + "users u WHERE u.`login` =  ? ";
        try(PreparedStatement prep = dbProvider.getConnection().prepareStatement( sql )) {
            prep.setString( 1, login);
            ResultSet resultSet = prep.executeQuery();
            if ( resultSet.next() ){
                User user = new User( resultSet );
                String salt = user.getSalt();
                String passDk = kdfService.getDerivedKey(password, salt );
                if ( passDk.equals( user.getPassDK() )){
                    return user;
                }
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage() + " --- " + sql);
        }
        return null;
    }
}
