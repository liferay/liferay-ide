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

package com.liferay.ide.ui.liferay.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.util.NLS;

import com.liferay.ide.ui.swtbot.util.StringPool;

/**
 * @author Greg Amerson
 * @author Cindy Li
 * @author Simon Jiang
 */
public class ZipUtil {

	public static String getFirstZipEntryName(File zipFile) throws Exception {
		ZipFile zip = new ZipFile(zipFile);

		ZipEntry entry = zip.entries().nextElement();

		String name = entry.getName();

		zip.close();

		return name;
	}

	public static ZipEntry getZipEntry(ZipFile zip, String name) {
		String lcasename = name.toLowerCase();

		for (Enumeration<? extends ZipEntry> itr = zip.entries(); itr.hasMoreElements();) {
			ZipEntry zipentry = itr.nextElement();

			String entryName = zipentry.getName().toLowerCase();

			if (entryName.equals(lcasename)) {
				return zipentry;
			}
		}

		return null;
	}

	public static ZipFile open(File file) throws IOException {
		try {
			return new ZipFile(file);
		}
		catch (FileNotFoundException fnfe) {
			FileNotFoundException exception = new FileNotFoundException(file.getAbsolutePath());

			exception.initCause(fnfe);

			throw fnfe;
		}
	}

	public static void unzip(File file, File destdir) throws IOException {
		unzip(file, destdir, new NullProgressMonitor());
	}

	public static void unzip(File file, File destdir, IProgressMonitor monitor) throws IOException {
		unzip(file, null, destdir, monitor);
	}

	public static void unzip(File file, String entryToStart, File destdir, IProgressMonitor monitor)
		throws IOException {

		ZipFile zip = open(file);

		try {
			Enumeration<? extends ZipEntry> entries = zip.entries();

			int totalWork = zip.size();

			monitor.beginTask(Resources.progressUnzipping, totalWork);

			int c = 0;

			boolean foundStartEntry = false;

			if (entryToStart == null) {
				foundStartEntry = true;
			}

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				if (!foundStartEntry) {
					foundStartEntry = entryToStart.equals(FileUtil.removeDirPrefix(entry.getName()));
					continue;
				}

				monitor.worked(1);

				String taskMsg = NLS.bind(Resources.progressUnzipped, new Object[] {file.getName(), c++, totalWork});

				monitor.subTask(taskMsg);

				if (entry.isDirectory()) {
					continue;
				}

				String entryName = null;

				if (entryToStart == null) {
					entryName = entry.getName();
				}
				else {
					entryName = entry.getName().replaceFirst(entryToStart, StringPool.BLANK);
				}

				File f = new File(destdir, entryName);

				File dir = f.getParentFile();

				if (!dir.exists() && !dir.mkdirs()) {
					String msg = "Could not create dir: " + dir.getPath();

					throw new IOException(msg);
				}

				InputStream in = null;
				OutputStream out = null;

				try {
					in = zip.getInputStream(entry);
					out = Files.newOutputStream(f.toPath());

					byte[] bytes = new byte[1024];

					int count = in.read(bytes);

					while (count != -1) {
						out.write(bytes, 0, count);
						count = in.read(bytes);
					}

					out.flush();
				}
				finally {
					if (in != null) {
						try {
							in.close();
						}
						catch (IOException ioe) {
						}
					}

					if (out != null) {
						try {
							out.close();
						}
						catch (IOException ioe) {
						}
					}
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

	public static void zip(File dir, File target) throws IOException {
		zip(dir, null, target);
	}

	public static void zip(File dir, FilenameFilter filenameFilter, File target) throws IOException {
		if (target.exists()) {
			_delete(target);
		}

		try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(target.toPath()))) {
			_zipDir(target, zip, dir, filenameFilter, StringPool.BLANK);
		}
	}

	private static void _delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				_delete(child);
			}
		}

		if (!f.delete()) {
			String msg = "Could not delete " + f.getPath() + ".";

			throw new IOException(msg);
		}
	}

	private static void _zipDir(File target, ZipOutputStream zip, File dir, FilenameFilter filter, String path)
		throws IOException {

		for (File f : filter != null ? dir.listFiles(filter) : dir.listFiles()) {
			String cpath = path + f.getName();

			if (f.isDirectory()) {
				_zipDir(target, zip, f, filter, cpath + "/");
			}
			else {
				_zipFile(target, zip, f, cpath);
			}
		}
	}

	private static void _zipFile(File target, ZipOutputStream zip, File file, String path) throws IOException {
		if (!file.equals(target)) {
			ZipEntry ze = new ZipEntry(path);

			ze.setTime(file.lastModified() + 1999);
			ze.setMethod(ZipEntry.DEFLATED);

			zip.putNextEntry(ze);

			InputStream in = Files.newInputStream(file.toPath());

			try {
				int bufsize = 8 * 1024;
				long flength = file.length();

				if (flength == 0) {
					return;
				}
				else if (flength < bufsize) {
					bufsize = (int)flength;
				}

				byte[] buffer = new byte[bufsize];

				int count = in.read(buffer);

				while (count != -1) {
					zip.write(buffer, 0, count);
					count = in.read(buffer);
				}
			}
			finally {
				try {
					in.close();
				}
				catch (IOException ioe) {
				}
			}
		}
	}

	private static class Resources extends NLS {

		public static String progressUnzipped;
		public static String progressUnzipping;

		static {
			initializeMessages(ZipUtil.class.getName(), Resources.class);
		}

	}

}