package com.liferay.blade.test;

public class Util {
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}
}
