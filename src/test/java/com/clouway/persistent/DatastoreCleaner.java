package com.clouway.persistent;

import com.clouway.persistent.adapter.jdbc.ConnectionProvider;
import com.clouway.persistent.datastore.DataStore;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class DatastoreCleaner {
  private final ConnectionProvider provider = new ConnectionProvider();
  private final DataStore dataStore = new DataStore(provider);
  private final String[] table;

  public DatastoreCleaner(String... table) {
    this.table = table;
  }

  public void perform() {
    for (String each : table) {
      dataStore.update("truncate " + each);
    }
  }
}
