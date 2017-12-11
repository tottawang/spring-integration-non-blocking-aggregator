package com.sample.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.sample.domain.DomainObject;

@Component
public class Aggregator implements ReleaseStrategy {

  private static Logger log = LoggerFactory.getLogger(Aggregator.class);

  @Override
  public boolean canRelease(MessageGroup group) {

    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    Message<?> message = group.getOne();
    DomainObject event = (DomainObject) message.getPayload();
    boolean result = group.getMessages().size() == event.getCount();
    if (result) {
      log.info(String.format("Aggregator: done!! %s completed %s", event.toString(),
          group.getMessages().size()));
    } else {
      log.info("Aggregator: not done yet!!");
    }
    return result;
  }
}
