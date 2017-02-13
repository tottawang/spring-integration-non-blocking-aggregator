package com.sample.service;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.sample.domain.DomainObject;

@Component
public class Cleaner {

  private static Logger log = LoggerFactory.getLogger(Cleaner.class);


  /**
   * Cleaner is to release resource when first message is completed.
   * 
   * @param message
   */
  public void cleanup(Message<?> message) {
    @SuppressWarnings("unchecked")
    ArrayList<DomainObject> objects = (ArrayList<DomainObject>) message.getPayload();
    log.info("Cleaner: " + objects.size() + " messages to clean up");
  }

  /**
   * Clean up method to release resource by splitter when there is no messages available.
   */
  public void cleanup() {
    log.info("Cleaner: 0 messages to clean up");
  }
}
