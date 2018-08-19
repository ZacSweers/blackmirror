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

package io.sweers.blackmirror.samples.logging;

import io.sweers.blackmirror.ClassResult;
import io.sweers.blackmirror.Interceptor;
import timber.log.Timber;

public final class LoggingInterceptor implements Interceptor {

  @Override public ClassResult interceptFind(FindChain chain) throws ClassNotFoundException {
    Timber.tag("BlackMirror")
        .d(
            "Finding \"%s\"",
            chain.request()
                .name());
    return chain.proceedFind(chain.request());
  }

  @Override public ClassResult interceptLoad(LoadChain chain, boolean resolve)
      throws ClassNotFoundException {
    Timber.tag("BlackMirror")
        .d(
            "Loading \"%s\", resolve = %S",
            chain.request()
                .name(),
            resolve);
    return chain.proceedLoad(chain.request(), resolve);
  }
}
