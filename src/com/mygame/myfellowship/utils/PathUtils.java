package com.mygame.myfellowship.utils;
import java.io.File;

import android.os.Environment;

public class PathUtils {
  public static String getSDcardDir() {
    return Environment.getExternalStorageDirectory().getPath() + "/";
  }

  public static String checkAndMkdirs(String dir) {
    File file = new File(dir);
    if (file.exists() == false) {
      file.mkdirs();
    }
    return dir;
  }

  public static String getAppPath() {
    String dir = getSDcardDir() + "leanchat/";
    return checkAndMkdirs(dir);
  }

  public static String getAvatarDir() {
    String dir = getAppPath() + "avatar/";
    return checkAndMkdirs(dir);
  }

  public static String getAvatarTmpPath() {
    return getAvatarDir() + "tmp";
  }

}