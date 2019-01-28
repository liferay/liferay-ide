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

import com.liferay.ide.upgrade.plan.tasks.core.internal.problem.upgrade.JSPTagMigrator;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.FileMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=jsp,jspf",
	"problem.title=Removed mbMessages and fileEntryTuples Attributes from app-view-search-entry Tag",
	"problem.section=#removed-mbmessages-and-fileentrytuples-attributes-from-app-view-search-entr",
	"problem.summary=Removed mbMessages and fileEntryTuples Attributes from app-view-search-entry Tag",
	"problem.tickets=LPS-55886", "implName=AppViewSearchEntryTags", "version=7.0"
},
	service = FileMigrator.class)
public class AppViewSearchEntryTags extends JSPTagMigrator {

	public AppViewSearchEntryTags() {
		super(_ATTR_NAMES, new String[0], new String[0], new String[0], _TAG_NAMES, new String[0]);
	}

	private static final String[] _ATTR_NAMES = {"mbMessages", "fileEntryTuples"};

	private static final String[] _TAG_NAMES = {"liferay-ui:app-view-search-entry"};

}