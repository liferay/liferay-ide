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

package com.liferay.ide.core.tests;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Kyle Miho
 */
public class ZipUtilTests extends BaseTests {

	@Before
	public void setUp() throws IOException {
		Path tempDirPath = Files.createTempDirectory("ziputil-test");

		_tempDir = tempDirPath.toFile();
	}

	@After
	public void tearDown() {
		FileUtil.deleteDir(_tempDir, true);
	}

	@Test
	public void unzip() throws Exception {
		File zipFile = _createZip(_tempDir, "test.zip", "dir/file.txt");

		File destDir = new File(_tempDir, "dest");

		destDir.mkdirs();

		ZipUtil.unzip(zipFile, destDir);

		assertFileExists(new File(destDir, "dir/file.txt"));
	}

	@Test
	public void unzipBlocksPathTraversal() throws Exception {
		File zipFile = _createZip(_tempDir, "evil.zip", "../escape.txt");

		File destDir = new File(_tempDir, "dest");

		destDir.mkdirs();

		try {
			ZipUtil.unzip(zipFile, destDir);

			Assert.fail("Expected IOException for path traversal entry");
		}
		catch (IOException ioe) {
		}

		assertFileNotExists(new File(_tempDir, "escape.txt"));
	}

	private File _createZip(File destDir, String fileName, String... entryNames) throws IOException {
		File zipFile = new File(destDir, fileName);

		try (FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
			ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {

			for (String entryName : entryNames) {
				ZipEntry zipEntry = new ZipEntry(entryName);

				zipOutputStream.putNextEntry(zipEntry);

				zipOutputStream.write(_ENTRY_CONTENT, 0, _ENTRY_CONTENT.length);

				zipOutputStream.closeEntry();
			}
		}

		return zipFile;
	}

	private static final byte[] _ENTRY_CONTENT = "content".getBytes();

	private File _tempDir;

}
