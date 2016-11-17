package com.clouway.http.filters;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class SecurityFilter implements Filter {
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) servletRequest;
    HttpServletResponse resp = ((HttpServletResponse) servletResponse);

    Cookie cookie = getCookie(req);
    String requestURI = req.getRequestURI();

    if (cookie == null) {
      if (!"/login".equals(requestURI) && !"/register".equals(requestURI)) {
        resp.sendRedirect("login");
        return;
      }
    } else {
      if ("/login".equals(requestURI) || "/register".equals(requestURI)) {
        resp.sendRedirect("account");
        return;
      }
    }

    filterChain.doFilter(req, resp);
  }

  private Cookie getCookie(HttpServletRequest req) {
    Cookie[] cookies = req.getCookies();
    for (Cookie c : cookies) {
      if (c.getName().equals("SID")) {
        return c;
      }
    }
    return null;
  }

  @Override
  public void destroy() {

  }
}