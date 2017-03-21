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

package com.liferay.ide.portal.core.debug.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Shuyang Zhou
 */
public class ThreadUtil {

	public static String dump(long pid) {
		_logger.log(Level.INFO, "Dumping threads for process with id:{0}", pid);

		ProcessBuilder jstackProcessBuilder = new ProcessBuilder(
			"jstack", "-l", Long.toString(pid));

		Process jstackProcess = null;

		try {
			jstackProcess = jstackProcessBuilder.start();

			Reader reader = new InputStreamReader(
				jstackProcess.getInputStream());

			StringBuilder sb = new StringBuilder();

			int ch = -1;
			while ((ch = reader.read()) != -1) {
				sb.append((char) ch);
			}

			_logger.log(
				Level.INFO, "Dumped out threads for process with pid : {0}" +
					" successfully.", pid);

			return sb.toString();
		}
		catch (Exception ex) {
			_logger.log(
				Level.SEVERE, "Fail to dump threads for process with id:" + pid,
				ex);
			return null;
		}
		finally {
			if (jstackProcess != null) {
				jstackProcess.destroy();
			}
		}
	}

	public static String dump(String processName) {
		long pid = JPSUtil.getPid(processName);

		if (pid == -1) {
			_logger.log(
				Level.SEVERE, "Fail to find process with name:{0}, abort" +
					" dumping process",	processName);
			return null;
		}

		return dump(pid);
	}

	public static void dump(String processName, String dir) {
		Date date = new Date();

		String threadDump = dump(processName);

		if (threadDump == null) {
			return;
		}

		File dumpFile = new File(dir, date.toString() + ".tdump");

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(dumpFile);
			fileWriter.write(threadDump);
			fileWriter.flush();
		}
		catch (IOException ioe) {
			_logger.log(Level.SEVERE, "Fail to write thread dump.", ioe);
		}
		finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException ioe) {
					_logger.log(
						Level.SEVERE, "Fail to close thread dump file writer.",
						ioe);
				}
			}
		}

		_logger.log(
			Level.INFO, "Saved thread dump to file : {0}.",
			dumpFile.getAbsolutePath());
	}

	private static final Logger _logger = Logger.getLogger(
		ThreadUtil.class.getName());

}
