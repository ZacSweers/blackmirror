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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

  private static BlackMirror INSTANCE;

  public static synchronized BlackMirror getInstance(Context context,
      List<? extends Interceptor> interceptors) {
    if (INSTANCE == null) {
      INSTANCE = new BlackMirror(location(context.getPackageManager(), context.getPackageName()),
          interceptors);
    }
    return INSTANCE;
  }

  public static void install(Context context) throws Exception {
    install(context, Collections.<Interceptor>emptyList());
  }

  public static void install(Context context, List<? extends Interceptor> interceptors)
      throws Exception {
    BlackMirror instance = getInstance(context, interceptors);

    // Application stuff still ends up going through here?
    Log.d("BlackMirror", "BlackMirror.install - Created hack");
    Class<?> systemLoader = Class.forName("java.lang.ClassLoader$SystemClassLoader");
    Log.d("BlackMirror", "BlackMirror.install - Found class");

    // TODO this next line no longer works on API 28
    Field field = systemLoader.getDeclaredField("loader");
    Log.d("BlackMirror", "BlackMirror.install - Acquired system classloader field");
    field.set(null, instance);
    Log.d("BlackMirror", "BlackMirror.install - setting system classLoader");
    Thread.currentThread()
        .setContextClassLoader(instance);
    Log.d("BlackMirror", "BlackMirror.install - set system classLoader successful");
    if (ClassLoader.getSystemClassLoader() != instance) {
      throw new RuntimeException("DARN");
    }

    // This is the real magic
    // This is a "ContextImpl"
    Log.d("BlackMirror", "BlackMirror.install - Reading ContextImpl");
    Context baseContext = ((ContextWrapper) context.getApplicationContext()).getBaseContext();
    Class<?> baseContextClazz = baseContext.getClass();

    try {
      Field baseCotextClassLoader = baseContextClazz.getDeclaredField("mClassLoader");
      baseCotextClassLoader.setAccessible(true);
      baseCotextClassLoader.set(context, instance);
    } catch (NoSuchFieldException e) {
      // DOES NOT WORK ON SAMSUNG 😬🔫
    }

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
    classLoaderField.set(packageInfoField.get(baseContext), instance);
    Log.d("BlackMirror", "BlackMirror.install - Success");
  }

  private static String location(PackageManager pm, String hostPackageName) {
    List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
    List<ApplicationInfo> appInfoList = pm.getInstalledApplications(0);
    for (int x = 0; x < packages.size(); x++) {
      PackageInfo pkginfo = packages.get(x);
      if (hostPackageName.equals(pkginfo.packageName)) {
        return appInfoList.get(x).sourceDir;
      }
    }
    throw new RuntimeException("DID NOT FIND OUR PACKAGE LOL");
  }

  private final List<Interceptor> interceptors;

  private BlackMirror(String sourceDir, List<? extends Interceptor> interceptors) {
    super(sourceDir, getSystemClassLoader());
    List<Interceptor> finalInterceptors = new ArrayList<>(interceptors);
    finalInterceptors.add(this);
    this.interceptors = unmodifiableList(finalInterceptors);
  }

  @Override protected Class<?> findClass(String name) throws ClassNotFoundException {
    //Log.d("BlackMirror", "BlackMirror.findClass - " + name);
    ClassRequest request = ClassRequest.builder()
        .name(name)
        .build();
    return new InterceptorChain(interceptors, 0, request).proceedFind(request)
        .clazz();
  }

  @Override protected Class<?> loadClass(String name, boolean resolve)
      throws ClassNotFoundException {
    //Log.d("BlackMirror", "BlackMirror.loadClass - " + name + " " + resolve);
    ClassRequest request = ClassRequest.builder()
        .name(name)
        .build();
    return new InterceptorChain(interceptors, 0, request).proceedLoad(request, false)
        .clazz();
  }

  @Override public ClassResult interceptFind(FindChain chain) throws ClassNotFoundException {
    return ClassResult.builder()
        .clazz(super.findClass(chain.request()
            .name()))
        .name(chain.request()
            .name())
        .build();
  }

  @Override public ClassResult interceptLoad(LoadChain chain, boolean resolve)
      throws ClassNotFoundException {
    return ClassResult.builder()
        .clazz(super.loadClass(chain.request()
            .name(), resolve))
        .name(chain.request()
            .name())
        .build();
  }
}
