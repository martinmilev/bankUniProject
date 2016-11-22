package com.clouway.core;

import com.clouway.persistent.DatastoreCleaner;
import com.clouway.persistent.adapter.jdbc.ConnectionProvider;
import com.clouway.persistent.adapter.jdbc.PersistentSessionRepository;
import com.clouway.persistent.datastore.DataStore;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class SessionsCleanerTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private MyClock clock = context.mock(MyClock.class);

  private PersistentSessionRepository repo;

  @Before
  public void setUp() throws Exception {
    repo = new PersistentSessionRepository(new DataStore(new ConnectionProvider()));
    DatastoreCleaner datastoreCleaner = new DatastoreCleaner("sessions");
    datastoreCleaner.perform();
  }

  @Test
  public void happyPath() throws Exception {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Date dateCurrent = format.parse("2000-01-01 00:10:00");
    Date dateExpired = format.parse("2000-01-01 00:00:00");
    Date dateValid = format.parse("2000-01-01 00:08:00");

    Session invalidSession = new Session("id1", "name", dateExpired);
    Session validSession = new Session("id2", "name", dateValid);
    repo.save(invalidSession);
    repo.save(validSession);

    SessionsCleaner cleaner = new SessionsCleaner(repo, clock);

    context.checking(new Expectations() {{
      oneOf(clock).getDate();
      will(returnValue(dateCurrent));
    }});

    cleaner.cleanSessions();
    Session actual = repo.getAll().get(0);

    assertThat(actual, is(validSession));
  }
}