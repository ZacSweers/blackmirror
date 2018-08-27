package io.sweers.blackmirror.sample.helloimpl;

import io.sweers.blackmirror.sample.hello.Hello;

public class HelloImpl implements Hello {
  @Override public String sayHello() {
    return "Hello! This is loaded from _____.";
  }
}
