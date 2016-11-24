package com.clouway.core;

import com.google.inject.Inject;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class SessionsCleaner {
  private final SessionsRepository sessionsRepo;
  private final MyClock clock;
  private final Integer sessionLife = 5;

  @Inject
  public SessionsCleaner(SessionsRepository sessionsRepo, MyClock clock) {
    this.sessionsRepo = sessionsRepo;
    this.clock = clock;
  }

  public void cleanSessions() {
    Date currentTime = clock.getDate();
    for (Session session : sessionsRepo.getAll()) {
      Date sessionExpiration = DateUtils.addMinutes(session.creationDate, sessionLife);
      if (sessionExpiration.before(currentTime)) {
        sessionsRepo.deleteByID(session.id);
      }
    }
  }
}