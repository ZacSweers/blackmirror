package io.sweers.blackmirror.sample.assets;

import android.content.Context;
import dalvik.system.BaseDexClassLoader;
import io.sweers.blackmirror.ClassResult;
import io.sweers.blackmirror.Interceptor;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;

public final class AssetsInterceptor implements Interceptor {

  private final BaseDexClassLoader assetsClassLoader;

  public AssetsInterceptor(Context context) {
    try {
      Constructor constructor = Class.forName("dalvik.system.BaseDexClassLoader")
          .getConstructor(ByteBuffer[].class, ClassLoader.class);
      constructor.setAccessible(true);
      ByteBuffer[] buffers = new ByteBuffer[1];

      InputStream is = context.getAssets().open("types.dex");
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int nRead;
      byte[] data = new byte[is.available()];
      while ((nRead = is.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
      }

      buffer.flush();
      byte[] byteArray = buffer.toByteArray();
      buffers[0] = ByteBuffer.wrap(byteArray);
      assetsClassLoader =
          (BaseDexClassLoader) constructor.newInstance(buffers, context.getClassLoader());
      //Constructor constructor1 = assetsClassLoader.loadClass("Types").getDeclaredConstructors()[0];
      //constructor1.setAccessible(true);
      //Object typesInstance = constructor1.newInstance();
      //Method getStringMethod = assetsClassLoader.loadClass("Types").getDeclaredMethod("returnsString");
      //getStringMethod.setAccessible(true);
      //System.out.println(getStringMethod.invoke(typesInstance));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override public ClassResult intercept(Chain chain) throws ClassNotFoundException {
    if ("Types".equals(chain.request().name())) {
      return ClassResult.builder()
          .name("Types")
          .clazz(assetsClassLoader.loadClass("Types"))
          .build();
    } else {
      return chain.proceed(chain.request());
    }
  }
}
