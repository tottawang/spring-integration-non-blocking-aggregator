package com.sample.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import com.sample.domain.DomainObject;
import com.sample.domain.DomainObjectKey;

@Component
public class Handler {

  private static Logger log = LoggerFactory.getLogger(Handler.class);

  @Autowired
  @Qualifier("aggregatorChannel")
  private ExecutorChannel aggregatorChannel;

  public void getMessage(Message<?> message) {
    handleMessage(message);
  }

  public String handleMessage(Message<?> message) {
    DomainObject event = (DomainObject) message.getPayload();
    log.info("Handler: " + event.getName());
    DomainObjectKey key = event.getkey();
    event.setValue(null);
    event = null;
    aggregatorChannel.send(new GenericMessage<>(key, message.getHeaders()));
    // memory issue that we should not send full payload to aggregator
    // aggregatorChannel.send(new GenericMessage<>(event, message.getHeaders()));
    return "";
  }

}
