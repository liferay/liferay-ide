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

import com.liferay.ide.core.util.CoreUtil;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;

import org.apache.commons.io.FileUtils;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;

/**
 * @author Lovett Li
 * @author Simon Jiang
 */
public class GradleDependencyUpdater {

	public GradleDependencyUpdater(File file) throws IOException, MultipleCompilationErrorsException {
		this(FileUtils.readFileToString(file, "UTF-8"));
		_file = file;
	}

	public GradleDependencyUpdater(String scriptContents) throws MultipleCompilationErrorsException {
		AstBuilder builder = new AstBuilder();

		_nodes = builder.buildFromString(scriptContents);
	}

	public List<GradleDependency> getAllDependencies() {
		FindDependenciesVisitor visitor = new FindDependenciesVisitor();

		walkScript(visitor);

		return visitor.getDependencies();
	}

	public List<String> getGradleFileContents() {
		return _gradleFileContents;
	}

	public FindDependenciesVisitor insertDependency(GradleDependency gradleDependency) throws IOException {
		StringBuilder sb = new StringBuilder();

		sb.append("compile group: \"");
		sb.append(gradleDependency.getGroup());
		sb.append("\", name:\"");
		sb.append(gradleDependency.getName());
		sb.append("\", version:\"");
		sb.append(gradleDependency.getVersion());
		sb.append("\"");

		return insertDependency(sb.toString());
	}

	public FindDependenciesVisitor insertDependency(String dependency) throws IOException {
		FindDependenciesVisitor visitor = new FindDependenciesVisitor();

		walkScript(visitor);

		_gradleFileContents = FileUtils.readLines(_file);

		if (!dependency.startsWith("\t")) {
			dependency = "\t" + dependency;
		}

		if (visitor.getDependenceLineNum() == -1) {
			_gradleFileContents.add("");
			_gradleFileContents.add("dependencies {");
			_gradleFileContents.add(dependency);
			_gradleFileContents.add("}");
		}
		else {
			if (visitor.getColumnNum() != -1) {
				_gradleFileContents = Files.readAllLines(Paths.get(_file.toURI()), StandardCharsets.UTF_8);

				StringBuilder builder = new StringBuilder(_gradleFileContents.get(visitor.getDependenceLineNum() - 1));

				builder.insert(visitor.getColumnNum() - 2, "\n" + dependency + "\n");
				String dep = builder.toString();

				if (CoreUtil.isWindows()) {
					dep.replace("\n", "\r\n");
				}
				else if (CoreUtil.isMac()) {
					dep.replace("\n", "\r");
				}

				_gradleFileContents.remove(visitor.getDependenceLineNum() - 1);
				_gradleFileContents.add(visitor.getDependenceLineNum() - 1, dep);
			}
			else {
				_gradleFileContents.add(visitor.getDependenceLineNum() - 1, dependency);
			}
		}

		return visitor;
	}

	public void walkScript(GroovyCodeVisitor visitor) {
		for (ASTNode node : _nodes) {
			node.visit(visitor);
		}
	}

	private File _file;
	private List<String> _gradleFileContents;
	private List<ASTNode> _nodes;

}