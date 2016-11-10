package com.clouway.persistent.adapter.jdbc;

import com.clouway.core.DailyActivityRepository;
import com.clouway.persistent.datastore.DataStore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
public class PersistentDailyActivityRepository implements DailyActivityRepository {
  private DataStore dataStore;

  public PersistentDailyActivityRepository(DataStore dataStore) {
    this.dataStore = dataStore;
  }

  public Map<String, Integer> dailyActivity() {
    String[] types = {"Deposit", "Withdraw"};
    String query = "select count(operation) from transaction_history as disOpt where operation=? and date >= CURDATE();";
    Map<String, Integer> transactions = new HashMap();
    try {
      for (String each: types) {
        ResultSet set = dataStore.execute(query, each);
        if (set.next()) {
          transactions.put(each, set.getInt(1));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return transactions;
  }
}
