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

package com.liferay.ide.core.util;

import com.liferay.ide.core.LiferayCore;

import java.io.File;
import java.io.IOException;

import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.EnumSet;

/**
 * @author Terry Jia
 */
public class IOUtil {

	public static void copyDirToDir(File src, File dest) {
		Path targetPath = Paths.get(dest.getPath());
		Path sourcePath = Paths.get(src.getPath());

		EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
		TreeCopier tc = new TreeCopier(sourcePath, targetPath);

		try {
			Files.walkFileTree(sourcePath, opts, Integer.MAX_VALUE, tc);
		}
		catch (IOException ioe) {
			LiferayCore.logError("copy folder " + src.getName() + " error", ioe);
		}
	}

	private static class TreeCopier implements FileVisitor<Path> {

		public TreeCopier(Path source, Path target) {
			_source = source;
			_target = target;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			_copyFile(file, _target.resolve(_source.relativize(file)));

			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) {
			return FileVisitResult.CONTINUE;
		}

		private void _copyFile(Path source, Path target) {
			CopyOption[] options = {StandardCopyOption.REPLACE_EXISTING};

			try {
				File file = target.toFile();

				File folder = file.getParentFile();

				if (!folder.exists()) {
					folder.mkdirs();
				}

				Files.copy(source, target, options);
			}
			catch (IOException ioe) {
				File file = source.toFile();

				LiferayCore.logError("copy file " + file.getName() + " error", ioe);
			}
		}

		private final Path _source;
		private final Path _target;

	}

}