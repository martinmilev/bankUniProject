package com.clouway.persistent.datastore;

import com.clouway.core.Provider;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class DataStore {
  private Provider<Connection> provider;

  public DataStore(Provider<Connection> provider) {
    this.provider = provider;
  }

  public void update(String query, Object... objects) {
    Connection connection = provider.get();
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      fillStatement(statement, objects);
      statement.execute();
    } catch (SQLException e) {
      throw new IllegalStateException("Connection to the database wasn't established", e);
    } finally {
      close(connection);
    }
  }

  public ResultSet execute(String query, Object... objects) throws SQLException {
    Connection connection = provider.get();
    ResultSet set = null;
    try {
      PreparedStatement statement = connection.prepareStatement(query);
      fillStatement(statement, objects);
      set = statement.executeQuery();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return set;
  }

  public <T> List<T> fetchRows(String query, RowFetcher<T> rowFetcher) {
    List<T> list = Lists.newArrayList();
    Connection connection = provider.get();
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      ResultSet resultSet = statement.executeQuery(query);
      while (resultSet.next()) {
        T row = rowFetcher.fetchRow(resultSet);
        list.add(row);
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Connection to the database wasn't established");
    } finally {
      close(connection);
    }
    return list;
  }

  private void fillStatement(PreparedStatement statement, Object... objects) throws SQLException {
    for (int i = 0; i < objects.length; i++) {
      statement.setObject(i + 1, objects[i]);
    }
  }

  private void close(Connection connection) {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public void runInTransaction(TransactionCall callback) {
    Connection connection = provider.get();
    try {
      connection.setAutoCommit(false);

      callback.execute(connection);

      connection.commit();
    } catch (SQLException e) {
      try {
        if (connection != null) {
          connection.rollback();
        }
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    } finally {
      try {
        connection.setAutoCommit(true);
        close(connection);
      } catch (SQLException e) {
        throw new IllegalStateException("Connection cannot be moved to it's original state.", e);
      }
    }
  }

  public BigDecimal fetchOne(Connection connection, String query, Object... params) {
    BigDecimal decimal = null;
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      fillStatement(statement, params);
      ResultSet set = statement.executeQuery();
      if (set.next()) {
        decimal = BigDecimal.valueOf(set.getDouble(1));
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Connection to the database wasn't established", e);
    }
    return decimal;
  }

  public void executeUpdate(Connection connection, String query, Object... params) {
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      fillStatement(statement, params);
      statement.execute();
    } catch (SQLException e) {
      throw new IllegalStateException("Connection to the database wasn't established", e);
    }
  }
}