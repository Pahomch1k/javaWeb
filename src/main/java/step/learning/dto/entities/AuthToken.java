package step.learning.dto.entities;

import step.learning.dao.AuthTokenDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class AuthToken {
    private String jti ;
    private String sub ;
    private Date exp;
    private Date iat;

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public Date getExp() {
        return exp;
    }

    public void setExp(Date exp) {
        this.exp = exp;
    }

    public Date getIat() {
        return iat;
    }

    public void setIat(Date iat) {
        this.iat = iat;
    }

    public AuthToken(){

    }

    public AuthToken(ResultSet resultSet) throws SQLException {
        this.setJti( resultSet.getString( "jti" ));
        this.setSub( resultSet.getString( "sub" ));

        Timestamp moment = resultSet.getTimestamp("reg_at");
        this.setExp( moment == null ? null : new Date( moment.getTime() ));
        moment = resultSet.getTimestamp("del_at");
        this.setIat( moment == null ? null : new Date( moment.getTime() ));
    }

}
