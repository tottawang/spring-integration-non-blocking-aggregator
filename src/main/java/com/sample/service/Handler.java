package com.sample.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import com.sample.domain.DomainObject;

@Component
public class Handler {

  private static Logger log = LoggerFactory.getLogger(Handler.class);

  @Autowired
  @Qualifier("aggregatorChannel")
  private DirectChannel aggregatorChannel;

  public void getMessage(Message<?> message) {
    handleMessage(message);
  }

  public String handleMessage(Message<?> message) {
    DomainObject event = (DomainObject) message.getPayload();
    log.info("Handler: " + event.getName());
    aggregatorChannel.send(new GenericMessage<>(event, message.getHeaders()));
    return "";
  }

}
