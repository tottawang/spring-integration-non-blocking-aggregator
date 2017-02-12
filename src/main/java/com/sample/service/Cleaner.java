package com.sample.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.sample.conf.PrintInfo;
import com.sample.domain.DomainObject;

@Component
public class Cleaner {

  @Autowired
  private PrintInfo printInfo;

  /**
   * Cleaner is to release resource when first message is completed.
   * 
   * @param message
   */
  public void cleanup(Message<?> message) {
    @SuppressWarnings("unchecked")
    ArrayList<DomainObject> objects = (ArrayList<DomainObject>) message.getPayload();
    printInfo.print("Cleaner: " + objects.size() + " messages to clean up " + objects);
  }

  /**
   * Clean up method to release resource by splitter when there is no messages available.
   */
  public void cleanup() {
    printInfo.print("Cleaner: 0 messages to clean up");
  }
}
