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

package com.liferay.ide.upgrade.plan.tasks.core.internal.problem.upgrade.liferay70.apichanges;

import com.liferay.ide.upgrade.plan.tasks.core.internal.problem.upgrade.XMLFileMigrator;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.FileMigrator;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.SearchResult;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.XMLFile;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=xml", "problem.title=Deprecated Category Entry for Pages",
	"problem.summary=The category entry for Site Administration > Pages has been deprecated in favor of Site " +
		"Administration > Navigation.",
	"problem.tickets=LPS-63667", "problem.section=#deprecated-category-entry-for-pages",
	"implName=CategoryEntryforPages", "version=7.0"
},
	service = FileMigrator.class)
public class CategoryEntryforPages extends XMLFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, XMLFile xmlFileChecker) {
		if (!"liferay-portlet.xml".equals(file.getName())) {
			return Collections.emptyList();
		}

		List<SearchResult> results = new ArrayList<>();

		results.addAll(xmlFileChecker.findElement("control-panel-entry-category", "site_administration.pages"));

		return results;
	}

}