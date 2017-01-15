package com.clouway.core;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class Transfer {
  public final java.sql.Timestamp date;
  public final String from;
  public final String to;
  public final Double amount;

  public Transfer(java.sql.Timestamp date, String from, String to, Double amount) {
    this.date = date;
    this.from = from;
    this.to = to;
    this.amount = amount;
  }
}
