package com.sample.service;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.sample.conf.Application;
import com.sample.conf.HttpWebClient;
import com.sample.domain.DomainObject;

import rx.Observable;
import rx.Observer;

@Component
public class Handler {

  private static Logger log = LoggerFactory.getLogger(Handler.class);

  private static final com.netflix.hystrix.HystrixCommand.Setter cachedSetter =
      com.netflix.hystrix.HystrixCommand.Setter
          .withGroupKey(HystrixCommandGroupKey.Factory.asKey(HttpWebClient.GROUP))
          .andCommandKey(HystrixCommandKey.Factory.asKey(HttpWebClient.COMMAND_NON_BLOCKING_GET))
          .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(HttpWebClient.THREAD_POOL_KEY));

  @Value("${HYSTRIX_TEST_URL:url_not_accessiable}")
  private String HYSTRIX_TEST_URL;

  @Autowired
  @Qualifier(Application.DEFAULT_REST_TEMPLATE)
  protected RestTemplate restTemplate;

  @Autowired
  @Qualifier("aggregatorChannel")
  private DirectChannel aggregatorChannel;

  public void getMessage(Message<?> message) {
    handleMessage(message);
  }

  public String handleMessage(Message<?> message) {
    DomainObject event = (DomainObject) message.getPayload();
    log.info("Handler: " + event.getName());
    long start = System.currentTimeMillis();
    aggregatorChannel.send(new GenericMessage<>(event, message.getHeaders()));
    long end = System.currentTimeMillis();
    log.info(String.format("Time taken to get %s results is %s milliseconds", event.getName(),
        (end - start)));
    return "";
  }

  private void wrapPublish(DomainObject event, MessageHeaders messageHeaders) {

    Observable<String> pm = new PublishCommand("").observe();
    log.info("Observer created " + Thread.currentThread().getName());

    // non-blocking
    pm.subscribe(new Observer<String>() {

      @Override
      public void onCompleted() {
        log.info("Complete " + Thread.currentThread().getName());
        aggregatorChannel.send(new GenericMessage<>(event, messageHeaders));
      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
      }

      @Override
      public void onNext(String v) {
        log.info("onNEXT " + Thread.currentThread().getName());
      }

    });
  }

  class PublishCommand extends com.netflix.hystrix.HystrixCommand<String> {

    private final String name;

    public PublishCommand(String name) {
      super(cachedSetter);
      this.name = name;
    }

    @Override
    protected String run() {
      log.info("non blocking started");
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
      HttpEntity<String> entity = new HttpEntity<String>(headers);
      try {
        URI endpointUrl = new URI(HYSTRIX_TEST_URL);
        ResponseEntity<String> responseEntity =
            restTemplate.exchange(endpointUrl, HttpMethod.GET, entity, String.class);
        log.info(responseEntity.getBody());
        return responseEntity.getBody();
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }


}
