package com.clouway.core;

import java.util.List;
import java.util.Optional;

/**
 * Repository for sessions.
 *
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public interface SessionsRepository {
  /**
   * Saves new session to DB
   *
   * @param session
   */
  void save(Session session);

  /**
   * Finds session in the DB by userID.
   *
   * @param uid id for the user who owns the new session
   * @return Session
   */
  Optional<Session> findBySID(String uid);

  /**
   * Counts the current active sessions saved in the DB.
   *
   * @return the count of the sessions in Integer
   */
  Integer countSessions();

  /**
<<<<<<< fa1b7278e8609d2323d9e5d5d644da0f60f6aa6b
   * Get all the sessions from the DB.
   *
   * @return List of the Sessions
   */
  List<Session> getAll();

  /**
   * Deleting session from the database by sessions id.
   *
   * @param id
   */
  void deleteByID(String id);
}