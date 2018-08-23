package io.sweers.blackmirror.spy;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Method;
import java.util.List;

public final class Spy {

  private final BaseDexClassLoader classLoader;

  public Spy(Context context) {
    String neighborLocation =
        location(context.getPackageManager(), "io.sweers.blackmirror.neighbor");
    classLoader = new PathClassLoader(neighborLocation, context.getClassLoader());
  }

  public String sayHello() {
    try {
      Class<?> clazz = classLoader.loadClass("io.sweers.blackmirror.neighbor.MainActivity");
      Method sayHelloMethod = clazz.getDeclaredMethod("sayHello");
      return (String) sayHelloMethod.invoke(null);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
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
}
