package com.clouway.http.servlets;

import com.clouway.core.*;
import com.clouway.persistent.adapter.jdbc.ConnectionProvider;
import com.clouway.persistent.adapter.jdbc.PersistentAccountRepository;
import com.clouway.persistent.adapter.jdbc.PersistentDailyActivityRepository;
import com.clouway.persistent.adapter.jdbc.PersistentSessionRepository;
import com.clouway.persistent.datastore.DataStore;
import com.google.common.annotations.VisibleForTesting;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class AccountPageServlet extends HttpServlet {
  private final ServletPageRenderer servletResponseWriter;
  private final AccountRepository repository;
  private final DailyActivityRepository activityRepository;
  private final SessionsRepository sessions;

  @Ignore
  @SuppressWarnings("unused")
  public AccountPageServlet() {
    this(
            new PersistentAccountRepository(new DataStore(new ConnectionProvider())),
            new PersistentDailyActivityRepository(new DataStore(new ConnectionProvider())),
            new PersistentSessionRepository(new DataStore(new ConnectionProvider())),
            new HtmlServletPageRenderer()
    );
  }

  @VisibleForTesting
  public AccountPageServlet(AccountRepository repository, DailyActivityRepository activityRepository, SessionsRepository sessions, ServletPageRenderer servletResponseWriter) {
    this.servletResponseWriter = servletResponseWriter;
    this.repository = repository;
    this.activityRepository = activityRepository;
    this.sessions = sessions;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Map<String, Object> params = new LinkedHashMap<>();
    Session session = null;

    for (Cookie cookie : req.getCookies()) {
      if ("SID".equals(cookie.getName())) {
        session = sessions.findBySID(cookie.getValue()).get();
      }
    }

    Map<String, Integer> transactions = activityRepository.dailyActivity();

    Account account = repository.getByName(session.username).get();
    params.put("name", account.name);
    params.put("balance", account.amount);
    params.put("count", sessions.countSessions());
    params.put("deposits", transactions.get("Deposit"));
    params.put("withdraws", transactions.get("Withdraw"));
    servletResponseWriter.renderPage("account.html", params, resp);
  }
}