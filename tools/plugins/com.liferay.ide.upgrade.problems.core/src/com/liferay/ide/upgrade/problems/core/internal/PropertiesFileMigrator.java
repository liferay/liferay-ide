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
import java.util.Dictionary;
import java.util.List;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Gregory Amerson
 */
public abstract class PropertiesFileMigrator implements FileMigrator {

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

		addPropertiesToSearch(this.properties);

		_workspaceFile = new WorkspaceFile();
	}

	@Override
	public List<UpgradeProblem> analyze(File file) {
		List<UpgradeProblem> problems = new ArrayList<>();

		PropertiesFileChecker propertiesFileChecker = new PropertiesFileChecker(file);

		for (String key : properties) {
			List<FileSearchResult> results = propertiesFileChecker.findProperties(key);

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
							problemTitle, problemSummary, problemType, problemTickets, version,
							_workspaceFile.getIFile(file), searchResult.startLine, searchResult.startOffset,
							searchResult.endOffset, sectionHtml, searchResult.autoCorrectContext,
							UpgradeProblem.STATUS_NOT_RESOLVED, UpgradeProblem.DEFAULT_MARKER_ID,
							UpgradeProblem.MARKER_ERROR));
				}
			}
		}

		return problems;
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

	private WorkspaceFile _workspaceFile;

}