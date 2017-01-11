package com.wxk.nestedscrollingtest;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppLog {
  private static final boolean SHOW_LOG = true;
  private static final int V = 1;
  private static final int D = 2;
  private static final int I = 3;
  private static final int W = 4;
  private static final int E = 5;
  private static final int DEFAULT_METHOD_COUNT = 2;  //默认显示的方法数
  private static final String DEFAULT_TAG = "ntespm_log";  //默认的tag

  private static final int LOG_MAX_SIZE = 4000;  //Android 最大的log大小为4k
  private static final int JSON_INDENT = 2;  //json每行的缩进
  private static final char TOP_LEFT_CORNER = '╔';
  private static final char BOTTOM_LEFT_CORNER = '╚';
  private static final char MIDDLE_CORNER = '╟';
  private static final char HORIZONTAL_DOUBLE_LINE = '║';
  private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════════════════════════════";
  private static final String SINGLE_DIVIDER = "────────────────────────────────────────────────────────────────────";
  private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
  private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
  private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;


  private AppLog() {}

  public static void v(String msg) {
    customLog(V, null, msg, null);
  }

  public static void v(String tag, String msg) {
    customLog(V, tag, msg, null);
  }

  public static void v(String tag, String msg, Throwable t) {
    customLog(V, tag, msg, t);
  }

  public static void d(String msg) {
    customLog(D, null, msg, null);
  }

  public static void d(String tag, String msg) {
    customLog(D, tag, msg, null);
  }

  public static void d(String tag, String msg, Throwable t) {
    customLog(D, tag, msg, t);
  }

  public static void i(String msg) {
    customLog(I, null, msg, null);
  }

  public static void i(String tag, String msg) {
    customLog(I, tag, msg, null);
  }

  public static void i(String tag, String msg, Throwable t) {
    customLog(I, tag, msg, t);
  }

  public static void w(String msg) {
    customLog(W, null, msg, null);
  }

  public static void w(String tag, String msg) {
    customLog(W, tag, msg, null);
  }

  public static void w(String tag, String msg, Throwable t) {
    customLog(W, tag, msg, t);
  }

  public static void e(String msg) {
    customLog(E, null, msg, null);
  }

  public static void e(String tag, String msg) {
    customLog(E, tag, msg, null);
  }

  public static void e(String tag, String msg, Throwable t) {
    customLog(E, tag, msg, t);
  }

  private static void customLog(int type, String tagStr, Object obj, Throwable t) {
    if (!SHOW_LOG) {
      return;
    }
    String tag = (tagStr == null ? DEFAULT_TAG : tagStr);
    String msg = (obj == null ? "Log with null Object" : obj.toString());
    logTopBorder(type, tag);
    logMethod(type, tag);
    logDivider(type, tag);
    logContent(type, tag, msg);
    logBottomBorder(type, tag);
  }

  /**
   * 打印出顶部
   */
  private static void logTopBorder(int logType, String tag) {
    logMsg(logType, tag, TOP_BORDER);
  }

  /**
   * 打印出分割线
   */
  private static void logDivider(int logType, String tag) {
    logMsg(logType, tag, MIDDLE_BORDER);
  }

  /**
   * 打印出底部
   */
  private static void logBottomBorder(int logType, String tag) {
    logMsg(logType, tag, BOTTOM_BORDER);
  }

  /**
   * 打印出方法
   */
  private static void logMethod(int logType, String tag) {
    StackTraceElement[] trace = Thread.currentThread().getStackTrace();
    logMsg(logType, tag, HORIZONTAL_DOUBLE_LINE + " Thread: " + Thread.currentThread().getName());
    logDivider(logType, tag);

    String level = "";
    int stackOffset = 4;
    int methodCount = DEFAULT_METHOD_COUNT;

    for (int i = 1; i <= methodCount; i++) {
      int stackIndex = i + stackOffset;
      if (stackIndex >= trace.length) {
        continue;
      }
      StringBuilder builder = new StringBuilder();
      builder.append("║ ")
          .append(level)
          .append(getSimpleClassName(trace[stackIndex].getClassName()))
          .append(".")
          .append(trace[stackIndex].getMethodName())
          .append(" ")
          .append(" (")
          .append(trace[stackIndex].getFileName())
          .append(":")
          .append(trace[stackIndex].getLineNumber())
          .append(")");
      level += "   ";
      logMsg(logType, tag, builder.toString());
    }
  }

  /**
   * 处理要打印的内容
   */
  private static void logContent(int logType, String tag, String msg) {
    String message;

    if (isJson(msg)) {

      try {
        String tempMessage = "";
        msg = msg.trim();
        if (msg.startsWith("{")) {
          JSONObject jsonObject = new JSONObject(msg);
          tempMessage = jsonObject.toString(JSON_INDENT);
        }
        if (msg.startsWith("[")) {
          JSONArray jsonArray = new JSONArray(msg);
          tempMessage = jsonArray.toString(JSON_INDENT);
        }

        message = tempMessage.replace("\\/", "\\");
      } catch (JSONException e) {
        message = "Invalid Json";
      }
    } else {
      message = msg;
    }

    byte[] bytes = message.getBytes();
    int length = bytes.length;

    for (int i = 0; i < length; i += LOG_MAX_SIZE) {
      int count = Math.min(length - i, LOG_MAX_SIZE);
      logRealContent(logType, tag, new String(bytes, i, count));
    }
  }

  /**
   * 真正打印内容的地方
   */
  private static void logRealContent(int logType, String tag, String msg) {
    String[] lines = msg.split(System.getProperty("line.separator"));
    for (String line : lines) {
      logMsg(logType, tag, HORIZONTAL_DOUBLE_LINE + " " + line);
    }
  }

  private static String getSimpleClassName(String name) {
    int lastIndex = name.lastIndexOf(".");
    return name.substring(lastIndex + 1);
  }

  /**
   * 判断是否是json格式的字符串
   */
  private static boolean isJson(String str) {
    if (TextUtils.isEmpty(str)) {
        return false;
    }
    try {
      new JSONObject(str);
      return true;
      //yes, it is
    } catch(Exception e){
      return false;
    }
  }

  private static void logMsg(int logType, String tag, String msg) {
    switch (logType) {
      case V:
        Log.v(tag, msg);
        break;
      case D:
        Log.d(tag, msg);
        break;
      case I:
        Log.i(tag, msg);
        break;
      case W:
        Log.w(tag, msg);
        break;
      case E:
        Log.e(tag, msg);
        break;
      default:
        break;
    }
  }

}
