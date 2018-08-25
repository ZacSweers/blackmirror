package io.sweers.blackmirror.samples.resources;

import android.content.Context;
import io.sweers.blackmirror.ClassResult;
import io.sweers.blackmirror.Interceptor;
import io.sweers.blackmirror.sample.bytebufferutil.ByteBufferUtil;
import java.io.InputStream;
import java.nio.ByteBuffer;
import sweers.io.blackmirror.samples.resources.R;

public final class ResourcesInterceptor implements Interceptor {

  private final ClassLoader assetsClassLoader;

  public ResourcesInterceptor(Context context) {
    try {
      assetsClassLoader = loadClassLoader(context);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override public ClassResult intercept(Chain chain) throws ClassNotFoundException {
    if ("packaged.Packaged".equals(chain.request().name())) {
      return ClassResult.builder()
          .name("packaged.Packaged")
          .clazz(loadPackaged(assetsClassLoader))
          .build();
    } else {
      return chain.proceed(chain.request());
    }
  }

  public static ClassLoader loadClassLoader(Context context) {
    try {
      ByteBuffer[] buffers = new ByteBuffer[1];

      // Read the dex file into memory as a bytebuffer
      InputStream is = context.getResources().openRawResource(R.raw.providedpackaged);
      buffers[0] = ByteBufferUtil.loadBuffer(is);

      // Init the hidden constructor with the bytebuffer
      return ByteBufferUtil.createClassLoaderFromDexFiles(buffers);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Class<?> loadPackaged(Context context) throws ClassNotFoundException {
    ClassLoader cl = loadClassLoader(context);
    return loadPackaged(cl);
  }

  public static Class<?> loadPackaged(ClassLoader cl) throws ClassNotFoundException {
    return cl.loadClass("packaged.Packaged");
  }
}
