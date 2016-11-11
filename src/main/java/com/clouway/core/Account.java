package com.clouway.core;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class Account {
  public final String name;
  public final String password;
  public final Double amount;

  public Account(String name, String password, Double amount) {
    this.name = name;
    this.password = password;
    this.amount = amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Account account = (Account) o;

    if (name != null ? !name.equals(account.name) : account.name != null) return false;
    if (password != null ? !password.equals(account.password) : account.password != null) return false;
    return amount != null ? amount.equals(account.amount) : account.amount == null;
  }
}