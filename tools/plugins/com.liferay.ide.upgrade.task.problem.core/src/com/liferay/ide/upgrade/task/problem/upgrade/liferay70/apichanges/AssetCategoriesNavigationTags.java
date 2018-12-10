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

package com.liferay.ide.upgrade.task.problem.upgrade.liferay70.apichanges;

import com.liferay.ide.upgrade.task.problem.api.AutoMigrator;
import com.liferay.ide.upgrade.task.problem.api.FileMigrator;
import com.liferay.ide.upgrade.task.problem.upgrade.JSPTagMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=jsp,jspf",
	"problem.title=Removed the liferay-ui:asset-categories-navigation Tag and Replaced with liferay-asset:asset-cate" +
		"gories-navigation",
	"problem.section=#removed-the-liferay-uiasset-categories-navigation-tag-and-replaced-with-lif",
	"problem.summary=Removed the liferay-ui:asset-categories-navigation Tag and Replaced with liferay-asset:asset-" +
		"categories-navigation",
	"problem.tickets=LPS-60753", "auto.correct=jsptag", "implName=AssetCategoriesNavigationTags", "version=7.0"
},
	service = {AutoMigrator.class, FileMigrator.class})
public class AssetCategoriesNavigationTags extends JSPTagMigrator {

	public AssetCategoriesNavigationTags() {
		super(new String[0], new String[0], new String[0], new String[0], _TAG_NAMES, _NEW_TAG_NAMES);
	}

	private static final String[] _NEW_TAG_NAMES = {"liferay-asset:asset-categories-navigation"};

	private static final String[] _TAG_NAMES = {"liferay-ui:asset-categories-navigation"};

}