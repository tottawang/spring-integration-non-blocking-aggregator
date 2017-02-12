package com.sample.conf;

import org.springframework.stereotype.Component;

@Component
public class PrintInfo {

  public void print(String info) {
    System.out.println(Thread.currentThread().getName() + ": " + info);
  }
}
