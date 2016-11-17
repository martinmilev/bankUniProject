package com.clouway.core;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class SessionsCleaner implements Runnable {
  private final Integer sessionLife;
  private final SessionsRepository sessionsRepo;
  private final MyClock clock;

  public SessionsCleaner(Integer sessionLife, SessionsRepository sessionsRepo, MyClock clock) {
    this.sessionLife = sessionLife;
    this.sessionsRepo = sessionsRepo;
    this.clock = clock;
  }

  @Override
  public void run() {
    try{
      while (true) {
        Thread.sleep(10000);
        cleanSessions();
      }
    } catch (InterruptedException e) {}
  }

  private void cleanSessions() {
    Date currentTime = clock.getDate();
    for (Session session : sessionsRepo.getAll()) {
      Date sessionExpiration = DateUtils.addMinutes(session.creationDate, sessionLife);
      if (sessionExpiration.after(currentTime)) {
       sessionsRepo.deleteByID(session.id);
      }
    }
  }
}