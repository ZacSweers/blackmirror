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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.util.Log;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * https://www.youtube.com/watch?v=kIvEu-XjZ40
 */
public final class BlackMirror extends PathClassLoader implements Interceptor {

  private static final boolean IS_28 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
  private static final boolean IS_27 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1;
  private static BlackMirror instance;

  private static synchronized void init(ClassLoader delegate, Context context,
      List<? extends Interceptor> interceptors) {
    instance = new BlackMirror(delegate, context.getApplicationInfo().sourceDir, interceptors);
  }

  public static synchronized boolean isInstalled() {
    return instance != null;
  }

  public static synchronized BlackMirror getInstance() {
    if (instance == null) {
      try {
        return ((BlackMirror) Thread.currentThread().getContextClassLoader());
      } catch (Throwable t) {
        throw t;
      }
    }
    return instance;
  }

  public static void install(Context context) throws Exception {
    install(context, Collections.<Interceptor>emptyList());
  }

  public static void install(Context context, List<? extends Interceptor> interceptors)
      throws Exception {
    // This is the real magic
    // This is a "ContextImpl"
    Log.d("BlackMirror", "BlackMirror.install - Reading ContextImpl");
    Context baseContext = ((ContextWrapper) context.getApplicationContext()).getBaseContext();
    Class<?> baseContextClazz = baseContext.getClass();

    // LoadedApk is what holds the classloader that all android bits route through
    Log.d("BlackMirror", "BlackMirror.install - Reading context packageInfo");
    Field packageInfoField = baseContextClazz.getDeclaredField("mPackageInfo");
    packageInfoField.setAccessible(true);
    Log.d("BlackMirror", "BlackMirror.install - Reading LoadedApk");
    @SuppressLint("PrivateApi") Class<?> loadedApkClazz = Class.forName("android.app.LoadedApk");
    Log.d("BlackMirror", "BlackMirror.install - Reading LoadedApk.mClassLoader");
    Field classLoaderField = loadedApkClazz.getDeclaredField("mClassLoader");
    Log.d("BlackMirror", "BlackMirror.install - Setting LoadedApk.mClassLoader");
    classLoaderField.setAccessible(true);

    ClassLoader delegate = (ClassLoader) classLoaderField.get(packageInfoField.get(baseContext));
    init(delegate, context, interceptors);

    classLoaderField.set(packageInfoField.get(baseContext), instance);
    Log.d("BlackMirror", "BlackMirror.install - Success");

    try {
      Field baseContextClassLoader = baseContextClazz.getDeclaredField("mClassLoader");
      baseContextClassLoader.setAccessible(true);
      if (IS_27) {
        baseContextClassLoader.set(baseContext, instance);
      } else {
        baseContextClassLoader.set(context, instance);
      }
    } catch (NoSuchFieldException e) {
      // DOES NOT WORK ON SAMSUNG 😬
    }

    // Application stuff still ends up going through here?
    Class<?> systemLoader = Class.forName("java.lang.ClassLoader$SystemClassLoader");
    Log.d("BlackMirror", "BlackMirror.install - Found class");

    if (!IS_28) {
      Field field = systemLoader.getDeclaredField("loader");
      Log.d("BlackMirror", "BlackMirror.install - Acquired system classloader field");
      field.set(null, instance);
    }
    Log.d("BlackMirror", "BlackMirror.install - setting system classLoader");
    if (ClassLoader.getSystemClassLoader() != instance) {
      // TODO This doesn't work anymore on API 28
      Log.d("BlackMirror", "BlackMirror.install - set system classLoader failed");
    } else {
      Log.d("BlackMirror", "BlackMirror.install - set system classLoader successful");
    }
  }

  private final ClassLoader delegate;
  private final List<Interceptor> interceptors;

  private BlackMirror(ClassLoader delegate, String sourceDir,
      List<? extends Interceptor> interceptors) {
    super(sourceDir, null);
    this.delegate = delegate;
    List<Interceptor> finalInterceptors = new ArrayList<>(interceptors);
    finalInterceptors.add(this);
    this.interceptors = unmodifiableList(finalInterceptors);
  }

  @Override protected Class<?> loadClass(String name, boolean resolve)
      throws ClassNotFoundException {
    //Log.d("BlackMirror", "BlackMirror.loadClass - " + name + " " + resolve);
    ClassRequest request = ClassRequest.builder().name(name).build();
    return new InterceptorChain(interceptors, 0, request).proceed(request).clazz();
  }

  @Override public ClassResult intercept(Chain chain) throws ClassNotFoundException {
    return ClassResult.builder()
        .clazz(delegate.loadClass(chain.request().name()))
        .name(chain.request().name())
        .build();
  }
}
