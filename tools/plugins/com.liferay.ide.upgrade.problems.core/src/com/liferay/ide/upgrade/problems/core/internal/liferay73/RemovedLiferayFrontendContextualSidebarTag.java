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

package com.liferay.ide.upgrade.problems.core.internal.liferay73;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.JSPTagMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Seiphon Wang
 */
@Component(
	property = {
		"file.extensions=jsp,jspf", "problem.title=Removed liferay-frontend:contextual-sidebar Tag",
		"problem.section=#removed-liferay-frontend-contextual-sidebar-tag",
		"problem.summary=The `liferay-frontend:contextual-sidebar` tag was removed.", "problem.tickets=LPS-100146",
		"version=7.3"
	},
	service = FileMigrator.class
)
public class RemovedLiferayFrontendContextualSidebarTag extends JSPTagMigrator {

	public RemovedLiferayFrontendContextualSidebarTag() {
		super(new String[0], new String[0], new String[0], new String[0], _TAG_NAMES, new String[0]);
	}

	private static final String[] _TAG_NAMES = {"liferay-frontend:contextual-sidebar"};

}