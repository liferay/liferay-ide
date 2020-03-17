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

package com.liferay.ide.gradle.core.model;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;

/**
 * @author Vernon Singleton
 * @author Gregory Amerson
 */
public class GradleBuildScript {

	public GradleBuildScript(File file) throws IOException, MultipleCompilationErrorsException {
		this(file.toPath());
	}

	public GradleBuildScript(Path path) throws IOException, MultipleCompilationErrorsException {
		this(new String(Files.readAllBytes(path)));

		_path = path;
	}

	public GradleBuildScript(String scriptContents) throws MultipleCompilationErrorsException {
		AstBuilder astBuilder = new AstBuilder();

		_astNodes = astBuilder.buildFromString(scriptContents);
	}

	public List<GradleDependency> getBuildScriptDependencies() {
		BuildScriptVisitor buildScriptVisitor = new BuildScriptVisitor();

		_walkScript(buildScriptVisitor);

		return buildScriptVisitor.getBuildscriptDependencies();
	}

	public List<GradleDependency> getDependencies() {
		BuildScriptVisitor buildScriptVisitor = new BuildScriptVisitor();

		_walkScript(buildScriptVisitor);

		return buildScriptVisitor.getDependencies();
	}

	public List<GradleDependency> getDependencies(String configuration) {
		BuildScriptVisitor buildScriptVisitor = new BuildScriptVisitor();

		_walkScript(buildScriptVisitor);

		List<GradleDependency> dependencies = buildScriptVisitor.getDependencies();

		return dependencies.stream(
		).filter(
			dep -> Objects.equals(dep.getConfiguration(), configuration)
		).collect(
			Collectors.toList()
		);
	}

	public List<String> getFileContents() {
		return _fileContents;
	}

	public BuildScriptVisitor insertDependency(GradleDependency gradleDependency) throws IOException {
		return _insertDependency(_toGradleDependencyString(gradleDependency));
	}

	public void modifyDependencyVersion(GradleDependency oldDependency, GradleDependency newDependency)
		throws IOException {

		List<String> gradleFileContents = Files.readAllLines(_path);

		int lineNumber = oldDependency.getLineNumber() - 1;

		String lineToModify = gradleFileContents.get(lineNumber);

		String newVersion = newDependency.getVersion();

		Pattern pattern = Pattern.compile("(.*)(" + Pattern.quote(oldDependency.getVersion()) + ")(.*)");

		Matcher matcher = pattern.matcher(lineToModify);

		if (matcher.find()) {
			String modifiedLine = matcher.replaceFirst("$1" + newVersion + "$3");

			gradleFileContents.set(lineNumber, modifiedLine);

			_save(gradleFileContents);
		}
	}

	public void updateDependencies(List<GradleDependency> gradleDependencies) throws IOException {
		BuildScriptVisitor buildScriptVisitor = new BuildScriptVisitor();

		_walkScript(buildScriptVisitor);

		gradleDependencies.sort(
			new Comparator<GradleDependency>() {

				@Override
				public int compare(GradleDependency dep1, GradleDependency dep2) {
					int lastLineNumber1 = dep1.getLastLineNumber();
					int lastLineNumber2 = dep2.getLastLineNumber();

					return lastLineNumber2 - lastLineNumber1;
				}

			});

		_fileContents = Files.readAllLines(_path);

		for (GradleDependency gradleDependency : gradleDependencies) {
			_updateDependency(gradleDependency, gradleDependency);
		}

		String content = _fileContents.stream(
		).collect(
			Collectors.joining(System.lineSeparator())
		);

		Files.write(_path, content.getBytes());
	}

	public void updateDependency(GradleDependency oldArtifact, GradleDependency newArtifact) throws IOException {
		_fileContents = Files.readAllLines(_path);

		_updateDependency(oldArtifact, newArtifact);

		String content = _fileContents.stream(
		).collect(
			Collectors.joining(System.lineSeparator())
		);

		Files.write(_path, content.getBytes());
	}

	public void updateDependency(String dependency) throws IOException {
		_insertDependency(dependency);

		String content = _fileContents.stream(
		).collect(
			Collectors.joining(System.lineSeparator())
		);

		Files.write(_path, content.getBytes());
	}

	private BuildScriptVisitor _insertDependency(String dependency) throws IOException {
		BuildScriptVisitor buildScriptVisitor = new BuildScriptVisitor();

		_walkScript(buildScriptVisitor);

		_fileContents = Files.readAllLines(_path);

		String d = dependency.trim();

		boolean exist = _fileContents.stream(
		).map(
			line -> line.trim()
		).filter(
			line -> line.equals(d)
		).findAny(
		).isPresent();

		if (exist) {
			return buildScriptVisitor;
		}

		if (!dependency.startsWith("\t")) {
			dependency = "\t" + dependency;
		}

		int dependencyLastLineNumber = buildScriptVisitor.getDependenciesLastLineNumber();

		if (dependencyLastLineNumber == -1) {
			_fileContents.add("");
			_fileContents.add("dependencies {");
			_fileContents.add(dependency);
			_fileContents.add("}");
		}
		else {
			_fileContents.add(dependencyLastLineNumber - 1, dependency);
		}

		return buildScriptVisitor;
	}

	private void _save(List<String> contents) throws IOException {
		String content = contents.stream(
		).collect(
			Collectors.joining(System.lineSeparator())
		);

		Files.write(_path, content.getBytes());
	}

	private String _toGradleDependencyString(GradleDependency gradleDependency) {
		StringBuilder sb = new StringBuilder();

		sb.append(gradleDependency.getConfiguration());
		sb.append(" group: \"");
		sb.append(gradleDependency.getGroup());
		sb.append("\", name: \"");
		sb.append(gradleDependency.getName());

		String version = gradleDependency.getVersion();

		if ((version != null) && !version.isEmpty()) {
			sb.append("\", version: \"");
			sb.append(gradleDependency.getVersion());
			sb.append("\"");
		}
		else {
			sb.append("\"");
		}

		return sb.toString();
	}

	private void _updateDependency(GradleDependency oldArtifact, GradleDependency newArtifact) {
		int[] lineNumbers = {oldArtifact.getLineNumber(), oldArtifact.getLastLineNumber()};

		if (lineNumbers.length != 2) {
			return;
		}

		int startLineNumber = lineNumbers[0];

		String content = _fileContents.get(startLineNumber - 1);

		int startPos = content.indexOf(oldArtifact.getConfiguration());

		if (startPos == -1) {
			return;
		}

		int endLineNumber = lineNumbers[1];

		StringBuilder dependencyBuilder = new StringBuilder(_toGradleDependencyString(newArtifact));

		String prefixString = content.substring(0, startPos);

		prefixString.chars(
		).filter(
			ch -> ch == '\t'
		).asLongStream(
		).forEach(
			it -> dependencyBuilder.insert(0, "\t")
		);

		_fileContents.set(startLineNumber - 1, dependencyBuilder.toString());

		for (int i = endLineNumber - 1; i > (startLineNumber - 1); i--) {
			_fileContents.remove(i);
		}
	}

	private void _walkScript(GroovyCodeVisitor visitor) {
		for (ASTNode astNode : _astNodes) {
			astNode.visit(visitor);
		}
	}

	private List<ASTNode> _astNodes;
	private List<String> _fileContents;
	private Path _path;

}