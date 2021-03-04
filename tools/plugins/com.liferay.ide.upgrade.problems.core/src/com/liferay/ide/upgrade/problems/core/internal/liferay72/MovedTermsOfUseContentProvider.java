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

package com.liferay.ide.upgrade.problems.core.internal.liferay72;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Seiphon Wang
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf", "problem.title=Moved TermsOfUseContentProvider out of kernel.util",
		"problem.summary=The TermsOfUseContentProvider interface's package changed.", "problem.tickets=LPS-88869",
		"problem.section=#moved-termsofusecontentprovider-out-of-kernel-uitl", "version=7.2"
	},
	service = FileMigrator.class
)
public class MovedTermsOfUseContentProvider extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile fileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "TermsOfUseContentProviderRegistryUtil", "*", null));
		searchResults.add(
			fileChecker.findImport("com.liferay.portal.kernel.util.TermsOfUseContentProviderRegistryUtil"));
		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "TermsOfUseContentProvider", "getClassName", null));
		searchResults.add(fileChecker.findImport("com.liferay.portal.kernel.util.TermsOfUseContentProvider"));

		return searchResults;
	}

}