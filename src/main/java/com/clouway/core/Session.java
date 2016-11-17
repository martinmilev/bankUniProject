package com.clouway.core;

import java.util.Date;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class Session {
  public final String id;
  public final String username;
  public final Date creationDate;

  public Session(String id, String username, Date creationDate) {
    this.id = id;
    this.username = username;
    this.creationDate = creationDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Session session = (Session) o;

    if (username != null ? !username.equals(session.username) : session.username != null) return false;
    if (id != null ? !id.equals(session.id) : session.id != null) return false;
    return creationDate != null ? creationDate.equals(session.creationDate) : session.creationDate == null;
  }

  @Override
  public int hashCode() {
    int result = username != null ? username.hashCode() : 0;
    result = 31 * result + (id != null ? id.hashCode() : 0);
    result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
    return result;
  }
}