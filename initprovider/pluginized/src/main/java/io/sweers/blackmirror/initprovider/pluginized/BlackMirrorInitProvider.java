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

package io.sweers.blackmirror.initprovider.pluginized;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import io.sweers.blackmirror.BlackMirror;
import io.sweers.blackmirror.Interceptor;
import java.util.List;

/**
 * Content provider, to auto-init in consuming apps before onCreate.
 *
 * <p>Based on https://medium.com/@andretietz/auto-initialize-your-android-library-2349daf06920
 */
public abstract class BlackMirrorInitProvider extends ContentProvider {

  public abstract List<? extends Interceptor> interceptors(Context context);

  public BlackMirrorInitProvider() {}

  @SuppressWarnings("ConstantConditions") @Override public boolean onCreate() {
    try {
      Log.d("BLAH", "InitProvider.static initializer - ");
      BlackMirror.install(getContext(), interceptors(getContext()));
      Log.d("BLAH", "InitProvider.static initializer - Success");
    } catch (Exception e) {
      e.printStackTrace();
    }

    return true;
  }

  @Override public void attachInfo(Context context, ProviderInfo providerInfo) {
    if (providerInfo == null) {
      throw new NullPointerException(BlackMirrorInitProvider.class.getSimpleName()
          + " ProviderInfo cannot be null.");
    }
    // So if the authorities equal the library internal ones, the developer forgot to set their
    // applicationId
    //if (InitProvider.class.getName().equals(providerInfo.authority)) {
    //  throw new IllegalStateException(
    //      "Incorrect provider authority in manifest. Most likely due to a "
    //          + "missing applicationId variable in application\'s build.gradle.");
    //}
    super.attachInfo(context, providerInfo);
  }

  @Nullable @Override public Cursor query(Uri uri,
      String[] projection,
      String selection,
      String[] selectionArgs,
      String sortOrder) {
    return null;
  }

  @Nullable @Override public String getType(Uri uri) {
    return null;
  }

  @Nullable @Override public Uri insert(Uri uri, ContentValues values) {
    return null;
  }

  @Override public int delete(Uri uri, String selection, String[] selectionArgs) {
    return 0;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    return 0;
  }
}
