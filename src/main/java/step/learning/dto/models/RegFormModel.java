package step.learning.dto.models;

import org.apache.commons.fileupload.FileItem;
import step.learning.services.formparse.FormParseResult;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class RegFormModel {
    private String name;
    private String login;
    private String pass;
    private String repeat;
    private String email;
    private Date birthdate;
    private boolean isAgree;
    private String avatar;

//    public RegFormModel(HttpServletRequest request) throws ParseException {
//        this.setName(request.getParameter("reg-name") );
//        this.setLogin(request.getParameter("reg-login") );
//        this.setPass(request.getParameter("reg-pass") );
//        this.setRepeat(request.getParameter("reg-repeat") );
//        this.setEmail(request.getParameter("reg-email") );
//        this.setBirthdate(request.getParameter("reg-birthdate") );
//        this.setIsAgree(request.getParameter("reg-rules") );
//
//    }

    public RegFormModel(FormParseResult result) throws ParseException {
        Map<String, String> fields = result.getFields();
        this.setName(fields.get("reg-name") );
        this.setLogin(fields.get("reg-login") );
        this.setPass(fields.get("reg-pass") );
        this.setRepeat(fields.get("reg-repeat") );
        this.setEmail(fields.get("reg-email") );
        this.setBirthdate(fields.get("reg-birthdate") );
        this.setIsAgree(fields.get("reg-rules") );

        this.setAvatar(result);
    }

    private void setAvatar(FormParseResult result) throws ParseException {
        Map<String, FileItem> files = result.getFiles();
        if (! files.containsKey("reg-avatar")){
            this.avatar = null;
            return;
        }
        FileItem item = files.get("reg-avatar");
        String targetDir = result.getRequest().getServletContext().getRealPath("./");
        String submittedFilename = item.getName();
        String ext = submittedFilename.substring(submittedFilename.lastIndexOf('.'));

        String savedFilename;
        File savedFile;
        do {
            savedFilename = UUID.randomUUID().toString().substring(0,8) + ext;
            savedFile = new File(targetDir, savedFilename);
        }
        while (savedFile.exists());

        try {
            item.write(savedFile);
        }
        catch (Exception ex){
            throw new ParseException("File upload error", 0);
        }
        this.avatar = savedFilename;

        this.avatar = savedFilename;
    }

        public Map<String, String> getErrorMessages() {
        Map<String, String> result = new HashMap<>();
        if(login == null || "".equals(login)){
            result.put("login", "логин не может быть пустым");
        }
        else if(!Pattern.matches("^[a-zA-z0-9]+$",login)){
            result.put("login", "логин не по шаблону");
        }

        if(name == null || "".equals(name)){
            result.put("name", "имя не может быть пустым");
        }
        return result;
    }
    private static final SimpleDateFormat formDate =
            new SimpleDateFormat("yyyy-MM-dd");
    
    
    // region Accessor
    public String  setBirthdateAsString() {
        if (birthdate == null ) {
            return "";
        }
        return formDate.format(getBirthdate());
    }
    public void setBirthdate(String birthdate)  throws ParseException {
        if (birthdate == null || "".equals(birthdate)) {
            this.birthdate = null;
        }
        else {
            this.birthdate = formDate.parse(birthdate);
        }

    }

    public void setIsAgree(String isAgree) {
        this.isAgree = "on".equalsIgnoreCase(isAgree) || "true".equalsIgnoreCase(isAgree);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isAgree() {
        return isAgree;
    }

    public void setAgree(boolean agree) {
        isAgree = agree;
    }
    // endregion
}