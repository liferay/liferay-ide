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

import com.liferay.ide.core.Artifact;
import com.liferay.ide.gradle.core.parser.DependenciesClosureVisitor;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.test.core.base.BaseTests;
import com.liferay.ide.test.core.base.support.FileSupport;

import java.io.IOException;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lovett Li
 * @author Terry Jia
 */
public class GradleParseTests extends BaseTests {

	@Test
	public void addDependenceInClosureLine() throws IOException {
		FileSupport fs = new FileSupport("testParse5.gradle", false);

		fs.before();

		GradleDependencyUpdater updater = new GradleDependencyUpdater(fs.getFile());

		Artifact artifact = new Artifact("com.liferay", "com.liferay.bookmarks.api", "1.0.0", "compile", null);

		DependenciesClosureVisitor visitor = updater.insertDependency(artifact);

		int dependenceLineNum = visitor.getDependenciesLineNumber();

		Assert.assertEquals(24, dependenceLineNum);

		writeFile(fs, updater.getGradleFileContents());

		assertFileContains(fs, "compile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"");

		fs.after();
	}

	@Test
	public void addDependenceInSameLine() throws IOException {
		FileSupport fs = new FileSupport("testParse4.gradle", false);

		fs.before();

		GradleDependencyUpdater updater = new GradleDependencyUpdater(fs.getFile());

		Artifact artifact = new Artifact("com.liferay", "com.liferay.bookmarks.api", "1.0.0", "compile", null);

		DependenciesClosureVisitor visitor = updater.insertDependency(artifact);

		int dependenceLineNum = visitor.getDependenciesLineNumber();

		Assert.assertEquals(23, dependenceLineNum);

		writeFile(fs, updater.getGradleFileContents());

		assertFileContains(fs, "compile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"");

		fs.after();
	}

	@Test
	public void addDependenceIntoEmptyBlock() throws IOException {
		FileSupport fs = new FileSupport("testParse2.gradle", false);

		fs.before();

		GradleDependencyUpdater updater = new GradleDependencyUpdater(fs.getFile());

		Artifact artifact = new Artifact("com.liferay", "com.liferay.bookmarks.api", "1.0.0", "compile", null);

		DependenciesClosureVisitor visitor = updater.insertDependency(artifact);

		int dependenceLineNum = visitor.getDependenciesLineNumber();

		Assert.assertEquals(24, dependenceLineNum);

		writeFile(fs, updater.getGradleFileContents());

		assertFileContains(fs, "compile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"");

		fs.after();
	}

	@Test
	public void addDependenceSkipComment() throws IOException {
		FileSupport fs = new FileSupport("testParse.gradle", false);

		fs.before();

		GradleDependencyUpdater updater = new GradleDependencyUpdater(fs.getFile());

		Artifact artifact = new Artifact("com.liferay", "com.liferay.bookmarks.api", "1.0.0", "compile", null);

		DependenciesClosureVisitor visitor = updater.insertDependency(artifact);

		int dependenceLineNum = visitor.getDependenciesLineNumber();

		Assert.assertEquals(27, dependenceLineNum);

		writeFile(fs, updater.getGradleFileContents());

		assertFileContains(fs, "compile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"");

		fs.after();
	}

	@Test
	public void addDependenceWithoutDendendenceBlock() throws IOException {
		FileSupport fs = new FileSupport("testParse3.gradle", false);

		fs.before();

		GradleDependencyUpdater updater = new GradleDependencyUpdater(fs.getFile());

		Artifact artifact = new Artifact("com.liferay", "com.liferay.bookmarks.api", "1.0.0", "compile", null);

		DependenciesClosureVisitor visitor = updater.insertDependency(artifact);

		int dependenceLineNum = visitor.getDependenciesLineNumber();

		Assert.assertEquals(-1, dependenceLineNum);

		writeFile(fs, updater.getGradleFileContents());

		assertFileContains(fs, "compile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"");

		fs.after();
	}

	@Test
	public void getAllDependencies() throws IOException {
		FileSupport fs = new FileSupport("testDependencies.gradle", false);

		fs.before();

		GradleDependencyUpdater updater = new GradleDependencyUpdater(fs.getFile());

		List<Artifact> allDependencies = updater.getDependencies("*");

		Assert.assertEquals("", 3, allDependencies.size());

		fs.after();
	}

	@Test
	public void getAllDependenciesShortFormat() throws IOException {
		FileSupport fs = new FileSupport("testDependenciesShortFormat.gradle", false);

		fs.before();

		GradleDependencyUpdater updater = new GradleDependencyUpdater(fs.getFile());

		List<Artifact> allDependencies = updater.getDependencies("*");

		Assert.assertEquals("", 3, allDependencies.size());

		fs.after();
	}

	@Test
	public void getAllDependenciesShortFormatAndLongFormat() throws IOException {
		FileSupport fs = new FileSupport("testDependenciesShortFormatAndLongFormat.gradle", false);

		fs.before();

		GradleDependencyUpdater updater = new GradleDependencyUpdater(fs.getFile());

		List<Artifact> allDependencies = updater.getDependencies("*");

		Assert.assertEquals("", 3, allDependencies.size());

		fs.after();
	}

}