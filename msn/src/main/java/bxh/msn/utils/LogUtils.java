package bxh.msn.utils;

import android.util.Log;
import bxh.msn.BuildConfig;

public class LogUtils {
    public final static boolean LOGGABLE = BuildConfig.DEBUG;

    public static void e(String tag, String content) {
        Log.e(tag, "" + content);
    }

    public static void i(String tag, String content) {
        Log.i(tag, "" + content);
    }
}
