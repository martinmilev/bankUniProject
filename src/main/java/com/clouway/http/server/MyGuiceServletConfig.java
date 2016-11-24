package com.clouway.http.server;

import com.clouway.core.CoreModule;
import com.clouway.http.filters.SecurityFilter;
import com.clouway.http.servlets.AccountPageServlet;
import com.clouway.http.servlets.LoginPageServlet;
import com.clouway.http.servlets.LogoutServlet;
import com.clouway.http.servlets.RegistrationPageServlet;
import com.clouway.http.servlets.TransactionHistoryPageServlet;
import com.clouway.http.servlets.TransactionPageServlet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class MyGuiceServletConfig extends GuiceServletContextListener {
  @Override
  protected Injector getInjector() {
    return Guice.createInjector(new CoreModule(),new ServletModule() {
      @Override
      protected void configureServlets() {
        filter("/*").through(SecurityFilter.class);

        serve("/account").with(AccountPageServlet.class);
        serve("/login").with(LoginPageServlet.class);
        serve("/logout").with(LogoutServlet.class);
        serve("/register").with(RegistrationPageServlet.class);
        serve("/transaction").with(TransactionPageServlet.class);
        serve("/history").with(TransactionHistoryPageServlet.class);
      }
    });
  }
}
