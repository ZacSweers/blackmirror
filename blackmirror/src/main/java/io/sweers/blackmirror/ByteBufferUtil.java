package io.sweers.blackmirror;

import dalvik.system.BaseDexClassLoader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

public final class ByteBufferUtil {

  /**
   * @return a {@link ByteBuffer} of a given {@code inputStream}
   */
  public static ByteBuffer loadBuffer(InputStream inputStream) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int nRead;
    byte[] data = new byte[inputStream.available()];
    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }

    buffer.flush();
    byte[] byteArray = buffer.toByteArray();
    return ByteBuffer.wrap(byteArray);
  }

  /**
   * @return a {@link BaseDexClassLoader} from a given set of dex files, represented as {@link ByteBuffer ByteBuffers}.
   */
  public static BaseDexClassLoader createClassLoaderFromDexFiles(ClassLoader cl, ByteBuffer... dexFiles)
      throws IllegalAccessException, InvocationTargetException, InstantiationException,
      ClassNotFoundException, NoSuchMethodException {
    Constructor constructor = Class.forName("dalvik.system.BaseDexClassLoader")
        .getConstructor(ByteBuffer[].class, ClassLoader.class);
    constructor.setAccessible(true);

    // Init the hidden constructor with the bytebuffer
    return (BaseDexClassLoader) constructor.newInstance(dexFiles, cl);
  }
}
