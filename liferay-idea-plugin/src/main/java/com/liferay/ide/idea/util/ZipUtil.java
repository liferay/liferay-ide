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

package com.liferay.ide.idea.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Terry Jia
 */
public class ZipUtil {

	public static ZipFile open(File file) throws IOException {
		try {
			return new ZipFile(file);
		}
		catch (FileNotFoundException fnfe) {
			FileNotFoundException e = new FileNotFoundException(file.getAbsolutePath());

			e.initCause(fnfe);

			throw e;
		}
	}

	public static void unzip(File file, File destdir) throws IOException {
		unzip(file, null, destdir);
	}

	public static void unzip(File file, String entryToStart, File destdir) throws IOException {
		ZipFile zip = open(file);

		try {
			Enumeration<? extends ZipEntry> entries = zip.entries();

			int totalWork = zip.size();

			int c = 0;

			boolean foundStartEntry = false;

			if (entryToStart == null) {
				foundStartEntry = true;
			}

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				if (!foundStartEntry) {
					foundStartEntry = entryToStart.equals(entry.getName());
					continue;
				}

				String entryName = null;

				if (entryToStart == null) {
					entryName = entry.getName();
				}
				else {
					String name = entry.getName();

					entryName = name.replaceFirst(entryToStart, "");
				}

				if (entry.isDirectory()) {
					File emptyDir = new File(destdir, entryName);

					_mkdir(emptyDir);

					continue;
				}

				File f = new File(destdir, entryName);

				File dir = f.getParentFile();

				_mkdir(dir);

				try (InputStream in = zip.getInputStream(entry); FileOutputStream out = new FileOutputStream(f)) {
					byte[] bytes = new byte[1024];

					int count = in.read(bytes);

					while (count != -1) {
						out.write(bytes, 0, count);
						count = in.read(bytes);
					}

					out.flush();
				}
			}
		}
		finally {
			try {
				zip.close();
			}
			catch (IOException ioe) {
			}
		}
	}

	private static void _mkdir(File dir) throws IOException {
		if (!dir.exists() && !dir.mkdirs()) {
			String msg = "Could not create dir: " + dir.getPath();

			throw new IOException(msg);
		}
	}

}