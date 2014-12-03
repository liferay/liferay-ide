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

import com.liferay.ide.core.util.StringPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Shuyang Zhou
 */
public class JPSUtil {

	public static long getPid(String processName) {
		return getPid(processName, null, null, null);
	}

	public static long getPid(String processName, String arguments) {
		return getPid(processName, arguments, null, null);
	}

	public static long getPid(String processName, String host, String port) {
		return getPid(processName, host, port);
	}

	public static long getPid(
		String processName, String arguments, String host, String port) {

		boolean remote = host != null && port != null;

		if (remote) {
			_logger.log(
				Level.INFO, "Finding pid for process with name : {0}," +
					" arguments : {1}, host : {2}, port : {3}.",
				new Object[] {processName, arguments, host, port});
		}
		else {
			_logger.log(
				Level.INFO, "Finding pid for process with name : {0}," +
					" arguments : {1}.", new Object[] {processName, arguments});
		}

		Process jpsProcess = null;

		BufferedReader reader = null;

		long pid = -1;

		try {
			ProcessBuilder processBuilder = null;

			if (remote) {
				processBuilder = new ProcessBuilder(
					"jps", "-m", host + StringPool.COLON +port);
			}
			else {
				processBuilder = new ProcessBuilder("jps", "-m");
			}

			jpsProcess = processBuilder.start();

			reader = new BufferedReader(new InputStreamReader(
				jpsProcess.getInputStream()));

			String readLine = null;

			List<String> lines = new ArrayList<String>();

			while ((readLine = reader.readLine()) != null) {
				lines.add(readLine);

				_logger.log(Level.INFO, "jps echo : [{0}]", readLine);
			}

			for (String line : lines) {
				List<String> elements = StringUtil.split(line, StringPool.SPACE);

				if ((arguments != null) && (elements.size() >= 3)) {
					if (elements.get(1).equalsIgnoreCase(processName) &&
							elements.get(2).equalsIgnoreCase(arguments)) {
						try {
							pid = Long.parseLong(elements.get(0));

							break;
						}
						catch (NumberFormatException ex) {
							//ignore, continue
						}
					}
				}
				else if (elements.size() >= 2) {
					if (elements.get(1).equalsIgnoreCase(processName)) {
						try {
							pid = Long.parseLong(elements.get(0));

							break;
						}
						catch (NumberFormatException ex) {
							//ignore, continue
						}
					}
				}
			}
		}
		catch (IOException ioe) {
			_logger.log(
				Level.SEVERE, "Fail to find process with name : " + processName,
				ioe);
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				}
				catch (IOException ioe) {
					_logger.log(
						Level.SEVERE, "Fail to close process InputStream.",
						ioe);
				}
			}

			if (jpsProcess != null) {
				jpsProcess.destroy();
			}
		}

		if (pid == -1) {
			_logger.log(
				Level.WARNING, "No process with name : {0} is found.",
				processName);
		}
		else {
			_logger.log(
				Level.INFO, "Found process with name : {0}, pid is : {1}",
				new Object[]{processName, pid});
		}

		return pid;
	}

	private static final Logger _logger = Logger.getLogger(
		JPSUtil.class.getName());

}
