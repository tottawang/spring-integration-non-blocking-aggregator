package com.sample.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.sample.domain.DomainObject;

@Component
public class Splitter extends AbstractMessageSplitter {

  @Autowired
  private Cleaner cleaner;

  @SuppressWarnings("unchecked")
  @Override
  protected Object splitMessage(Message<?> message) {
    System.out.println("Splitter: header: " + message.getHeaders());
    List<DomainObject> list = (List<DomainObject>) message.getPayload();
    if (list.isEmpty()) {
      cleaner.cleanup();
    } else {
      list.forEach(m -> m.setCount(list.size()));
    }
    return list;
  }
}
