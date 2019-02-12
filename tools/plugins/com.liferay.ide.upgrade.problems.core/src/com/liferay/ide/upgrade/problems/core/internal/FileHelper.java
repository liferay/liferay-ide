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

package com.liferay.ide.upgrade.problems.core.internal;

import com.liferay.ide.core.util.FileUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Amerson
 */
public class FileHelper {

	public List<File> findFiles(File dir, String ext) {
		List<File> files = new ArrayList<>();

		FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				File file = path.toFile();

				if (file.isFile()) {
					if (FileUtil.nameEndsWith(file, ext)) {
						files.add(file);
					}
				}

				return super.visitFile(path, attrs);
			}

		};

		try {
			Files.walkFileTree(dir.toPath(), visitor);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return files;
	}

	public String readFile(File file) throws FileNotFoundException, IOException {
		return new String(Files.readAllBytes(file.toPath()));
	}

	public int writeFile(File file, String contents) throws IOException {
		int retval = -1;

		try (OutputStream stream = Files.newOutputStream(file.toPath());
			BufferedOutputStream out = new BufferedOutputStream(stream);
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(contents.getBytes());
			BufferedInputStream bin = new BufferedInputStream(byteInputStream)) {

			byte[] buffer = new byte[1024];

			int bytesRead = 0;
			int bytesTotal = 0;

			// Keep reading from the file while there is any content
			// when the end of the stream has been reached, -1 is returned

			while ((bytesRead = bin.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				bytesTotal += bytesRead;
			}

			retval = bytesTotal;
		}

		return retval;
	}

}