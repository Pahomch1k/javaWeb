package step.learning.filters;

import com.google.inject.Singleton;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Singleton
public class CharsetFilter implements Filter {
    private FilterConfig filterConfig;
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String charsetName = StandardCharsets.UTF_8.name();
        req.setCharacterEncoding(charsetName);
        resp.setCharacterEncoding(charsetName);

        req.setAttribute("charsetName", charsetName);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
        this.filterConfig = null;
    }
}
