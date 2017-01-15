package com.clouway.core;

import java.util.List;
import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public interface AccountRepository {
  void register(Account account);

  void deposit(String name, Double balance);

  void withdraw(String name, Double balance);

  Optional<Account> getByName(String name);

  List<Account> getAll();
}

