package step.learning.services.formparse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class MixedFormParseService implements FormParseService {
    private static final int MEMORY_THRESHOLD = 10 * 1024 * 1024; //10MB
    private static final int MAX_FORM_SIZE = 40 * 1024 * 1024; //40MB

    private final ServletFileUpload fileUpload;
    @Inject
    public MixedFormParseService(){
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        fileItemFactory.setSizeThreshold(MEMORY_THRESHOLD);
        fileItemFactory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        fileUpload = new ServletFileUpload(fileItemFactory);
        fileUpload.setFileSizeMax(MAX_FORM_SIZE);
        fileUpload.setSizeMax(MAX_FORM_SIZE);

    }

    @Override
    public  FormParseResult parse(HttpServletRequest request){
        final Map<String, String> fields = new HashMap<>();
        final Map<String, FileItem> files = new HashMap<>();
        final HttpServletRequest req = request;

        boolean isMultipart = request.getHeader("Content-Type").startsWith("multipart/form-data");

        String charsetName = (String) request.getAttribute("charsetName");
        if (charsetName == null){
            charsetName =  StandardCharsets.UTF_8.name();
        }
        if (isMultipart){
            try {
                for (FileItem item : fileUpload.parseRequest(request)) {
                    if (item.isFormField()){
                        fields.put(item.getFieldName(), item.getString(charsetName));
                    }
                    else{
                        files.put(item.getFieldName(), item);
                    }
                }
            }
            catch (FileUploadException | UnsupportedEncodingException ex){
                throw new RuntimeException(ex);
            }
        }
        else {
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement();
                fields.put(name, request.getParameter(name));
            }
        }

        return new FormParseResult() {
            @Override public Map<String, String> getFields() { return fields; }
            @Override public Map<String, FileItem> getFiles() { return files; }
            @Override public HttpServletRequest getRequest() { return req; }

        };
    }
}
