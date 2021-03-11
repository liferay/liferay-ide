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

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Seiphon Wang
 */
public abstract class LegacyFilesMigrator implements FileMigrator {

	@Activate
	public void activate(ComponentContext ctx) {
		context = ctx;

		Dictionary<String, Object> properties = context.getProperties();

		problemType = safeGet(properties, "problem.type");
		problemTitle = safeGet(properties, "problem.title");
		problemSummary = safeGet(properties, "problem.summary");
		fileExtension = safeGet(properties, "file.extensions");
		problemTickets = safeGet(properties, "problem.tickets");
		sectionKey = safeGet(properties, "problem.section");

		String versionValue = safeGet(properties, "version");

		if (versionValue.isEmpty()) {
			version = versionValue;
		}
		else {
			VersionRange versionRange = new VersionRange(versionValue);

			Version left = versionRange.getLeft();

			version = left.getMajor() + "." + left.getMinor();
		}
	}

	@Override
	public List<UpgradeProblem> analyze(File file) {
		List<UpgradeProblem> problems = new ArrayList<>();

		List<FileSearchResult> results = searchFile(file);

		if ((results != null) && !results.isEmpty()) {
			for (FileSearchResult searchResult : results) {
				problems.add(
					new UpgradeProblem(
						problemTitle, problemSummary, problemType, problemTickets, version, searchResult.file,
						searchResult.startLine, searchResult.startOffset, searchResult.endOffset, "",
						"delete-legacy-file", UpgradeProblem.STATUS_NOT_RESOLVED, UpgradeProblem.DEFAULT_MARKER_ID,
						UpgradeProblem.MARKER_ERROR));
			}
		}

		return problems;
	}

	public List<FileSearchResult> findLegacyFiles(List<File> legacyFiles) {
		List<FileSearchResult> fileSearchResult = new ArrayList<>();

		if (!legacyFiles.isEmpty()) {
			Stream<File> legacyFilesStream = legacyFiles.stream();

			fileSearchResult.addAll(
				legacyFilesStream.filter(
					file -> file.isFile()
				).map(
					this::_getFileSearchResult
				).collect(
					Collectors.toList()
				));

			legacyFilesStream = legacyFiles.stream();

			fileSearchResult.addAll(
				legacyFilesStream.filter(
					file -> file.isDirectory()
				).map(
					dir -> _getAllFiles(dir)
				).flatMap(
					files -> files.stream()
				).map(
					this::_getFileSearchResult
				).collect(
					Collectors.toList()
				));
		}

		return fileSearchResult;
	}

	@Override
	public int reportProblems(File file, Collection<UpgradeProblem> upgradeProblems) {
		return 0;
	}

	protected abstract List<FileSearchResult> searchFile(File file);

	protected final List<String> artifactIds = new ArrayList<>();
	protected ComponentContext context;
	protected String fileExtension;
	protected String problemSummary;
	protected String problemTickets;
	protected String problemTitle;
	protected String problemType;
	protected String sectionKey = "";
	protected String version = "";

	private List<File> _getAllFiles(File folder) {
		List<File> fileList = new ArrayList<>();

		File[] members = folder.listFiles();

		for (File member : members) {
			if (member.isFile()) {
				fileList.add(member);
			}
			else if (member.isDirectory()) {
				fileList.addAll(_getAllFiles(member));
			}
		}

		return fileList;
	}

	private FileSearchResult _getFileSearchResult(File legacyFile) {
		int startOffset = 0;
		int endOffset = (int)legacyFile.length();

		int startLine = 1;

		int endLine = startLine;

		try (FileReader fileReader = new FileReader(legacyFile);
			LineNumberReader lineNumberReader = new LineNumberReader(fileReader)) {

			lineNumberReader.skip(Long.MAX_VALUE);

			endLine = lineNumberReader.getLineNumber() + 1;
		}
		catch (Exception e) {
		}

		return new FileSearchResult(legacyFile, startOffset, endOffset, startLine, endLine, true);
	}

}