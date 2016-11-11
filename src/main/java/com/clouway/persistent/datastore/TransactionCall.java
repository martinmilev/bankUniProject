package com.clouway.persistent.datastore;

import java.sql.Connection;

/**
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
public interface TransactionCall {
  void execute(Connection connection);
}
