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

package com.liferay.ide.gradle.core.parser;

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.builder.AstBuilder;

import org.eclipse.core.resources.IFile;

/**
 * @author Lovett Li
 * @author Simon Jiang
 * @author Terry Jia
 */
public class GradleDependencyUpdater {

	public GradleDependencyUpdater(File file) throws IOException {
		this(FileUtils.readFileToString(file, "UTF-8"));

		_file = file;
	}

	public GradleDependencyUpdater(IFile file) throws IOException {
		this(FileUtils.readFileToString(FileUtil.getFile(file), "UTF-8"));

		_file = FileUtil.getFile(file);
	}

	public GradleDependencyUpdater(String scriptContents) {
		AstBuilder builder = new AstBuilder();

		_nodes = builder.buildFromString(scriptContents);
	}

	public List<Artifact> getDependencies(boolean buildscript, String configuration) {
		DependenciesClosureVisitor visitor = new DependenciesClosureVisitor(buildscript);

		_walkScript(visitor);

		return visitor.getDependencies(configuration);
	}

	public List<Artifact> getDependencies(String configuration) {
		DependenciesClosureVisitor visitor = new DependenciesClosureVisitor();

		_walkScript(visitor);

		return visitor.getDependencies(configuration);
	}

	public List<String> getGradleFileContents() {
		return _gradleFileContents;
	}

	public DependenciesClosureVisitor insertDependency(Artifact artifact) throws IOException {
		return _insertDependency(_toGradleDependencyString(artifact));
	}

	public void updateDependency(String dependency) throws IOException {
		_insertDependency(dependency);

		FileUtils.writeLines(_file, _gradleFileContents);
	}

	public void updateDependencyVersions(boolean buildscript, List<Artifact> artifacts) throws IOException {
		DependenciesClosureVisitor dependenciesClosureVisitor = new DependenciesClosureVisitor(buildscript);

		_walkScript(dependenciesClosureVisitor);

		artifacts.sort(
			new Comparator<Artifact>() {

				@Override
				public int compare(Artifact artifact1, Artifact artifact2) {
					int[] lineNumbers1 = dependenciesClosureVisitor.getDependenceLineNumbers(artifact1);
					int[] lineNumbers2 = dependenciesClosureVisitor.getDependenceLineNumbers(artifact2);

					return lineNumbers2[1] - lineNumbers1[1];
				}

			});

		_gradleFileContents = Arrays.asList(FileUtil.readLinesFromFile(_file));

		for (Artifact artifact : artifacts) {
			int[] lineNumbers = dependenciesClosureVisitor.getDependenceLineNumbers(artifact);

			if (lineNumbers.length != 2) {
				return;
			}

			int startLineNumber = lineNumbers[0];

			int endLineNumber = lineNumbers[1];

			String content = _gradleFileContents.get(startLineNumber - 1);

			int startPos = content.indexOf(artifact.getConfiguration());

			if (startPos == -1) {
				return;
			}

			StringBuilder dependencyBuilder = new StringBuilder(_toGradleDependencyString(artifact));

			String prefixString = content.substring(0, startPos);

			prefixString.chars(
			).filter(
				ch -> ch == '\t'
			).asLongStream(
			).forEach(
				it -> dependencyBuilder.insert(0, "\t")
			);

			_gradleFileContents.set(startLineNumber - 1, dependencyBuilder.toString());

			for (int i = endLineNumber - 1; i > startLineNumber - 1; i--) {
				_gradleFileContents.remove(i);
			}
		}

		FileUtils.writeLines(_file, _gradleFileContents);
	}

	private DependenciesClosureVisitor _insertDependency(String dependency) throws IOException {
		DependenciesClosureVisitor visitor = new DependenciesClosureVisitor();

		_walkScript(visitor);

		_gradleFileContents = Arrays.asList(FileUtil.readLinesFromFile(_file));

		if (!dependency.startsWith("\t")) {
			dependency = "\t" + dependency;
		}

		int dependencyLineNumber = visitor.getDependenciesLineNumber();

		if (dependencyLineNumber == -1) {
			_gradleFileContents.add("");
			_gradleFileContents.add("dependencies {");
			_gradleFileContents.add(dependency);
			_gradleFileContents.add("}");
		}
		else {
			_gradleFileContents.add(dependencyLineNumber - 1, dependency);
		}

		return visitor;
	}

	private String _toGradleDependencyString(Artifact artifact) {
		StringBuilder sb = new StringBuilder();

		sb.append(artifact.getConfiguration());
		sb.append(" group: \"");
		sb.append(artifact.getGroupId());
		sb.append("\", name: \"");
		sb.append(artifact.getArtifactId());

		if (CoreUtil.isNotNullOrEmpty(artifact.getVersion())) {
			sb.append("\", version:\"");
			sb.append(artifact.getVersion());
			sb.append("\"");
		}
		else {
			sb.append("\"");
		}

		return sb.toString();
	}

	private void _walkScript(GroovyCodeVisitor visitor) {
		for (ASTNode node : _nodes) {
			node.visit(visitor);
		}
	}

	private File _file;
	private List<String> _gradleFileContents;
	private List<ASTNode> _nodes;

}