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

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.XMLFile;
import com.liferay.ide.upgrade.problems.core.internal.XMLFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=xml", "problem.title=Deprecated Category Entry for Users",
		"problem.summary=The category entry for Site Administration > Users has been deprecated in favor of Site Administration > Members.",
		"problem.tickets=LPS-63466", "problem.section=#deprecated-category-entry-for-users", "version=7.0"
	},
	service = FileMigrator.class
)
public class CategoryEntryforUsers extends XMLFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, XMLFile xmlFileChecker) {
		if (!Objects.equals("liferay-portlet.xml", file.getName())) {
			return Collections.emptyList();
		}

		List<FileSearchResult> results = new ArrayList<>();

		results.addAll(xmlFileChecker.findElement("control-panel-entry-category", "site_administration.users"));

		return results;
	}

}