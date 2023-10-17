package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dto.models.RegFormModel;
import step.learning.services.formparse.FormParseResult;
import step.learning.services.formparse.FormParseService;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

@Singleton
public class SignupServlet extends HttpServlet {

    private final FormParseService formParseService;

    @Inject
    public SignupServlet(FormParseService formParseService){
        this.formParseService = formParseService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //resp.getWriter().print("HomeServlet");
        HttpSession session = req.getSession();
        Integer regStatus = (Integer) session.getAttribute("reg-status");
        if (regStatus != null){
            session.removeAttribute("reg-status");

            String mess;
            if (regStatus == 0){
                mess = "Ошибка обработки данных формы";
            }
            else if (regStatus == 1){
                mess = "Ошибка валлидации данных формы";
                req.setAttribute("reg-model", session.getAttribute("reg-model"));
                session.removeAttribute("reg-model");
            }
            else{
                mess = "Регистрация успешна";
            }
            req.setAttribute("reg-mess", mess);
        }

        req.setAttribute("page-body", "signup.jsp");
        req.getRequestDispatcher("WEB-INF/_layout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        FormParseResult formParseResult = formParseService.parse(req);
        RegFormModel model;
        try {
            model = new RegFormModel(formParseResult);
        }
        catch (ParseException ex) {
            //throw new RuntimeException(ex);
            model = null;
        }

        HttpSession session = req.getSession();
        if(model == null){
           session.setAttribute("reg-status", 0);
        }
        else if (! model.getErrorMessages().isEmpty()) {
            session.setAttribute("reg-model", model);
            session.setAttribute("reg-status", 1);
        }
        else {
            session.setAttribute("reg-status", 2);
        }
        resp.sendRedirect(req.getRequestURI());
//
//        Part filePart = req.getPart("reg-avatar");
//        if (filePart != null && filePart.getSize() > 0) {
//            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
//            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
//
//            List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
//            if (!allowedExtensions.contains(fileExtension)) {
//                resp.getWriter().write("Недопустиме розширення файлу.");
//                return;
//            }
//
//            String savePath = "/path/to/your/folder";
//            File uploads = new File(savePath);
//            File file = new File(uploads, fileName);
//
//            try (InputStream input = filePart.getInputStream()) {
//                Files.copy(input, file.toPath());
//            }
//        }
    }
}
