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

import com.liferay.ide.upgrade.plan.tasks.core.internal.problem.upgrade.JavaFileMigrator;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.FileMigrator;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.JavaFile;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.SearchResult;

import java.io.File;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=java,jsp,jspf",
	"problem.title=Moved the AssetPublisherUtil Class and Removed It from the Public API",
	"problem.section=#moved-the-assetpublisherutil-class-and-removed-it-from-the-public-api",
	"problem.summary=Moved the AssetPublisherUtil Class and Removed It from the Public API",
	"problem.tickets=LPS-52744", "implName=AssetPublisherUtilInvocation", "version=7.0"
},
	service = FileMigrator.class)
public class AssetPublisherUtilInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findMethodInvocations(null, "AssetPublisherUtil", "*", null);
	}

}