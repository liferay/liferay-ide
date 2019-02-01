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

package com.liferay.ide.upgrade.problems.core.internal.liferay70;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.ide.upgrade.plan.tasks.core.SearchResult;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=java,jsp,jspf",
	"problem.summary=The getEntries method was no longer used, and contained hardcoded references to classes that " +
		"will be moved into OSGi bundles.",
	"problem.tickets=LPS-56247", "problem.title=Removed Method getEntries from DL, DLImpl, and DLUtil Classes",
	"problem.section=#removed-method-getentries-from-dl-dlimpl-and-dlutil-classes", "implName=DLGetEntriesInvocation",
	"version=7.0"
},
	service = FileMigrator.class)
public class DLGetEntriesInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> searchResults = new ArrayList<>();

		String[] argTypes = {"Hits"};

		searchResults.addAll(javaFileChecker.findMethodInvocations("DL", null, "getEntries", argTypes));

		searchResults.addAll(javaFileChecker.findMethodInvocations(null, "DL", "getEntries", argTypes));

		searchResults.addAll(javaFileChecker.findMethodInvocations("DLImpl", null, "getEntries", null));

		searchResults.addAll(javaFileChecker.findMethodInvocations(null, "DLImpl", "getEntries", argTypes));

		searchResults.addAll(javaFileChecker.findMethodInvocations("DLUtil", null, "getEntries", argTypes));

		searchResults.addAll(javaFileChecker.findMethodInvocations(null, "DLUtil", "getEntries", argTypes));

		return searchResults;
	}

}