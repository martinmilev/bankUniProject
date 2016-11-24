package com.clouway.persistent.adapter.jdbc;

import com.clouway.core.Session;
import com.clouway.core.SessionsRepository;
import com.clouway.persistent.datastore.DataStore;
import com.clouway.persistent.datastore.RowFetcher;
import com.google.inject.Inject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class PersistentSessionRepository implements SessionsRepository {
  private final DataStore dataStore;

  @Inject
  public PersistentSessionRepository(DataStore dataStore) {
    this.dataStore = dataStore;
  }

  @Override
  public void save(Session session) {
    String query = "insert into sessions values(?,?,?)";
    dataStore.update(query, session.id, session.username, session.creationDate);
  }

  @Override
  public Optional<Session> findBySID(String uid) {
    String query = "select * from sessions where id='" + uid + "'";
    List<Session> session = getSession(query);
    return session.isEmpty() ? Optional.empty() : Optional.ofNullable(session.iterator().next());
  }

  @Override
  public List<Session> getAll() {
    String query = "select * from sessions";
    return getSession(query);
  }

  @Override
  public Integer countSessions() {
    String query = "select count(distinct Name) from sessions";
    return dataStore.fetchRows(query, resultSet -> {
      try {
        return resultSet.getInt(1);
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return 0;
    }).get(0);
  }

  @Override
  public void deleteByID(String id) {
    String query = "delete from sessions where ID=?";
    dataStore.update(query, id);
  }

  private List<Session> getSession(String query) {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    return dataStore.fetchRows(query, new RowFetcher<Session>() {
      @Override
      public Session fetchRow(ResultSet resultSet) {
        try {
          return new Session(resultSet.getString(1), resultSet.getString(2), format.parse(resultSet.getString(3)));
        } catch (SQLException | ParseException e) {
          e.printStackTrace();
        }
        return null;
      }
    });
  }
}