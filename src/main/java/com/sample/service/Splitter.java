package com.sample.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.sample.domain.DomainObject;

@Component
public class Splitter extends AbstractMessageSplitter {

  private static Logger log = LoggerFactory.getLogger(Splitter.class);

  @Autowired
  private Cleaner cleaner;

  @SuppressWarnings("unchecked")
  @Override
  protected Object splitMessage(Message<?> message) {
    log.info("Splitter: headers: " + message.getHeaders());
    List<DomainObject> list = (List<DomainObject>) message.getPayload();
    if (list.isEmpty()) {
      cleaner.cleanup();
    } else {
      log.info(String.format("Splitter: set count on message: ", list.size()));
      list.forEach(m -> m.setCount(list.size()));
    }
    return list;
  }
}
