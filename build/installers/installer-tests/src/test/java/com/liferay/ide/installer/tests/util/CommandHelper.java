/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.ide.installer.tests.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author Ashley Yuan
 * @author Terry Jia
 */
public class CommandHelper {

	private static String _readStreamToString(InputStream contents) throws IOException {
		if (contents == null) {
			return null;
		}

		char[] buffer = new char[0x10000];

		StringBuilder out = new StringBuilder();

		Reader in = new InputStreamReader(contents, "UTF-8");

		int read;

		do {
			read = in.read(buffer, 0, buffer.length);

			if (read > 0) {
				out.append(buffer, 0, read);
			}
		} while (read >= 0);

		contents.close();

		return out.toString();
	}

	public static String execWithResult(String cmd) throws IOException {
		return execWithResult(cmd, false);
	}

	public static Process exec(File dir, String cmd) throws IOException {
		String fullCmd = dir.getPath() + "/" + cmd;

		Runtime runtime = Runtime.getRuntime();

		return runtime.exec(fullCmd);
	}

	public static Process exec(String fullCmd) throws IOException {
		Runtime runtime = Runtime.getRuntime();

		return runtime.exec(fullCmd);
	}

	public static String execWithResult(String cmd, boolean error) throws IOException {
		Process process = exec(cmd);

		InputStream in = null;

		if (error) {
			in = process.getErrorStream();
		} else {
			in = process.getInputStream();
		}

		return _readStreamToString(in);
	}

}