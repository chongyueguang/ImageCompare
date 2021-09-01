package com.company.util;

import org.apache.log4j.Logger;

public class LogUtils {

	public static void trace(String msg) {
		getLogger().trace(msg);
	}

	public static void debug(String msg) {
		getLogger().debug(msg);
	}

	public static void info(String msg) {
		getLogger().info(msg);
	}

	public static void warn(String msg) {
		getLogger().warn(msg);
	}

	public static void error(String msg) {
		getLogger().error(msg);
	}

	public static void error(String msg, Throwable t) {
		getLogger().error(msg, t);
	}

	private static Logger getLogger() {
		return Logger.getLogger(findCaller().getClassName());
	}

	private static StackTraceElement findCaller() {
		// スタック情報を取得する
		StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
		StackTraceElement caller = null;

		// ログクラス名
		String logClassName = LogUtils.class.getName();
		// クラスIDをログに記録するためのループ
		int i = 0;
		for (int len = callStack.length; i < len; i++) {
			if (logClassName.equals(callStack[i].getClassName())) {
				break;
			}
		}
		caller = callStack[i + 3];
		return caller;
	}

}
