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

package com.liferay.ide.upgrade.plan.tasks.core.internal.problem.upgrade.liferay71.apichanges;

import com.liferay.ide.upgrade.plan.tasks.core.internal.problem.upgrade.JavaFileMigrator;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.FileMigrator;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.JavaFile;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.SearchResult;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Haoyi Sun
 */
@Component(property = {
	"file.extensions=java", "problem.title=Decoupled Several Classes from PortletURLImpl",
	"problem.summary=Decoupled Implementing BaseURL Classes", "problem.tickets=LPS-82119",
	"problem.section=#decoupled-implementing-baseurl-classes", "implName=DecoupledImplementingBaseURLClasses",
	"version=7.1"
},
	service = FileMigrator.class)
public class DecoupledImplementingBaseURLClasses extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile fileChecker) {
		List<SearchResult> searchResults = new ArrayList<>();

		List<SearchResult> subclasses = fileChecker.findSuperClass("PortletURLImplWrapper");

		searchResults.addAll(subclasses);

		subclasses = fileChecker.findSuperClass("StrutsActionPortletURL");

		searchResults.addAll(subclasses);

		subclasses = fileChecker.findSuperClass("LiferayStrutsPortletURLImpl");

		searchResults.addAll(subclasses);

		return searchResults;
	}

}