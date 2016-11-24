package com.clouway.http.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class JettyServer {
  private final int port;

  public JettyServer(int port) {
    this.port = port;
  }

  public void start() {
    Server server = new Server(port);

    WebAppContext context = new WebAppContext();
    context.setResourceBase("src/main/webapp");
    context.setWar("src/main/webapp");
    context.setContextPath("/");
    context.addEventListener(new MyGuiceServletConfig());
    context.setParentLoaderPriority(true);
    server.setHandler(context);
    try {
      server.start();
      server.join();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}