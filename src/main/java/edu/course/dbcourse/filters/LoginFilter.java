package edu.course.dbcourse.filters;

import edu.course.dbcourse.beans.UserBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpSession session = req.getSession();
        String pageRequested = req.getRequestURL().toString();
        UserBean user = (UserBean) session.getAttribute("user");
        if((user == null || user.getUser() == null) && !pageRequested.contains("authentication.xhtml"))
            resp.sendRedirect("faces/authentication.xhtml");
        else
            filterChain.doFilter(servletRequest, servletResponse);
    }
}
