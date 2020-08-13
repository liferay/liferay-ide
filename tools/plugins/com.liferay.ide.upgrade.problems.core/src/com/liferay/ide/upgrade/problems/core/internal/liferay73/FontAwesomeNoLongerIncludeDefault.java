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
 * @author Ethan Sun
 */
@Component(
	property = {
		"file.extensions=jsp,jspf", "problem.title=Liferay FontAwesome Is No Longer Included by Default",
		"problem.section=#deprecated-the-iconCssClass-attr-and-replaced-with-icon-and-markupView",
		"problem.summary=Liferay FontAwesome Is No Longer Included by Default", "problem.tickets=LPS-100021",
		"version=7.3"
	},
	service = FileMigrator.class
)
public class FontAwesomeNoLongerIncludeDefault extends JSPTagMigrator {

	public FontAwesomeNoLongerIncludeDefault() {
		super(_ATTR_NAMES, _EMPTY, _EMPTY, _EMPTY, _TAG_NAMES, _EMPTY);
	}

	private static final String[] _ATTR_NAMES = {"iconCssClass"};

	private static final String[] _EMPTY = new String[0];

	private static final String[] _TAG_NAMES = {"liferay-ui:icon"};

}