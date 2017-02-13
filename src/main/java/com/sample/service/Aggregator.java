package com.sample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.sample.conf.PrintInfo;
import com.sample.domain.DomainObject;

@Component
public class Aggregator implements ReleaseStrategy {

  @Autowired
  private PrintInfo printInfo;

  @Override
  public boolean canRelease(MessageGroup group) {
    Message<?> message = group.getOne();
    DomainObject event = (DomainObject) message.getPayload();
    boolean result = group.getMessages().size() == event.getCount();
    if (result) {
      printInfo.print(
          "Aggregator: done!! " + event.toString() + " finished " + group.getMessages().size());
    } else {
      printInfo.print("Aggregator: not done!!");
    }
    return result;
  }
}
