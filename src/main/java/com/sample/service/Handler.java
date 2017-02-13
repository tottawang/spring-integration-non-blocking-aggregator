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

import rx.Observable;
import rx.Observer;

@Component
public class Handler {

  private static Logger log = LoggerFactory.getLogger(Cleaner.class);

  @Autowired
  private HystrixService service;

  @Autowired
  @Qualifier("aggregatorChannel")
  private DirectChannel aggregatorChannel;

  public void getMessage(Message<?> message) {
    handleMessage(message);
  }

  public String handleMessage(Message<?> message) {
    DomainObject event = (DomainObject) message.getPayload();
    log.info("Handler: " + event.toString());
    long start = System.currentTimeMillis();
    Observable<String> observableResult = service.getContentNonBlocking();
    observableResult.subscribe(new Observer<String>() {
      @Override
      public void onCompleted() {
        log.info("Handler: completed");
        aggregatorChannel.send(new GenericMessage<>(event, message.getHeaders()));
      }

      @Override
      public void onError(Throwable e) {
        log.info("Handler error: " + e.getMessage());
      }

      @Override
      public void onNext(String t) {

      }
    });
    long end = System.currentTimeMillis();
    log.info("Time taken to get results " + (end - start) + " milliseconds");
    return observableResult.toString();
  }
}
