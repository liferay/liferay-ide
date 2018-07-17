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

package com.liferay.blade.upgrade;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.MarkdownParser;

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

		problemTitle = (String)properties.get("problem.title");
		problemSummary = (String)properties.get("problem.summary");
		problemType = (String)properties.get("file.extensions");
		problemTickets = (String)properties.get("problem.tickets");
		sectionKey = (String)properties.get("problem.section");
		version = (String)properties.get("version");

		addPropertiesToSearch(this.properties);
	}

	@Override
	public List<Problem> analyze(File file) {
		List<Problem> problems = new ArrayList<>();

		PropertiesFileChecker propertiesFileChecker = new PropertiesFileChecker(file);

		for (String key : properties) {
			List<SearchResult> results = propertiesFileChecker.findProperties(key);

			if (results != null) {
				String fileName = "BREAKING_CHANGES.markdown";

				if ("7.0".equals(version)) {
					fileName = "liferay70/" + fileName;
				}
				else if ("7.1".equals(version)) {
					fileName = "liferay71/" + fileName;
				}

				String sectionHtml = MarkdownParser.getSection(fileName, sectionKey);

				for (SearchResult searchResult : results) {
					problems.add(
						new Problem(
							problemTitle, problemSummary, problemType, problemTickets, file, searchResult.startLine,
							searchResult.startOffset, searchResult.endOffset, sectionHtml,
							searchResult.autoCorrectContext, Problem.STATUS_NOT_RESOLVED, Problem.DEFAULT_MARKER_ID,
							Problem.MARKER_ERROR));
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

}