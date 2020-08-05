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
 * @author Simon Jiang
 */
@Component(
	property = {
		"file.extensions=jsp,jspf", "problem.title=jQuery is no longer included by default",
		"problem.section=#jquery-is-no-longer-included-by-default",
		"problem.summary=jQuery Is No Longer Included by Default", "problem.tickets=LPS-95726", "version=7.3"
	},
	service = FileMigrator.class
)
public class JqueryNoLongerDefaultInclude extends JSPTagMigrator {

	public JqueryNoLongerDefaultInclude() {
		super(new String[0], new String[0], new String[0], new String[0], _TAG_NAMES, new String[0], _TAG_CONTENTS);
	}

	private static final String[] _TAG_CONTENTS = {"AUI.$", "window.$"};

	private static final String[] _TAG_NAMES = {"aui:script"};

}