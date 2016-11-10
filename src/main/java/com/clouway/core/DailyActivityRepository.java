package com.clouway.core;

import java.util.Map;

/**
 * This {@code DailyActivityRepository} interface provides the methods
 * to be implemented for work with the transaction_history table in the Bank database
 *
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
public interface DailyActivityRepository {

  /**
   * Returns the number of distinct transactions for today
   *
   * @return Map of Integer values and String keys
   */
  Map<String, Integer> dailyActivity();
}
