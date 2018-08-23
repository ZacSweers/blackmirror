package io.sweers.blackmirror.spy;

import android.content.Context;
import dalvik.system.BaseDexClassLoader;
import java.lang.reflect.Method;

public final class Spy {

  private final BaseDexClassLoader classLoader;

  public Spy(Context context) {
    classLoader = Spies.classLoaderFor(context, "io.sweers.blackmirror.neighbor");
  }

  public String sayHello() {
    try {
      Class<?> clazz = classLoader.loadClass("io.sweers.blackmirror.neighbor.MainActivity");
      Method sayHelloMethod = clazz.getDeclaredMethod("sayHello");
      return (String) sayHelloMethod.invoke(null);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
