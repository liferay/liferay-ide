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

package com.liferay.ide.upgrade.problems.core.internal.liferay71;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.JSPTagMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Haoyi Sun
 */
@Component(property = {
	"file.extensions=jsp,jspf",
	"problem.title=Changed Behavior of liferay-ui:input-date Taglib's showDisableCheckbox Argument",
	"problem.summary=Changed Tag Input Date Argument ShowDisableCheckBox", "problem.tickets=LPS-78475",
	"problem.section=#changed-tag-input-date-argument-showdisablecheckbox",
	"implName=ChangedShowDisableCheckboxBehavior", "version=7.1"
},
	service = FileMigrator.class)
public class ChangedShowDisableCheckboxBehavior extends JSPTagMigrator {

	public ChangedShowDisableCheckboxBehavior() {
		super(_ATTR_NAMES, new String[0], new String[0], new String[0], _TAG_NAMES, new String[0]);
	}

	private static final String[] _ATTR_NAMES = {"showDisableCheckbox"};

	private static final String[] _TAG_NAMES = {"liferay-ui:input-date"};

}