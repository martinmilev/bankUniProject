package com.clouway.persistent.adapter.jdbc;

import com.clouway.core.Transfer;
import com.clouway.core.TransferRepository;
import com.clouway.persistent.datastore.DataStore;
import com.google.inject.Inject;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class PersistentTransferRepository implements TransferRepository {
  private final DataStore dataStore;

  @Inject
  public PersistentTransferRepository(DataStore dataStore) {
    this.dataStore = dataStore;
  }

  @Override
  public void reg(Transfer transfer) {
    String query = "insert into transfer_history values(?,?,?,?)";
    dataStore.update(query, transfer.date, transfer.from, transfer.to, transfer.amount);
  }

  @Override
  public List<Transfer> getHistory(String username, int offset, int limit) {
    String query = "select * from transfer_history where NameFrom='" + username + "' OR NameTo='" + username + "' order by date limit " + offset + "," + limit + ";";
    return dataStore.fetchRows(query, set -> {
      try {
        return new Transfer(set.getTimestamp(1), set.getString(2), set.getString(3), set.getDouble(4));
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return null;
    });
  }
}
