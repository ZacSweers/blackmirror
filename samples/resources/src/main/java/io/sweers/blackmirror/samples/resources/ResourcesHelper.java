package io.sweers.blackmirror.samples.resources;

import android.content.Context;
import io.sweers.blackmirror.ByteBufferUtil;
import java.io.InputStream;
import java.nio.ByteBuffer;
import sweers.io.blackmirror.samples.resources.R;

public final class ResourcesHelper {

  public static ClassLoader loadClassLoader(Context context) {
    try {
      ByteBuffer[] buffers = new ByteBuffer[1];

      // Read the dex file into memory as a bytebuffer
      InputStream is = context.getResources().openRawResource(R.raw.helloimpl);
      buffers[0] = ByteBufferUtil.loadBuffer(is);

      return ByteBufferUtil.createClassLoaderFromDexFiles(context.getClassLoader(), buffers);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Class<?> loadHelloImpl(Context context) throws ClassNotFoundException {
    ClassLoader cl = loadClassLoader(context);
    return loadHelloImpl(cl);
  }

  public static Class<?> loadHelloImpl(ClassLoader cl) throws ClassNotFoundException {
    return cl.loadClass("io.sweers.blackmirror.sample.helloimpl.HelloImpl");
  }
}
