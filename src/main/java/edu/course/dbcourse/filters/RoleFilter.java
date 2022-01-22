package edu.course.dbcourse.filters;


import edu.course.dbcourse.Role;
import edu.course.dbcourse.beans.UserBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

public class RoleFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpSession session = req.getSession();
        String pageRequested = req.getRequestURL().toString();
        UserBean user = (UserBean) session.getAttribute("user");
        if(user == null || user.getUser() == null || !pageRequested.contains("faces")  || pageRequested.contains("faces/welcome.xhtml")
        || pageRequested.contains("faces/authentication.xhtml")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        Set<Role> roles = user.getRoles();
        int firstIndex = pageRequested.indexOf("faces/") + 6;
        int lastIndex = pageRequested.indexOf(".xhtml", firstIndex);
        String page = pageRequested.substring(firstIndex, lastIndex);
        boolean available = false;
        for (Role role:
             roles) {
            if (role.isPageAvailable(page)) {
                available = true;
                break;
            }
        }
        if(available)
            filterChain.doFilter(servletRequest, servletResponse);
        else
            resp.sendRedirect("faces/welcome.xhtml");
    }
}
