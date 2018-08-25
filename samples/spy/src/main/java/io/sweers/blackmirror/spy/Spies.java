package io.sweers.blackmirror.spy;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Spies {

  public static PathClassLoader classLoaderFor(Context context, String packageName) {
    String neighborLocation = Spies.location(context.getPackageManager(), packageName);
    return new PathClassLoader(neighborLocation, context.getClassLoader());
  }

  public static String location(PackageManager pm, String hostPackageName) {
    List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
    List<ApplicationInfo> appInfoList = pm.getInstalledApplications(0);
    for (int x = 0; x < packages.size(); x++) {
      PackageInfo pkginfo = packages.get(x);
      if (hostPackageName.equals(pkginfo.packageName)) {
        return appInfoList.get(x).sourceDir;
      }
    }
    throw new RuntimeException("Package not found");
  }

  public static DexFile[] dexFiles(BaseDexClassLoader classLoader) {
    try {
      Field pathListField = BaseDexClassLoader.class.getDeclaredField("pathList");
      pathListField.setAccessible(true);
      Object pathList = pathListField.get(classLoader);
      Class<?> dexPathListClass = Class.forName("dalvik.system.DexPathList");
      Field dexElementsField = dexPathListClass.getDeclaredField("dexElements");
      dexElementsField.setAccessible(true);
      Object[] elements = (Object[]) dexElementsField.get(pathList);
      Class<?> dexPathListElementClass = Class.forName("dalvik.system.DexPathList$Element");
      Field dexFileField = dexPathListElementClass.getDeclaredField("dexFile");
      dexFileField.setAccessible(true);
      DexFile[] dexFiles = new DexFile[elements.length];
      for (int i = 0; i < elements.length; i++) {
        dexFiles[i] = ((DexFile) dexFileField.get(elements[i]));
      }
      return dexFiles;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String guessBuildConfig(Context context, String packageName) {
    PathClassLoader classLoader = classLoaderFor(context, packageName);
    try {
      try {
        String defaultName = packageName + ".BuildConfig";
        classLoader.loadClass(defaultName);
        return defaultName;
      } catch (Throwable ignored) {
        // Continue on below
      }
      DexFile[] dexFiles = dexFiles(classLoader);
      for (DexFile dex : dexFiles) {
        Enumeration<String> entries = dex.entries();
        while (entries.hasMoreElements()) {
          String entry = entries.nextElement();

          if (entry.startsWith("android.") || entry.startsWith("java.")) {
            continue;
          }

          //Class<?> entryClass = dex.loadClass(entry, classLoader); // Not the safest because sometimes throws access issues
          Class<?> clazz = classLoader.loadClass(entry);
          if (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            boolean buildTypeFieldFound = false;
            boolean applicationIdFieldFound = false;
            for (Field field : fields) {
              field.setAccessible(true);
              if (Modifier.isStatic(field.getModifiers())) {
                String fieldValue = String.valueOf(field.get(null));
                if (packageName.equals(fieldValue)) {
                  applicationIdFieldFound = true;
                }
                if ("release".equals(fieldValue) || "debug".equals(fieldValue)) {
                  buildTypeFieldFound = true;
                }
                if (buildTypeFieldFound && applicationIdFieldFound) {
                  return entry;
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      return null;
    }
    return null;
  }

  public static Map<String, String> buildConfigFields(Context context, String packageName) {
    return buildConfigFields(context, packageName, null);
  }

  public static Map<String, String> buildConfigFields(Context context, String packageName,
      @Nullable String className) {
    className = className == null ? packageName + ".BuildConfig" : className;
    PathClassLoader classLoader = classLoaderFor(context, packageName);
    try {
      Class<?> clazz = classLoader.loadClass(className);
      Field[] fields = clazz.getDeclaredFields();
      Map<String, String> values = new LinkedHashMap<>();
      for (Field field : fields) {
        if (Modifier.isStatic(field.getModifiers())) {
          values.put(field.getName(), String.valueOf(field.get(null)));
        }
      }
      return values;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
