package com.clouway.core;

import java.util.Date;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class MyServerClock implements MyClock {
  @Override
  public Date getDate() {
    return new Date();
  }
}
