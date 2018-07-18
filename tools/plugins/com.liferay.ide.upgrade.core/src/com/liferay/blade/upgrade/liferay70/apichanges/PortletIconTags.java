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
import com.liferay.blade.upgrade.JSPTagMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=jsp,jspf", "problem.title=Removed the Tags that Start with portlet:icon-",
	"problem.section=#removed-the-tags-that-start-with-portleticon-",
	"problem.summary=Removed the Tags that Start with portlet:icon-", "problem.tickets=LPS-54620",
	"implName=PortletIconTags", "version=7.0"
},
	service = FileMigrator.class)
public class PortletIconTags extends JSPTagMigrator {

	public PortletIconTags() {
		super(new String[0], new String[0], new String[0], new String[0], _TAG_NAMES, new String[0]);
	}

	private static final String[] _TAG_NAMES = {
		"liferay-portlet:icon-close", "liferay-portlet:icon-configuration", "liferay-portlet:icon-edit",
		"liferay-portlet:icon-edit-defaults", "liferay-portlet:icon-edit-guest", "liferay-portlet:icon-export-import",
		"liferay-portlet:icon-help", "liferay-portlet:icon-maximize", "liferay-portlet:icon-minimize",
		"liferay-portlet:icon-portlet-css", "liferay-portlet:icon-print", "liferay-portlet:icon-refresh",
		"liferay-portlet:icon-staging"
	};

}