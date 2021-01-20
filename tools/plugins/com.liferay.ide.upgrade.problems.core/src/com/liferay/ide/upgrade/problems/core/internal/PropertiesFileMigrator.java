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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
import java.util.Optional;

import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public abstract class PropertiesFileMigrator implements FileMigrator {

	@Activate
	public void activate(ComponentContext ctx) {
		context = ctx;

		Dictionary<String, Object> serviceProperties = context.getProperties();

		problemTitle = safeGet(serviceProperties, "problem.title");
		problemSummary = safeGet(serviceProperties, "problem.summary");
		problemType = safeGet(serviceProperties, "file.extensions");
		problemTickets = safeGet(serviceProperties, "problem.tickets");
		sectionKey = safeGet(serviceProperties, "problem.section");

		String versionValue = safeGet(serviceProperties, "version");

		if (versionValue.isEmpty()) {
			version = versionValue;
		}
		else {
			VersionRange versionRange = new VersionRange(versionValue);

			Version left = versionRange.getLeft();

			version = left.getMajor() + "." + left.getMinor();
		}

		addPropertiesToSearch(properties);
	}

	@Override
	public List<UpgradeProblem> analyze(File file) {
		List<UpgradeProblem> problems = new ArrayList<>();

		PropertiesFileChecker propertiesFileChecker = new PropertiesFileChecker(file);

		for (String key : properties) {
			List<FileSearchResult> results = propertiesFileChecker.findProperties(key);

			if (!results.isEmpty()) {
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

	@Override
	public int reportProblems(File file, Collection<UpgradeProblem> upgradeProblems) {
		return 0;
	}

	protected abstract void addPropertiesToSearch(List<String> properties);

	protected ComponentContext context;
	protected String problemSummary;
	protected String problemTickets;
	protected String problemTitle;
	protected String problemType;
	protected final List<String> properties = new ArrayList<>();
	protected String sectionKey = "";
	protected String version = "";

}