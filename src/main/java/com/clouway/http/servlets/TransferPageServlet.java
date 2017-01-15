package com.clouway.http.servlets;

import com.clouway.core.Account;
import com.clouway.core.AccountRepository;
import com.clouway.core.ServletPageRenderer;
import com.clouway.core.Session;
import com.clouway.core.SessionsRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
@Singleton
public class TransferPageServlet extends HttpServlet {
  private final AccountRepository repository;
  private final ServletPageRenderer servletResponseWriter;
  private final SessionsRepository sessions;

  @Inject
  public TransferPageServlet(AccountRepository repository, SessionsRepository sessions, ServletPageRenderer servletResponseWriter) {
    this.repository = repository;
    this.servletResponseWriter = servletResponseWriter;
    this.sessions = sessions;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Map<String, Object> values = new HashMap();

    Optional<Account> account = repository.getByName(getName(req));
    if (account.isPresent()) {
      values.put("name", account.get().name);
      values.put("balance", account.get().amount);
      values.put("error", "");
      values.put("userList", getUserList());
    }

    servletResponseWriter.renderPage("transfer.html", values, resp);
  }

  private String getUserList() {
    List<Account> accounts = repository.getAll();
    String list = "";
    for (Account acc : accounts) {
      list += "<option>" + acc.name + "</option>";
    }
    return list;
  }

  private String getName(HttpServletRequest req) {
    Session session = null;

    for (Cookie cookie : req.getCookies()) {
      if ("SID".equals(cookie.getName())) {
        session = sessions.findBySID(cookie.getValue()).get();
      }
    }

    return session.username;
  }
}



