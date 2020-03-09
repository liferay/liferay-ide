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

import com.liferay.ide.core.Artifact;
import com.liferay.ide.gradle.core.model.GradleDependencyUpdater;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Seiphon Wang
 */
public abstract class GradleFileMigrator implements FileMigrator {

	@Activate
	public void activate(ComponentContext ctx) {
		context = ctx;

		Dictionary<String, Object> properties = context.getProperties();

		problemTitle = safeGet(properties, "problem.title");
		problemSummary = safeGet(properties, "problem.summary");
		problemType = safeGet(properties, "file.extensions");
		problemTickets = safeGet(properties, "problem.tickets");
		sectionKey = safeGet(properties, "problem.section");
		version = safeGet(properties, "version");

		addDependenciesToSearch(artifactIds);
	}

	@Override
	public List<UpgradeProblem> analyze(File file) {
		List<UpgradeProblem> problems = new ArrayList<>();

		for (String artifactId : artifactIds) {
			List<FileSearchResult> results = searchFile(file, artifactId);

			if (results != null) {
				String fileName = "BREAKING_CHANGES.markdown";

				if ("7.0".equals(version)) {
					fileName = "liferay70/" + fileName;
				}
				else if ("7.1".equals(version)) {
					fileName = "liferay71/" + fileName;
				}

				String sectionHtml = MarkdownParser.getSection(fileName, sectionKey);

				for (FileSearchResult searchResult : results) {
					problems.add(
						new UpgradeProblem(
							problemTitle, problemSummary, problemType, problemTickets, version, file,
							searchResult.startLine, searchResult.startOffset, searchResult.endOffset, sectionHtml,
							searchResult.autoCorrectContext, UpgradeProblem.STATUS_NOT_RESOLVED,
							UpgradeProblem.DEFAULT_MARKER_ID, UpgradeProblem.MARKER_ERROR));
				}
			}
		}

		return problems;
	}

	public List<Artifact> findArtifactsbyArtifactId(
		GradleDependencyUpdater gradleDependencyUpdater, String artifactId) {

		List<Artifact> artifacts = new ArrayList<>();

		List<Artifact> dependencies = gradleDependencyUpdater.getDependencies("*");

		artifacts = dependencies.stream(
		).filter(
			artifact -> artifactId.equals(artifact.getArtifactId())
		).collect(
			Collectors.toList()
		);

		return artifacts;
	}

	public List<FileSearchResult> findDependencies(File file, String artifactId) {
		List<FileSearchResult> retval = new ArrayList<>();

		GradleDependencyUpdater gradleDependencyUpdater = getGradleDependencyUpdater(file);

		if (gradleDependencyUpdater == null) {
			return retval;
		}

		List<String> gradleFileContents = new ArrayList<>();

		String gradleFileContentString = "";

		try {
			gradleFileContents = FileUtils.readLines(file);

			gradleFileContentString = FileUtils.readFileToString(file, "UTF-8");
		}
		catch (Exception e) {
		}

		List<Artifact> dependencies = gradleDependencyUpdater.getDependencies("*");

		List<Artifact> artifacts = dependencies.stream(
		).filter(
			artifact -> artifactId.equals(artifact.getArtifactId())
		).collect(
			Collectors.toList()
		);

		for (Artifact artifact : artifacts) {
			int[] lineNumbers = gradleDependencyUpdater.getDependenceLineNumbers(artifact);

			int startLineNumber = lineNumbers[0];
			int endLineNumber = lineNumbers[1];

			String startLineContent = gradleFileContents.get(startLineNumber - 1);

			String endLineContent = gradleFileContents.get(endLineNumber - 1);

			int originLength = startLineContent.length();

			startLineContent = startLineContent.trim();

			int startPos = gradleFileContentString.indexOf(startLineContent) + originLength - startLineContent.length();

			int endPos = gradleFileContentString.indexOf(endLineContent) + endLineContent.length();

			FileSearchResult result = new FileSearchResult(
				file, startPos, endPos, startLineNumber, endLineNumber, true);

			result.autoCorrectContext = "dependency:artifactId";

			retval.add(result);
		}

		return retval;
	}

	protected abstract void addDependenciesToSearch(List<String> dependencies);

	protected GradleDependencyUpdater getGradleDependencyUpdater(File file) {
		GradleDependencyUpdater gradleDependencyUpdater = null;

		try {
			gradleDependencyUpdater = new GradleDependencyUpdater(file);
		}
		catch (IOException ioe) {
			return null;
		}

		return gradleDependencyUpdater;
	}

	protected abstract List<FileSearchResult> searchFile(File file, String artifactId);

	protected final List<String> artifactIds = new ArrayList<>();
	protected ComponentContext context;
	protected String problemSummary;
	protected String problemTickets;
	protected String problemTitle;
	protected String problemType;
	protected String sectionKey = "";
	protected String version = "";

}