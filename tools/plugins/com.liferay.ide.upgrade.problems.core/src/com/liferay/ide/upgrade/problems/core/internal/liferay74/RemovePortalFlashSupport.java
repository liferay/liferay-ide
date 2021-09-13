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

package com.liferay.ide.upgrade.problems.core.internal.liferay74;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Simon Jiang
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf", "problem.tickets=LPS-121733", "problem.title=Removed the /portal/flash Path",
		"problem.summary=The goal of this task is to remove all Flash paths and any related supporting code in line with End of Support for Flash",
		"problem.section=#removed-portal-flash-support", "version=7.4"
	},
	service = FileMigrator.class
)
public class RemovePortalFlashSupport extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile fileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		searchResults.addAll(
			fileChecker.findMethodInvocations("ThemeDisplay", null, "setPathFlash", new String[] {"String"}));

		searchResults.addAll(fileChecker.findMethodInvocations("ThemeDisplay", null, "getPathFlash", null));

		return searchResults;
	}

}