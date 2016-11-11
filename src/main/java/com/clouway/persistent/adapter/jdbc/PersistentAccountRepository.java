package com.clouway.persistent.adapter.jdbc;


import com.clouway.core.Account;
import com.clouway.core.AccountRepository;
import com.clouway.persistent.datastore.DataStore;
import com.clouway.persistent.datastore.RowFetcher;
import com.clouway.persistent.datastore.TransactionCall;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class PersistentAccountRepository implements AccountRepository {
  private final DataStore dataStore;

  public PersistentAccountRepository(DataStore dataStore) {
    this.dataStore = dataStore;
  }

  @Override
  public void register(Account account) {
    String query = "insert into accounts values(null,?,?,?)";
    dataStore.update(query, account.name, account.password, account.amount);
  }

  @Override
  public void deposit(String name, Double balance) {
    dataStore.runInTransaction(new TransactionCall() {
      @Override
      public void execute(Connection connection) {
        BigDecimal currentBalance = dataStore.fetchOne(connection, "select amount from accounts where name=?", name);
        dataStore.executeUpdate(connection, "update accounts set amount=? where name=?", currentBalance.add(BigDecimal.valueOf(balance)), name);
      }
    });
  }

  @Override
  public void withdraw(String name, Double balance) {
    dataStore.runInTransaction(new TransactionCall() {
      @Override
      public void execute(Connection connection) {
        BigDecimal currentBalance = dataStore.fetchOne(connection, "select amount from accounts where name=?", name);
        dataStore.executeUpdate(connection, "update accounts set amount=? where name=?", currentBalance.subtract(BigDecimal.valueOf(balance)), name);
      }
    });
  }

  @Override
  public Optional<Account> getByName(String name) {
    String query = "select * from accounts where Name='" + name + "'";
    List<Account> accounts = dataStore.fetchRows(query, new RowFetcher<Account>() {
      @Override
      public Account fetchRow(ResultSet resultSet) {
        try {
          return new Account(resultSet.getString(2), resultSet.getString(3), resultSet.getDouble(4));
        } catch (SQLException e) {
          e.printStackTrace();
        }
        return null;
      }
    });
    return accounts.isEmpty() ? Optional.empty() : Optional.ofNullable(accounts.iterator().next());
  }
}