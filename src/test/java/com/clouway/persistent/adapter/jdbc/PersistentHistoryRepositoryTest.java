package com.clouway.persistent.adapter.jdbc;

import com.clouway.core.Transaction;
import com.clouway.persistent.datastore.DataStore;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertTrue;

/**
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
public class PersistentHistoryRepositoryTest {
  private ConnectionProvider provider = new ConnectionProvider();
  private DataStore dataStore = new DataStore(provider);
  private PersistentHistoryRepository historyRepository = new PersistentHistoryRepository(dataStore);

  @Before
  public void setUp() throws Exception {
    Connection connection = provider.get();
    try {
      Statement statement = connection.createStatement();
      statement.executeUpdate("TRUNCATE TABLE transaction_history;");
    } catch (SQLException e) {
      e.printStackTrace();
    }finally {
      connection.close();
    }
  }

  @Test
  public void happyPath() throws SQLException, ParseException {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Date date = format.parse("2000-01-01 00:00:00");
    Timestamp timestamp = new Timestamp(date.getTime());
    Transaction transaction = new Transaction(timestamp, "Jonathan", "Deposit", 50.0);
    addHistory(transaction, 4);

    List<Transaction> actual = historyRepository.getBalanceHistory("Jonathan", 0, 4);
    List<Transaction> expected = new ArrayList();
    expected.add(transaction);
    expected.add(transaction);
    expected.add(transaction);
    expected.add(transaction);

    assertTrue(actual.equals(expected));
  }

  @Test
  public void halfEmptyPage() throws SQLException, ParseException {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Date date = format.parse("2000-01-01 00:00:00");
    Timestamp timestamp = new Timestamp(date.getTime());
    Transaction transaction = new Transaction(timestamp, "Jonathan", "Deposit", 50.0);
    addHistory(transaction, 4);

    List<Transaction> actual = historyRepository.getBalanceHistory("Jonathan", 2, 4);
    List<Transaction> expected = new ArrayList();
    expected.add(transaction);
    expected.add(transaction);

    assertThat(actual, is(expected));
  }

  private void addHistory(Transaction transaction, Integer capacity) throws SQLException {
    Connection connection = provider.get();
    Statement statement = connection.createStatement();
    for (int i = 0; i < capacity; i++) {
      statement.executeUpdate("insert into transaction_history(Date,Name,Operation,Amount) values('" + transaction.operationDate + "','" + transaction.customerName + "','" + transaction.operationType + "'," + transaction.amount + ");");
    }
    connection.close();
  }
}
