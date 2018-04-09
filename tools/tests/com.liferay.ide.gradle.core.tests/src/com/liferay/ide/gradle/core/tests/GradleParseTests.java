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

package com.liferay.ide.gradle.core.tests;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.parser.FindDependenciesVisitor;
import com.liferay.ide.gradle.core.parser.GradleDependency;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lovett Li
 */
public class GradleParseTests {

	@Test
	public void addDependenceInClosureLine() throws IOException {
		File inputFile = new File("projects/testParseInput/testParse5.gradle");

		GradleDependencyUpdater updater = new GradleDependencyUpdater(inputFile);

		FindDependenciesVisitor visitor = updater.insertDependency(
			"\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"");

		int dependenceLineNum = visitor.getDependenceLineNum();

		Assert.assertEquals(24, dependenceLineNum);

		Files.write(_outputfile.toPath(), updater.getGradleFileContents(), StandardCharsets.UTF_8);

		File expectedOutputFile = new File("projects/testParseOutput/testParse5.gradle");

		Assert.assertEquals(
			_encoding(CoreUtil.readStreamToString(Files.newInputStream(expectedOutputFile.toPath()))),
			_encoding(CoreUtil.readStreamToString(Files.newInputStream(_outputfile.toPath()))));
	}

	@Test
	public void addDependenceInSameLine() throws IOException {
		File inputFile = new File("projects/testParseInput/testParse4.gradle");

		GradleDependencyUpdater updater = new GradleDependencyUpdater(inputFile);

		FindDependenciesVisitor visitor = updater.insertDependency(
			"\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"");

		int dependenceLineNum = visitor.getDependenceLineNum();

		Assert.assertEquals(23, dependenceLineNum);

		Files.write(_outputfile.toPath(), updater.getGradleFileContents(), StandardCharsets.UTF_8);

		File expectedOutputFile = new File("projects/testParseOutput/testParse4.gradle");

		Assert.assertEquals(
			_encoding(CoreUtil.readStreamToString(Files.newInputStream(expectedOutputFile.toPath()))),
			_encoding(CoreUtil.readStreamToString(Files.newInputStream(_outputfile.toPath()))));
	}

	@Test
	public void addDependenceIntoEmptyBlock() throws IOException {
		File inputFile = new File("projects/testParseInput/testParse2.gradle");

		GradleDependencyUpdater updater = new GradleDependencyUpdater(inputFile);

		FindDependenciesVisitor visitor = updater.insertDependency(
			"\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"");

		int dependenceLineNum = visitor.getDependenceLineNum();

		Assert.assertEquals(24, dependenceLineNum);

		Files.write(_outputfile.toPath(), updater.getGradleFileContents(), StandardCharsets.UTF_8);

		File expectedOutputFile = new File("projects/testParseOutput/testParse2.gradle");

		Assert.assertEquals(
			_encoding(CoreUtil.readStreamToString(Files.newInputStream(expectedOutputFile.toPath()))),
			_encoding(CoreUtil.readStreamToString(Files.newInputStream(_outputfile.toPath()))));
	}

	@Test
	public void addDependenceSkipComment() throws IOException {
		File inputFile = new File("projects/testParseInput/testParse.gradle");

		GradleDependencyUpdater updater = new GradleDependencyUpdater(inputFile);

		FindDependenciesVisitor visitor = updater.insertDependency(
			"\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"");

		int dependenceLineNum = visitor.getDependenceLineNum();

		Assert.assertEquals(27, dependenceLineNum);

		Files.write(_outputfile.toPath(), updater.getGradleFileContents(), StandardCharsets.UTF_8);

		File expectedOutputFile = new File("projects/testParseOutput/testParse.gradle");

		Assert.assertEquals(
			_encoding(CoreUtil.readStreamToString(Files.newInputStream(expectedOutputFile.toPath()))),
			_encoding(CoreUtil.readStreamToString(Files.newInputStream(_outputfile.toPath()))));
	}

	@Test
	public void addDependenceWithoutDendendenceBlock() throws IOException {
		File inputFile = new File("projects/testParseInput/testParse3.gradle");

		GradleDependencyUpdater updater = new GradleDependencyUpdater(inputFile);

		FindDependenciesVisitor visitor = updater.insertDependency(
			"\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"");

		int dependenceLineNum = visitor.getDependenceLineNum();

		Assert.assertEquals(-1, dependenceLineNum);

		Files.write(_outputfile.toPath(), updater.getGradleFileContents(), StandardCharsets.UTF_8);

		File expectedOutputFile = new File("projects/testParseOutput/testParse3.gradle");

		Assert.assertEquals(
			_encoding(CoreUtil.readStreamToString(Files.newInputStream(expectedOutputFile.toPath()))),
			_encoding(CoreUtil.readStreamToString(Files.newInputStream(_outputfile.toPath()))));
	}

	@Test
	public void getAllDependencies() throws IOException {
		File inputFile = new File("projects/testParseInput/testDependencies.gradle");

		GradleDependencyUpdater updater = new GradleDependencyUpdater(inputFile);

		List<GradleDependency> allDependence = updater.getAllDependencies();

		Assert.assertEquals("", 3, allDependence.size());
	}

	@Test
	public void getAllDependenciesShortFormat() throws IOException {
		File inputFile = new File("projects/testParseInput/testDependenciesShortFormat.gradle");

		GradleDependencyUpdater updater = new GradleDependencyUpdater(inputFile);

		List<GradleDependency> allDependencies = updater.getAllDependencies();

		Assert.assertEquals("", 3, allDependencies.size());
	}

	@Test
	public void getAllDependenciesShortFormatAndLongFormat() throws IOException {
		File inputFile = new File("projects/testParseInput/testDependenciesShortFormatAndLongFormat.gradle");

		GradleDependencyUpdater updater = new GradleDependencyUpdater(inputFile);

		List<GradleDependency> allDependencies = updater.getAllDependencies();

		Assert.assertEquals("", 3, allDependencies.size());
	}

	@Before
	public void setUp() throws IOException {
		if (_outputfile.exists()) {
			Assert.assertTrue(_outputfile.delete());
		}

		File parentFile = _outputfile.getParentFile();

		parentFile.mkdirs();

		Assert.assertTrue(_outputfile.createNewFile());
	}

	private String _encoding(String contents) {
		if (CoreUtil.isWindows()) {
			contents = contents.replace("\r\n", "\n");

			return contents;
		}
		else if (CoreUtil.isMac()) {
			contents = contents.replace("\r", "\n");

			return contents;
		}
		else {
			return contents;
		}
	}

	private static final File _outputfile = new File("generated/test/testbuild.gradle");

}