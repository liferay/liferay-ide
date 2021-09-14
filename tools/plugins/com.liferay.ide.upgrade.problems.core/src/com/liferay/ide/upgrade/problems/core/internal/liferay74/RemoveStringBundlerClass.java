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
 * @author Ethan Sun
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf", "problem.tickets=LPS-133200",
		"problem.title=Class com.liferay.portal.kernel.util.StringBundler has been deprecated",
		"problem.summary=The StringBundler class in package com.liferay.portal.kernel.util were removed",
		"problem.section=#removed-string-bundler-class", "version=7.4"
	},
	service = FileMigrator.class
)
public class RemoveStringBundlerClass extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<FileSearchResult> results = new ArrayList<>();

		FileSearchResult searchResult = javaFileChecker.findImport("com.liferay.portal.kernel.util.StringBundler");

		if (searchResult != null) {
			results.add(searchResult);
		}

		return results;
	}

}