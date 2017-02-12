package com.sample.service;

import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.store.MessageGroup;
import org.springframework.stereotype.Component;

@Component
public class Aggregator implements ReleaseStrategy {

  @Override
  public boolean canRelease(MessageGroup group) {
    return group.getMessages().size() == 1;
  }

}
