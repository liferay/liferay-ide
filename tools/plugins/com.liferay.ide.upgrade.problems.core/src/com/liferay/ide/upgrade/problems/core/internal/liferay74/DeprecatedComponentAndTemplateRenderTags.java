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
		"file.extensions=jsp,jspf", "problem.summary= Deprecate soy:component and soy:template renderer tags",
		"problem.tickets=LPS-122966", "problem.title=Deprecate soy:component and soy:template renderer tags",
		"problem.section=#server-side-closure-templates-support-has-been-removed", "problem.version=7.4", "version=7.4"
	},
	service = FileMigrator.class
)
public class DeprecatedComponentAndTemplateRenderTags extends JSPTagMigrator {

	public DeprecatedComponentAndTemplateRenderTags() {
		super(new String[0], new String[0], new String[0], new String[0], _TAG_NAMES, new String[0]);
	}

	private static final String[] _TAG_NAMES = {"soy:template-renderer", "soy:component-renderer"};

}