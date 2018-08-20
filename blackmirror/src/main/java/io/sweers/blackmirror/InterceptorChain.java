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

package io.sweers.blackmirror;

import java.util.List;

/**
 * A concrete interceptor chain that carries the entire interceptor chain.
 */
class InterceptorChain implements Interceptor.Chain {
  private final List<Interceptor> interceptors;
  private final int index;
  private final ClassRequest request;

  InterceptorChain(List<Interceptor> interceptors, int index, ClassRequest request) {
    this.interceptors = interceptors;
    this.index = index;
    this.request = request;
  }

  @Override public ClassRequest request() {
    return request;
  }

  @Override public ClassResult proceed(ClassRequest request) throws ClassNotFoundException {
    if (index >= interceptors.size()) {
      throw new AssertionError("no interceptors added to the chain");
    }

    // Call the next interceptor in the chain.
    InterceptorChain next = new InterceptorChain(interceptors, index + 1, request);
    Interceptor interceptor = interceptors.get(index);
    ClassResult result = interceptor.intercept(next);

    // Confirm that the intercepted response isn't null.
    if (result == null) {
      throw new NullPointerException("interceptor " + interceptor + " returned null");
    }

    return result;
  }
}
