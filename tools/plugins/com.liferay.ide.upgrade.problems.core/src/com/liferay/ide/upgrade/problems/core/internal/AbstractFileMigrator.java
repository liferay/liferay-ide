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
import com.liferay.ide.upgrade.problems.core.SourceFile;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Path;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public abstract class AbstractFileMigrator<T extends SourceFile> implements FileMigrator {

	public AbstractFileMigrator(Class<T> type) {
		this.type = type;
	}

	@Activate
	public void activate(ComponentContext ctx) {
		context = ctx.getBundleContext();

		Dictionary<String, Object> properties = ctx.getProperties();

		String fileExtensionsValue = safeGet(properties, "file.extensions");

		fileExtentions = Arrays.asList(fileExtensionsValue.split(","));

		problemTitle = safeGet(properties, "problem.title");
		problemSummary = safeGet(properties, "problem.summary");
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

		Path path = new Path(file.getAbsolutePath());

		String fileExtension = path.getFileExtension();

		List<FileSearchResult> searchResults = searchFile(file, createFileService(type, file, fileExtension));

		if (!searchResults.isEmpty()) {
			String fileName = "BREAKING_CHANGES.markdown";

			switch (version) {
				case "7.0":
					fileName = "liferay70/" + fileName;

					break;
				case "7.1":
					fileName = "liferay71/" + fileName;

					break;
				case "7.2":
					fileName = "liferay72/" + fileName;

					break;
				case "7.3":
					fileName = "liferay73/" + fileName;

					break;
				case "7.4":
					fileName = "liferay74/" + fileName;

					break;
				default:
					Optional<String> nullableVersion = Optional.ofNullable(version);

					throw new RuntimeException("Missing version information: " + nullableVersion.orElse("<null>"));
			}

			String sectionHtml = MarkdownParser.getSection(fileName, sectionKey);

			if (Objects.equals(sectionHtml, "#legacy")) {
				sectionHtml = problemSummary;
			}

			for (FileSearchResult searchResult : searchResults) {
				if (searchResult != null) {
					problems.add(
						new UpgradeProblem(
							problemTitle, problemSummary, fileExtension, problemTickets, version, file,
							searchResult.startLine, searchResult.startOffset, searchResult.endOffset, sectionHtml,
							searchResult.autoCorrectContext, UpgradeProblem.STATUS_NOT_RESOLVED,
							UpgradeProblem.DEFAULT_MARKER_ID, UpgradeProblem.MARKER_ERROR));
				}
			}
		}

		return problems;
	}

	@Override
	public int reportProblems(File file, Collection<UpgradeProblem> upgradeProblems) {
		Path path = new Path(file.getAbsolutePath());

		SourceFile sourceFile = createFileService(type, file, path.getFileExtension());

		sourceFile.setFile(file);

		Stream<UpgradeProblem> upgradeProblemsStream = upgradeProblems.stream();

		return upgradeProblemsStream.map(
			problem -> {
				try {
					sourceFile.appendComment(problem.getLineNumber(), "FIXME: " + problem.getTitle());

					return 1;
				}
				catch (IOException ioe) {
					ioe.printStackTrace(System.err);
				}

				return 0;
			}
		).reduce(
			0, Integer::sum
		);
	}

	protected T createFileService(Class<T> type, File file, String fileExtension) {
		try {
			Collection<ServiceReference<T>> refs = context.getServiceReferences(
				type, "(file.extension=" + fileExtension + ")");

			if ((refs != null) && !refs.isEmpty()) {
				Iterator<ServiceReference<T>> iterator = refs.iterator();

				T service = context.getService(iterator.next());

				T fileCheckerFile = type.cast(service);

				if (fileCheckerFile == null) {
					throw new IllegalArgumentException(
						"Could not find " + type.getSimpleName() + " service for specified file " + file.getName());
				}

				fileCheckerFile.setFile(file);

				return fileCheckerFile;
			}
		}
		catch (InvalidSyntaxException ise) {
		}

		return null;
	}

	protected abstract List<FileSearchResult> searchFile(File file, T fileChecker);

	protected BundleContext context;
	protected List<String> fileExtentions;
	protected String problemSummary;
	protected String problemTickets;
	protected String problemTitle;
	protected String sectionKey;
	protected final Class<T> type;
	protected String version;

}