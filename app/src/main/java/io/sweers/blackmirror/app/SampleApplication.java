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

import android.app.Application;
import io.sweers.blackmirror.BlackMirror;
import java.lang.reflect.Method;
import timber.log.Timber;

public class SampleApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    try {
      Timber.plant(new Timber.DebugTree());
      // Example of deferred classloading in application.
      // Can use BlackMirror.getInstance() directly or getClassLoader().
      // You're fine after this though
      Class<?> clazz = BlackMirror.getInstance().loadClass("io.sweers.blackmirror.app.AppDelegate");
      Method method = clazz.getDeclaredMethod("onCreate");
      method.setAccessible(true);
      method.invoke(null);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
