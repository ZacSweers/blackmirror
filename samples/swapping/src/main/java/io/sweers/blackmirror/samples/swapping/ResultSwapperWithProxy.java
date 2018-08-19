/*
 * Copyright (c) 2018. Zac Sweers
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sweers.blackmirror.samples.swapping;

import android.support.annotation.Nullable;
import io.sweers.blackmirror.ClassResult;
import io.sweers.blackmirror.Interceptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * An interceptor that attempts to return a proxy for every list.
 *
 * Doesn't work because the runtime checks this too: "$Proxy0 cannot be cast to java.lang.Class"
 *
 * Not sure where though
 */
public final class ResultSwapperWithProxy implements Interceptor {
  @Override public ClassResult interceptFind(FindChain chain) throws ClassNotFoundException {
    if (chain.request()
        .name()
        .equals("java.util.List")) {
      ClassResult result = chain.proceedFind(chain.request());
      return result.toBuilder()
          .clazz(fakeClass(result.clazz()))
          .build();
    }
    return chain.proceedFind(chain.request());
  }

  @Override public ClassResult interceptLoad(LoadChain chain, boolean resolve)
      throws ClassNotFoundException {
    if (chain.request()
        .name()
        .equals("java.util.List")) {
      ClassResult result = chain.proceedLoad(chain.request(), resolve);
      return result.toBuilder()
          .clazz(fakeClass(result.clazz()))
          .build();
    }
    return chain.proceedLoad(chain.request(), resolve);
  }

  private static Class<?> fakeClass(Class<?> targetClass) {
    return (Class<?>) Proxy.newProxyInstance(targetClass.getClassLoader(),
        new Class<?>[] { targetClass },
        new InvocationHandler() {
          @Override public Object invoke(Object proxy, Method method, @Nullable Object[] args)
              throws Throwable {
            return method.invoke(this, args);
          }
        });
  }
}
