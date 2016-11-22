package com.clouway.persistent.adapter.jdbc;

import com.clouway.core.DailyActivityRepository;
import com.clouway.persistent.datastore.DataStore;
import com.clouway.persistent.datastore.RowFetcher;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
public class PersistentDailyActivityRepository implements DailyActivityRepository {
  private DataStore dataStore;
  private Map<String, Integer> transactions = new HashMap();

  public PersistentDailyActivityRepository(DataStore dataStore) {
    this.dataStore = dataStore;
  }

  public Map<String, Integer> dailyActivity() {
    transactions.put("Deposit", get("Deposit"));
    transactions.put("Withdraw", get("Withdraw"));
    return transactions;
  }

  private Integer get(String operation) {
    String query = "select count(operation) from transaction_history as disOpt where operation='" + operation + "' and date >= CURDATE();";
    return dataStore.fetchRows(query, new RowFetcher<Integer>() {
      @Override
      public Integer fetchRow(ResultSet resultSet) {
        try {
          return resultSet.getInt(1);
        } catch (SQLException e) {
          e.printStackTrace();
        } return null;
      }
    }).get(0);
  }
}
