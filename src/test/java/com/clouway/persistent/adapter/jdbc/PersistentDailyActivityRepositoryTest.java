package com.clouway.persistent.adapter.jdbc;

import com.clouway.core.DailyActivityRepository;
import com.clouway.core.Transaction;
import com.clouway.persistent.datastore.DataStore;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
public class PersistentDailyActivityRepositoryTest {
  private ConnectionProvider provider = new ConnectionProvider();
  private DataStore dataStore = new DataStore(provider);
  private DailyActivityRepository activityRepository = new PersistentDailyActivityRepository(dataStore);

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
  public void getDailyTransactions() throws SQLException {
    Timestamp timestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
    addHistory(new Transaction(timestamp, "Jonathan", "Deposit", 50.0), 2);
    addHistory(new Transaction(timestamp, "Jonathan", "Withdraw", 50.0), 1);

    Map<String, Integer> actual = activityRepository.dailyActivity();

    assertTrue(actual.equals(new HashMap<String, Integer>() {{
      put("Deposit", 2);
      put("Withdraw", 1);
    }}));
  }

  @Test
  public void getOutDatedTransactions() throws SQLException {
    Timestamp timestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
    Timestamp yesterday = new Timestamp(timestamp.getTime() - 60*60*24*1000);
    addHistory(new Transaction(timestamp, "Jonathan", "Deposit", 50.0), 1);
    addHistory(new Transaction(yesterday, "Jonathan", "Withdraw", 50.0), 1);

    Map<String, Integer> actual = activityRepository.dailyActivity();

    assertTrue(actual.equals(new HashMap<String, Integer>() {{
      put("Deposit", 1);
      put("Withdraw", 0);
    }}));
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
