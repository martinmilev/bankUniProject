package com.clouway.http.servlets;

import com.clouway.core.Account;
import com.clouway.core.AccountRepository;
import com.clouway.core.RegexValidator;
import com.clouway.core.ServletPageRenderer;
import com.clouway.persistent.adapter.jdbc.ConnectionProvider;
import com.clouway.persistent.adapter.jdbc.PersistentAccountRepository;
import com.clouway.persistent.datastore.DataStore;
import com.google.common.annotations.VisibleForTesting;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class RegistrationPageServlet extends HttpServlet {
  private AccountRepository repository;
  private ServletPageRenderer servletResponseWriter;

  @Ignore
  @SuppressWarnings("unused")
  public RegistrationPageServlet() {
    this(
            new PersistentAccountRepository(new DataStore(new ConnectionProvider())),
            new HtmlServletPageRenderer()
    );
  }

  @VisibleForTesting
  public RegistrationPageServlet(AccountRepository repository, ServletPageRenderer servletResponseWriter) {
    this.repository = repository;
    this.servletResponseWriter = servletResponseWriter;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    servletResponseWriter.renderPage("register.html", Collections.singletonMap("error", ""), resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String name = req.getParameter("name");
    String pswd = req.getParameter("password");

    RegexValidator nameValidator = new RegexValidator("[a-zA-Z]{1,50}");
    RegexValidator pswdValidator = new RegexValidator("[a-zA-Z_0-9]{6,18}");

    if (!repository.getByName(name).isPresent() &&
            nameValidator.check(name) &&
            pswdValidator.check(pswd)) {

      Account account = new Account(name, pswd, 0.0);
      repository.register(account);

      resp.sendRedirect("/login");
    } else {
      servletResponseWriter.renderPage("register.html", Collections.singletonMap("error", "Username is taken"), resp);
    }
  }
}