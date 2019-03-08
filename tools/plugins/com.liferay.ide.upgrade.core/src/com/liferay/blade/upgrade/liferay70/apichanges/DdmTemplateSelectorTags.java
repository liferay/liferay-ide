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

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.JSPTagMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=jsp,jspf", "problem.title=Changed Usage of the liferay-ui:ddm-template-selector Tag",
	"problem.section=#changed-usage-of-the-liferay-uiddm-template-selector-tag",
	"problem.summary=The attribute classNameId of the liferay-ui:ddm-template-selector taglib tag has been renamed c" +
		"lassName",
	"problem.tickets=LPS-53790", "auto.correct=jsptag", "version=7.0"
},
	service = {AutoMigrator.class, FileMigrator.class})
public class DdmTemplateSelectorTags extends JSPTagMigrator {

	public DdmTemplateSelectorTags() {
		super(_ATTR_NAMES, _NEWW_ATTR_NAMES, new String[0], new String[0], _TAG_NAMES, new String[0]);
	}

	private static final String[] _ATTR_NAMES = {"classNameId"};

	private static final String[] _NEWW_ATTR_NAMES = {"className"};

	private static final String[] _TAG_NAMES = {"liferay-ui:ddm-template-selector"};

}