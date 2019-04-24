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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.SourceFile;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Path;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Gregory Amerson
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
		version = safeGet(properties, "version");

		workspaceFile = new WorkspaceFile();
	}

	@Override
	public List<UpgradeProblem> analyze(File file) {
		List<UpgradeProblem> problems = new ArrayList<>();

		String fileExtension = new Path(
			file.getAbsolutePath()
		).getFileExtension();

		List<FileSearchResult> searchResults = searchFile(file, createFileChecker(type, file, fileExtension));

		if (ListUtil.isNotEmpty(searchResults)) {
			String fileName = "BREAKING_CHANGES.markdown";

			if ("7.0".equals(version)) {
				fileName = "liferay70/" + fileName;
			}
			else if ("7.1".equals(version)) {
				fileName = "liferay71/" + fileName;
			}

			String sectionHtml = MarkdownParser.getSection(fileName, sectionKey);

			if (CoreUtil.isNullOrEmpty(sectionHtml) || sectionHtml.equals("#legacy")) {
				sectionHtml = problemSummary;
			}

			for (FileSearchResult searchResult : searchResults) {
				if (searchResult != null) {
					problems.add(
						new UpgradeProblem(
							problemTitle, problemSummary, fileExtension, problemTickets, version,
							workspaceFile.getIFile(file), searchResult.startLine, searchResult.startOffset,
							searchResult.endOffset, sectionHtml, searchResult.autoCorrectContext,
							UpgradeProblem.STATUS_NOT_RESOLVED, UpgradeProblem.DEFAULT_MARKER_ID,
							UpgradeProblem.MARKER_ERROR));
				}
			}
		}

		return problems;
	}

	protected T createFileChecker(Class<T> type, File file, String fileExtension) {
		try {
			Collection<ServiceReference<T>> refs = context.getServiceReferences(
				type, "(file.extension=" + fileExtension + ")");

			if (ListUtil.isNotEmpty(refs)) {
				Iterator<ServiceReference<T>> iterator = refs.iterator();

				T service = context.getService(iterator.next());

				T fileCheckerFile = type.cast(service);

				if (fileCheckerFile == null) {
					throw new IllegalArgumentException(
						"Could not find " + type.getSimpleName() + " service for specified file " + file.getName());
				}
				else {
					fileCheckerFile.setFile(file);
				}

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
	protected WorkspaceFile workspaceFile;

}