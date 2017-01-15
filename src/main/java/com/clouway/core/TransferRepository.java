package com.clouway.core;

import java.util.List;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public interface TransferRepository {
  void reg(Transfer transfer);

  List<Transfer> getHistory(String username, int offset, int limit);
}
