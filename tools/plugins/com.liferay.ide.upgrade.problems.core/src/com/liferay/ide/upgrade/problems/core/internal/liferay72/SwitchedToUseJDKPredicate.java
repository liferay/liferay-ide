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
		"file.extensions=java,jsp,jspf", "problem.title=Switched to Use JDK Predicate",
		"problem.summary=The interface com.liferay.portal.kernel.util.PredicateFilter was removed and replaced with java.util.function.Predicate. And these implementations were removed. The com.liferay.portal.kernel.util.ArrayUtil_IW class was regenerated.",
		"problem.tickets=LPS-89139", "problem.section=#switched-to-use-jdk-predicate", "version=7.2"
	},
	service = FileMigrator.class
)
public class SwitchedToUseJDKPredicate extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile fileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		searchResults.addAll(fileChecker.findImports(_imports));

		searchResults.addAll(fileChecker.findImplementsInterface("PredicateFilter"));

		return searchResults;
	}

	private static String[] _imports = {
		"com.liferay.portal.kernel.util.PredicateFilter", "com.liferay.portal.kernel.util.AggregatePredicateFilter",
		"com.liferay.portal.kernel.util.PrefixPredicateFilter",
		"com.liferay.portal.kernel.portlet.JavaScriptPortletResourcePredicateFilter",
		"com.liferay.dynamic.data.mapping.form.values.query.internal.model.DDMFormFieldValuePredicateFilter"
	};

}