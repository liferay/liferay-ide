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
		"file.extensions=java,jsp,jspf",
		"problem.title=Removed Unsafe Functional Interfaces in Package com.liferay.portal.kernel.util",
		"problem.summary=The com.liferay.portal.osgi.util.test.OSGiServiceUtil class was removed. Also, the following interfaces were removed from the com.liferay.portal.kernel.util package: UnsafeConsumer, UnsafeFunction, UnsafeRunnable.",
		"problem.tickets=LPS-89223",
		"problem.section=#removed-unsafe-functional-interfaces-in-package-com-liferay-portal-kernel-util",
		"problem.version=7.2", "version=7.2"
	},
	service = FileMigrator.class
)
public class RemovedUnsafeFunctionalInterface extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile fileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		searchResults.addAll(fileChecker.findMethodInvocations(null, "OSGiServiceUtil", "*", null));
		searchResults.add(fileChecker.findImport("com.liferay.portal.osgi.util.test.OSGiServiceUtil"));
		searchResults.addAll(fileChecker.findImports(_imports));

		return searchResults;
	}

	private static String[] _imports = {
		"com.liferay.portal.kernel.util.UnsafeConsumer", "com.liferay.portal.kernel.util.UnsafeFunction",
		"com.liferay.portal.kernel.util.UnsafeRunnable"
	};

}