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
import java.util.Map;
import java.util.Optional;

/**
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
@Singleton
public class TransactionPageServlet extends HttpServlet {
  private final AccountRepository repository;
  private final ServletPageRenderer servletResponseWriter;
  private final SessionsRepository sessions;

  @Inject
  public TransactionPageServlet(AccountRepository repository, SessionsRepository sessions, ServletPageRenderer servletResponseWriter) {
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
    }

    servletResponseWriter.renderPage("transaction.html", values, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Map<String, Object> values = new HashMap<String, Object>();
    Account account = null;
    String type = req.getParameter("transaction");
    Double amount = Double.parseDouble(req.getParameter("amount"));

    Optional<Account> optAccount = repository.getByName(getName(req));

    if(optAccount.isPresent()){
      account = optAccount.get();
    } else {
      resp.sendRedirect("/login");
      return;
    }

    values.put("name", account.name);
    values.put("error", "");
    values.put("balance", account.amount);

    if ("deposit".equals(type) && amount > 0) {
      repository.deposit(account.name, amount);
      values.put("balance", account.amount + amount);
    } else if ("withdraw".equals(type) && amount > 0) {
      if (account.amount >= amount) {
        repository.withdraw(account.name, amount);
        values.put("balance", account.amount - amount);
      } else {
        values.put("error", "Insufficient funds");
      }
    } else {
      throw new IllegalStateException(String.format("operationType = '%s' is not currently supported", type));
    }

    servletResponseWriter.renderPage("transaction.html", values, resp);
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
