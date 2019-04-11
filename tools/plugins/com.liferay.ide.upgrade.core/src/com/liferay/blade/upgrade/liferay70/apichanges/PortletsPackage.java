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

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf", "problem.title=Changed Java Package Names for Portlets Extracted as Modules",
		"problem.summary=The Java package names changed for portlets that were extracted as OSGi modules in 7.0.",
		"problem.tickets=LPS-56383", "problem.section=#changed-java-package-names-for-portlets-extracted-as-modules",
		"version=7.0"
	},
	service = FileMigrator.class
)
public class PortletsPackage extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> searchResults = new ArrayList<>();

		for (String packageName : _PACKAGES) {
			SearchResult packageResult = javaFileChecker.findPackage(packageName);

			if (packageResult != null) {
				searchResults.add(packageResult);
			}
		}

		return searchResults;
	}

	private static final String[] _PACKAGES = {
		"com.liferay.portlet.bookmarks.service.persistence", "com.liferay.portlet.dynamicdatalists.service.persistence",
		"com.liferay.portlet.journal.service.persistence", "com.liferay.portlet.polls.service.persistence",
		"com.liferay.portlet.wiki.service.persistence"
	};

}