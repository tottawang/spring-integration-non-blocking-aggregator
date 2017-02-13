package com.sample.service;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.command.ObservableResult;
import com.sample.conf.Application;
import com.sample.conf.HttpWebClient;

import rx.Observable;

@Component
public class HystrixService {

  @Value("${HYSTRIX_TEST_URL:url_not_accessiable}")
  private String HYSTRIX_TEST_URL;

  @Autowired
  @Qualifier(Application.DEFAULT_REST_TEMPLATE)
  protected RestTemplate restTemplate;

  @HystrixCommand(groupKey = HttpWebClient.GROUP,
      commandKey = HttpWebClient.COMMAND_NON_BLOCKING_GET,
      threadPoolKey = HttpWebClient.THREAD_POOL_KEY)
  public Observable<String> getContentNonBlocking() {
    return new ObservableResult<String>() {
      @Override
      public String invoke() {
        System.out.println(Thread.currentThread().getName() + ": " + " non blocking started");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
          URI endpointUrl = new URI(HYSTRIX_TEST_URL);
          ResponseEntity<String> responseEntity =
              restTemplate.exchange(endpointUrl, HttpMethod.GET, entity, String.class);
          return responseEntity.getBody();
        } catch (Throwable ex) {
          throw new RuntimeException(ex);
        }
      }
    };
  }
}
