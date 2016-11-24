package com.clouway.http.servlets;

import com.clouway.core.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
@Singleton
public class LoginPageServlet extends HttpServlet {
  private final AccountRepository repository;
  private final SessionsRepository sessions;
  private final ServletPageRenderer servletResponseWriter;
  private final MyClock clock;
  private final Provider uuid;

  @Inject
  public LoginPageServlet(AccountRepository repository,
                          SessionsRepository sessions,
                          ServletPageRenderer servletResponseWriter,
                          MyClock clock,
                          @Named("UUID") Provider uuidGenerator) {
    this.repository = repository;
    this.servletResponseWriter = servletResponseWriter;
    this.sessions = sessions;
    this.clock = clock;
    this.uuid = uuidGenerator;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    servletResponseWriter.renderPage("login.html", Collections.singletonMap("error", ""), resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    String name = req.getParameter("name");
    String pswd = req.getParameter("password");

    Optional<Account> possibleAccount = repository.getByName(name);
    RegexValidator nameValidator = new RegexValidator("[a-zA-Z]{1,50}");

    if (!possibleAccount.isPresent() || !nameValidator.check(name)) {
      servletResponseWriter.renderPage("login.html", Collections.singletonMap("error", "Wrong username"), resp);
      return;
    }

    Account account = possibleAccount.get();
    RegexValidator pswdValidator = new RegexValidator("[a-zA-Z_0-9]{6,18}");

    if (!pswd.equals(account.password) || !pswdValidator.check(pswd)) {
      servletResponseWriter.renderPage("login.html", Collections.singletonMap("error", "Wrong password"), resp);
    } else {
      String sid = uuid.get().toString();
      Date current = clock.getDate();
      Session session = new Session(sid, name, current);
      sessions.save(session);
      Cookie cookie = new Cookie("SID", sid);
      cookie.setMaxAge(30000);
      resp.addCookie(cookie);
      resp.sendRedirect("/");
    }
  }
}