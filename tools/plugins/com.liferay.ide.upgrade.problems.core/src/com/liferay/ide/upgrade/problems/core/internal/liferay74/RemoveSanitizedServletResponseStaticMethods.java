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
		"file.extensions=java,jsp,jspf",
		"problem.title=Remove some static methods in com.liferay.portal.kernel.servlet.SanitizedServletResponse and Portal Property http.header.secure.x.xss.protection",
		"problem.summary=Some static methods in `com.liferay.portal.kernel.servlet.SanitizedServletResponse` have been removed because these relate to the X-Xss-Protection header which is not supported by modern browsers",
		"problem.tickets=LPS-134188", "problem.section=#remove-sanitized-servlet-response-some-static-method",
		"version=7.4"
	},
	service = FileMigrator.class
)
public class RemoveSanitizedServletResponseStaticMethods extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "SanitizedServletResponse", "disableXSSAuditor",
				new String[] {"javax.servlet.http.HttpServletResponse"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "SanitizedServletResponse", "disableXSSAuditor", new String[] {"javax.portlet.PortletResponse"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "SanitizedServletResponse", "disableXSSAuditorOnNextRequest",
				new String[] {"javax.servlet.http.HttpServletRequest"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "SanitizedServletResponse", "disableXSSAuditorOnNextRequest",
				new String[] {"javax.portlet.PortletRequest"}));

		return searchResults;
	}

}