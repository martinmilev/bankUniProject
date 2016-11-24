package com.clouway.core;

import com.clouway.http.servlets.HtmlServletPageRenderer;
import com.clouway.persistent.adapter.jdbc.ConnectionProvider;
import com.clouway.persistent.adapter.jdbc.PersistentAccountRepository;
import com.clouway.persistent.adapter.jdbc.PersistentDailyActivityRepository;
import com.clouway.persistent.adapter.jdbc.PersistentHistoryRepository;
import com.clouway.persistent.adapter.jdbc.PersistentSessionRepository;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class CoreModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(MyClock.class).to(MyServerClock.class);
    bind(SessionsRepository.class).to(PersistentSessionRepository.class);
    bind(AccountRepository.class).to(PersistentAccountRepository.class);
    bind(HistoryRepository.class).to(PersistentHistoryRepository.class);
    bind(DailyActivityRepository.class).to(PersistentDailyActivityRepository.class);
    bind(ServletPageRenderer.class).to(HtmlServletPageRenderer.class);

    bind(Key.get(Provider.class, Names.named("CP"))).to(ConnectionProvider.class);
    bind(Key.get(Provider.class, Names.named("UUID"))).to(UuidProvider.class);
  }
}
