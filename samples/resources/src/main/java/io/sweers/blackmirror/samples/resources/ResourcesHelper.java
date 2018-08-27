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
      InputStream is = context.getResources().openRawResource(R.raw.providedpackaged);
      buffers[0] = ByteBufferUtil.loadBuffer(is);

      // Init the hidden constructor with the bytebuffer
      return ByteBufferUtil.createClassLoaderFromDexFiles(context.getClassLoader(), buffers);
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
