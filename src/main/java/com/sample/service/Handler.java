package com.sample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sample.conf.PrintInfo;
import com.sample.domain.DomainObject;

@Component
public class Handler {

  @Autowired
  private PrintInfo printInfo;

  public DomainObject getMessage(DomainObject payload) {
    payload.setValue("value: " + payload.getName());
    printInfo.print("Handler: " + payload.toString());
    return payload;
  }
}
