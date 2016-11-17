package com.clouway.persistent.adapter.jdbc;

import com.clouway.core.Session;
import com.clouway.persistent.DatastoreCleaner;
import com.clouway.persistent.datastore.DataStore;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class PersistentSessionRepositoryTest {
  private PersistentSessionRepository repo;

  @Before
  public void setUp() throws Exception {
    repo = new PersistentSessionRepository(new DataStore(new ConnectionProvider()));
    DatastoreCleaner datastoreCleaner = new DatastoreCleaner("accounts", "sessions");
    datastoreCleaner.perform();
  }

  @Test
  public void findSessionBySID() throws Exception {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Date date = format.parse("2000-01-01 00:00:00");
    Session session = new Session("id", "John", date);

    repo.save(session);
    Session actual = repo.findBySID("id").get();
    Session expected = new Session("id", "John", date);

    assertThat(actual, is(expected));
  }

  @Test
  public void countingSessions() throws Exception {
    Date date = new Date();

    repo.save(new Session("id1", "userA", date));
    repo.save(new Session("id2", "userB", date));
    assertThat(repo.countSessions(), is(2));
  }
}