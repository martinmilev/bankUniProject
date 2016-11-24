package com.clouway;

import com.clouway.core.CoreModule;
import com.clouway.core.SessionsCleaner;
import com.clouway.http.server.JettyServer;
import com.google.common.base.Strings;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class App {
  public static void main(String[] args) {
    Injector coreInjector = Guice.createInjector(new CoreModule());

    new Thread() {
      private SessionsCleaner cleaner = coreInjector.getInstance(SessionsCleaner.class);

      @Override
      public void run() {
        try {
          while (true) {
            cleaner.cleanSessions();
            Thread.sleep(10000L);
          }
        } catch (InterruptedException e) {
        }
      }
    }.start();

    // User default database when no configuration is specified
    if (Strings.isNullOrEmpty(System.getenv("BANK_DB_HOST"))) {
      System.setProperty("BANK_DB_HOST", "localhost");
      System.setProperty("BANK_DB_USER", "root");
      System.setProperty("BANK_DB_PASS", "123123");
      System.setProperty("BANK_DB_NAME", "myBank");
    }
    JettyServer server = new JettyServer(8080);
    server.start();
  }
}