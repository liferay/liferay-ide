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
import com.liferay.ide.upgrade.problems.core.XMLFile;
import com.liferay.ide.upgrade.problems.core.internal.XMLFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

/**
 * @author Seiphon Wang
 */
@Component(
	property = {
		"file.extensions=xml",
		"problem.summary=Themes can no longer leverage JSP templates. Also, related logic has been removed from the public APIs com.liferay.portal.kernel.util.ThemeHelper and com.liferay.taglib.util.ThemeUtil.",
		"problem.tickets=LPS-87064", "problem.title=Removed Support for JSP Templates in Themes",
		"problem.section=#removed-support-for-jsp-templates-in-themes", "version=7.2"
	},
	service = FileMigrator.class
)
public class RemovedSupportForJSPTemplates extends XMLFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, XMLFile xmlFileChecker) {
		List<FileSearchResult> results = new ArrayList<>();

		results.addAll(xmlFileChecker.findElement("template-extension", "jsp"));
		results.addAll(xmlFileChecker.findElement("liferay.theme.type", "jsp"));
		results.addAll(xmlFileChecker.findElementAttribute("property", _pattern));

		return results;
	}

	private static final Pattern _pattern = Pattern.compile("jsp", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

}