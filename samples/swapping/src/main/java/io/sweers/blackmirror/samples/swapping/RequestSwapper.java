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

import io.sweers.blackmirror.ClassResult;
import io.sweers.blackmirror.Interceptor;

/**
 * An interceptor that attempts to return a Button for every TextView by changing the request
 *
 * Doesn't work because ART checks this
 *
 * https://android.googlesource.com/platform/art/+/master/runtime/class_linker.cc#2567
 */
public final class RequestSwapper implements Interceptor {
  @Override public ClassResult interceptFind(FindChain chain)
      throws ClassNotFoundException {
    if (chain.request()
        .name()
        .equals("android.widget.TextView")) {
      return chain.proceedFind(chain.request()
          .toBuilder()
          .name("android.widget.Button")
          .build());
    }
    return chain.proceedFind(chain.request());
  }

  @Override public ClassResult interceptLoad(LoadChain chain, boolean resolve)
      throws ClassNotFoundException {
    if (chain.request()
        .name()
        .equals("android.widget.TextView")) {
      return chain.proceedLoad(chain.request()
          .toBuilder()
          .name("android.widget.Button")
          .build(), resolve);
    }
    return chain.proceedLoad(chain.request(), resolve);
  }
}
