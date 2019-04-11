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
		"file.extensions=java,jsp,jspf", "problem.summary=Removed the WAP Functionality", "problem.tickets=LPS-62920",
		"problem.title=Removed the WAP Functionality", "problem.section=#removed-the-wap-functionality", "version=7.0"
	},
	service = FileMigrator.class
)
public class RemoveWapFunctionality extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> result = new ArrayList<>();

		result.addAll(javaFileChecker.findMethodInvocations(null, "LayoutLocalServiceUtil", "updateLookAndFeel", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(null, "LayoutRevisionLocalServiceUtil", "addLayoutRevision", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "LayoutRevisionLocalServiceUtil", "updateLayoutRevision", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(null, "LayoutRevisionServiceUtil", "addLayoutRevision", null));

		result.addAll(javaFileChecker.findMethodInvocations(null, "LayoutServiceUtil", "updateLookAndFeel", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(null, "LayoutSetLocalServiceUtil", "updateLookAndFeel", null));

		result.addAll(javaFileChecker.findMethodInvocations(null, "LayoutSetServiceUtil", "updateLookAndFeel", null));

		result.addAll(javaFileChecker.findMethodInvocations(null, "ThemeLocalServiceUtil", "getColorScheme", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(null, "ThemeLocalServiceUtil", "getControlPanelThemes", null));

		result.addAll(javaFileChecker.findMethodInvocations(null, "ThemeLocalServiceUtil", "getPageThemes", null));

		result.addAll(javaFileChecker.findMethodInvocations(null, "ThemeLocalServiceUtil", "getTheme", null));

		return result;
	}

}