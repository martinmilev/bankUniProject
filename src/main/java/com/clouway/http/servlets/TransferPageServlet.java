package com.clouway.http.servlets;

import com.clouway.core.Account;
import com.clouway.core.AccountRepository;
import com.clouway.core.ServletPageRenderer;
import com.clouway.core.Session;
import com.clouway.core.SessionsRepository;
import com.clouway.core.Transfer;
import com.clouway.core.TransferRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
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
  private final TransferRepository transferRepository;
  private String username;

  @Inject
  public TransferPageServlet(AccountRepository repository, SessionsRepository sessions, ServletPageRenderer servletResponseWriter, TransferRepository transferRepository) {
    this.repository = repository;
    this.servletResponseWriter = servletResponseWriter;
    this.sessions = sessions;
    this.transferRepository = transferRepository;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Map<String, Object> values = new HashMap();

    Optional<Account> account = repository.getByName(getName(req));
    if (account.isPresent()) {
      username = account.get().name;
      values.put("name", username);
      values.put("balance", account.get().amount);
      values.put("error", "");
      values.put("msg", "");
    }

    servletResponseWriter.renderPage("transfer.html", values, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Map<String, Object> values = new HashMap<String, Object>();
    Account user = null;
    Account recipient = null;
    String recipientName = req.getParameter("recipient");
    Double transferAmount = Double.parseDouble(req.getParameter("amount"));

    Optional<Account> optAccountUser = repository.getByName(username);
    Optional<Account> optAccountRecipient = repository.getByName(recipientName);

    if (optAccountUser.isPresent()) {
      user = optAccountUser.get();
    } else {
      resp.sendRedirect("/login");
      return;
    }

    if (!optAccountRecipient.isPresent()) {
      values.put("error", "No such user exists!!!!");
    } else {
      recipient = optAccountRecipient.get();
      values.put("name", username);
      values.put("error", "");
      values.put("msg", "");
      values.put("balance", user.amount);

      if (username.equals(recipient.name)) {
        values.put("error", "Invalid Operation!!!!");
      } else if (transferAmount > user.amount) {
        values.put("error", "Insufficient funds");
      } else {
        values.put("balance", user.amount - transferAmount);
        values.put("msg", "Yo've successfully sended " + transferAmount + " to " + recipient.name + ".");
        repository.withdraw(user.name, transferAmount);
        repository.deposit(recipient.name, transferAmount);
        transferRepository.reg(new Transfer(new Timestamp(Calendar.getInstance().getTime().getTime()), username, recipientName, transferAmount));
      }
    }

    servletResponseWriter.renderPage("transfer.html", values, resp);
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



