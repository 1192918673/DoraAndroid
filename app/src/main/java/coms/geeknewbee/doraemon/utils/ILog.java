package coms.geeknewbee.doraemon.utils;

import android.util.Log;

public class ILog {
	public final static String TAG = "ILog";

	private static final String LOG_FORMAT = "%1$s\n%2$s";

	public final static int LOG_VERBOSE = Log.VERBOSE;
	public final static int LOG_DEBUG = Log.DEBUG;
	public final static int LOG_INFO = Log.INFO;
	public final static int LOG_WARN = Log.WARN;
	public final static int LOG_ERROR = Log.ERROR;
	public final static int LOG_ASSERT = Log.ASSERT;
	public final static int LOG_NONE = Log.ASSERT + 1;

	public final static int LOG_DEFAULT = LOG_VERBOSE;

	/**
	 * 配置输出等级
	 */
	private static int mLogPriority = LOG_DEFAULT;

	private final static int LOG_MAX_LEN = 2048; // 2048：2k，16384：16k，4194304：4MB，

	/**
	 * 是否测试阶段，正式版则屏蔽所有日志
	 */
	private static boolean debug = true;

	public static void setLogPriority(int priority) {
		mLogPriority = priority;
	}

	public static int getLogPriority() {
		return mLogPriority;
	}

	public static void v(String tag, String msg) {
		log(Log.VERBOSE, null, tag, msg);
	}

	public static void d(String tag, String msg) {
		log(LOG_DEBUG, null, tag, msg);
	}

	public static void i(String tag, String msg) {
		log(LOG_INFO, null, tag, msg);
	}

	public static void w(String tag, String msg) {
		log(LOG_WARN, null, tag, msg);
	}

	public static void e(String tag, String msg) {
		log(LOG_ERROR, null, tag, msg);
	}

	public static void e(Throwable ex) {
		log(LOG_ERROR, ex, null, null);
	}

	public static void e(String tag, Throwable ex) {
		log(LOG_ERROR, ex, tag, null);
	}

	public static void e(String tag, String msg, Throwable ex) {
		log(LOG_ERROR, ex, tag, msg);
	}

	public static void v(String msg) {
		log(Log.VERBOSE, null, getCallerName(), msg);
	}

	public static void d(String msg) {
		log(LOG_DEBUG, null, getCallerName(), msg);
	}

	public static void i(String msg) {
		log(LOG_INFO, null, getCallerName(), msg);
	}

	public static void w(String msg) {
		log(LOG_WARN, null, getCallerName(), msg);
	}

	public static void e(String msg) {
		log(LOG_ERROR, null, getCallerName(), msg);
	}

	/**
	 * 获取调用者的类名
	 * @return
	 */
	public static String getCallerName() {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
		String className = caller.getClassName();// 带有包名信息
		className = className.substring(className.lastIndexOf(".") + 1);

		return className;
	}

	private static void log(int priority, Throwable ex, String tag,
			String message) {
		if (debug) {
			if (priority < mLogPriority) {
				return;
			}

			if (ex != null) {
				message = message == null ? ex.getMessage() : message;
				String logBody = Log.getStackTraceString(ex);
				message = String.format(LOG_FORMAT, message, logBody);
			}

			String text = message;
			int partLen = 0;
			int length = text.length();
			while (text != null && length > 0) {
				partLen = length > LOG_MAX_LEN ? LOG_MAX_LEN : length;
				String partLog = text.substring(0, partLen);
				Log.println(priority, tag, partLog);
				text = text.substring(partLen);
				length = text.length();
			}

		}
	}

}
