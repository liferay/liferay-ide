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

package com.liferay.ide.upgrade.problems.core.internal.liferay74;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.JSPTagMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Seiphon Wang
 */
@Component(
	property = {
		"file.extensions=jsp,jspf", "problem.title=Previously unused and deprecated JSP tags are no longer available",
		"problem.section=#remove-deprecated-jsp-tags",
		"problem.summary=A series of deprecated and unused JSP tags have been removed and are no longer available",
		"problem.tickets=LPS-112476", "version=7.4"
	},
	service = FileMigrator.class
)
public class RemoveDeprecatedJSPTags extends JSPTagMigrator {

	public RemoveDeprecatedJSPTags() {
		super(new String[0], new String[0], new String[0], new String[0], _TAG_NAMES, new String[0]);
	}

	private static final String[] _TAG_NAMES = {
		"clay:table", "liferay-ui:alert", "liferay-ui:input-scheduler",
		"liferay-ui:organization-search-container-results", "liferay-ui:organization-search-form", "liferay-ui:ratings",
		"liferay-ui:search-speed", "liferay-ui:table-iterator", "liferay-ui:toggle-area", "liferay-ui:toggle",
		"liferay-ui:user-search-container-results", "liferay-ui:user-search-form", "liferay-theme:layout-icon",
		"liferay-theme:param"
	};

}