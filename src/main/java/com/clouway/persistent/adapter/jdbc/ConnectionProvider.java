package com.clouway.persistent.adapter.jdbc;

import com.clouway.core.ConnectionPool;
import com.clouway.core.Provider;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */

public class ConnectionProvider implements Provider<Connection> {

  @Override
  public java.sql.Connection get() {
    java.sql.Connection connection = null;
    try {
      connection = ConnectionPool.get().getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return connection;
  }
}