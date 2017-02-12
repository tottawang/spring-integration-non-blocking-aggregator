package com.sample.conf;

import java.util.concurrent.Executor;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.util.CallerBlocksPolicy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.sample.domain.DomainObject;
import com.sample.service.Aggregator;
import com.sample.service.Cleaner;
import com.sample.service.Handler;
import com.sample.service.Splitter;
import com.sample.service.Transformer;

@Configuration
public class ApplicationConfig extends ResourceConfig {

  @Autowired
  private Transformer transformer;

  @Autowired
  private Splitter splitter;

  @Autowired
  private Handler handler;

  @Autowired
  private Cleaner cleaner;

  @Autowired
  private Aggregator aggregator;

  public ApplicationConfig() {
    packages("com.sample.resources");
  }

  @Bean(name = "primaryWorkers")
  public Executor executors() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    int poolSize = 10;
    executor.setCorePoolSize(8);
    executor.setMaxPoolSize(poolSize);
    executor.setQueueCapacity(256);
    executor.setThreadNamePrefix("PrimaryWorkerThread-");
    executor.setRejectedExecutionHandler(new CallerBlocksPolicy(5000));
    executor.initialize();
    return executor;
  }

  @Bean
  @Qualifier("primaryWorkerChannel")
  public ExecutorChannel workerChannel() {
    return MessageChannels.executor(executors()).get();
  }

  @Bean
  public IntegrationFlow primaryFlow() {
    return IntegrationFlows.from(workerChannel()).transform(transformer).split(splitter, null)
        .handle((m, h) -> {
          // http://stackoverflow.com/questions/34929476/spring-dsl-sending-error-message-to-jms-queue-get-an-error-one-way-messageha
          handler.getMessage((DomainObject) m);
          return m;
        }).handle(m -> cleaner.cleanup(m)).get();
  }


}
