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

package io.sweers.blackmirror.app;

import io.sweers.blackmirror.Interceptor;
import io.sweers.blackmirror.initprovider.pluginized.InitProvider;
import io.sweers.blackmirror.samples.logging.LoggingInterceptor;
import java.util.List;

import static java.util.Arrays.asList;

public class SampleInitProvider extends InitProvider {

  public SampleInitProvider() {
  }

  @Override public List<? extends Interceptor> interceptors() {
    return asList(
        new LoggingInterceptor()
        //new TimingInterceptor(),
        //new RequestSwapper(),
        //new ResultSwapper(),
        //new ResultSwapperWithProxy()
        // Add others here
    );
  }
}
