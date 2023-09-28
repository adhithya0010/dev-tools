package com.intellij.devtools.locale;

import com.intellij.DynamicBundle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class MessageBundle extends DynamicBundle {

  private static MessageBundle bundle = null;

  public MessageBundle() {
    super(getPathToBundle());
  }

  private static String getPathToBundle() {
//    if(Boolean.parseBoolean(System.getProperty("ide.test.execution"))) {
//      return "locale/test_messages";
//    } else {
//      return "locale/messages";
//    }
    return "locale/messages";
  }

  @Override
  protected ResourceBundle findBundle(@NotNull String pathToBundle, @NotNull ClassLoader baseLoader,
      @NotNull ResourceBundle.Control control) {
    ResourceBundle base = super.findBundle(pathToBundle, baseLoader, control);
    Locale ideLocale = DynamicBundle.getLocale();
    if (!ideLocale.equals(Locale.ENGLISH)) {
      // load your bundle from baseName_<language>.properties, e.g. "baseName_zh.properties"
      String localizedPath = pathToBundle + "_" + ideLocale.getLanguage();
      ResourceBundle localeBundle = super.findBundle(localizedPath, MessageBundle.class.getClassLoader(), control);
      if (localeBundle != null && !base.equals(localeBundle)) {
        setParent(localeBundle, base);
        return localeBundle;
      }
    }
    return base;
  }

  /**
   * Borrows code from {@code com.intellij.DynamicBundle} to set the parent bundle using reflection.
   */
  private static void setParent(ResourceBundle localeBundle, ResourceBundle base) {
    try {
      Method method = ResourceBundle.class.getDeclaredMethod("setParent", ResourceBundle.class);
      method.setAccessible(true);
      MethodHandles.lookup().unreflect(method).bindTo(localeBundle).invoke(base);
    } catch (Throwable e) {
      // ignored, better handle this in production code
      e.printStackTrace();
    }
  }

  public static String get(@PropertyKey(resourceBundle = "locale.messages") String key, Object... params) {
    return getBundle().messageOrDefault(key, key, params);
  }

  public static MessageBundle getBundle() {
    if(bundle == null) {
      bundle = new MessageBundle();
    }
    return bundle;
  }
}
