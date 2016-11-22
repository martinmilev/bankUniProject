package com.clouway;

import com.clouway.core.MyServerClock;
import com.clouway.core.SessionsCleaner;
import com.clouway.http.server.JettyServer;
import com.clouway.persistent.adapter.jdbc.ConnectionProvider;
import com.clouway.persistent.adapter.jdbc.PersistentSessionRepository;
import com.clouway.persistent.datastore.DataStore;
import com.google.common.base.Strings;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class App {
  public static void main(String[] args) {

    new Thread() {
      private SessionsCleaner cleaner = new SessionsCleaner
              (new PersistentSessionRepository(new DataStore(new ConnectionProvider())), new MyServerClock());

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