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
import com.liferay.blade.upgrade.liferay70.JSPTagMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=jsp,jspf", "problem.title=Custom AUI Validators Are No Longer Implicitly Required",
	"problem.section=#custom-aui-validators-are-no-longer-implicitly-required",
	"problem.summary=The AUI Validator tag no longer forces custom validators to be required",
	"problem.tickets=LPS-60995", "implName=CustomAUIValidatorTags"
},
	service = FileMigrator.class)
public class CustomAUIValidatorTags extends JSPTagMigrator {

	public CustomAUIValidatorTags() {
		super(_ATTR_NAMES, new String[0], _ATTR_VALUES, new String[0], _TAG_NAMES, new String[0]);
	}

	private static final String[] _ATTR_NAMES = {"name"};

	private static final String[] _ATTR_VALUES = {"custom"};

	private static final String[] _TAG_NAMES = {"aui:validator"};

}