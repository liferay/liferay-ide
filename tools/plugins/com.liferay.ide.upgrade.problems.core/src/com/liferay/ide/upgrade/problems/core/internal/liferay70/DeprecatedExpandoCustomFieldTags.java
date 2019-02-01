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

package com.liferay.ide.upgrade.problems.core.internal.liferay70;

import com.liferay.ide.upgrade.problems.core.AutoMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.JSPTagMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=jsp,jspf", "problem.title=Moved the Expando Custom Field Tags to liferay-expando Taglib",
	"problem.section=#moved-the-expando-custom-field-tags-to-liferay-expando-taglib",
	"problem.summary=Moved the Expando Custom Field Tags to liferay-expando Taglib", "problem.tickets=LPS-69400",
	"auto.correct=jsptag", "implName=DeprecatedExpandoCustomFieldTags", "version=7.0"
},
	service = {AutoMigrator.class, FileMigrator.class})
public class DeprecatedExpandoCustomFieldTags extends JSPTagMigrator {

	public DeprecatedExpandoCustomFieldTags() {
		super(new String[0], new String[0], new String[0], new String[0], _TAG_NAMES, _NEW_TAG_NAMES);
	}

	private static final String[] _NEW_TAG_NAMES = {
		"liferay-expando:custom-attribute", "liferay-expando:custom-attribute-list",
		"liferay-expando:custom-attributes-available"
	};

	private static final String[] _TAG_NAMES =
		{"liferay-ui:custom-attribute", "liferay-ui:custom-attribute-list", "liferay-ui:custom-attributes-available"};

}