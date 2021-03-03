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
import com.liferay.ide.upgrade.problems.core.internal.PropertiesFileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Seiphon Wang
 */
@Component(
	property = {
		"file.extensions=properties",
		"problem.title=Removed Portal Property user.groups.copy.layouts.to.user.personal.site",
		"problem.summary=The portal property user.groups.copy.layouts.to.user.personal.site and the behavior associated with it were removed.",
		"problem.tickets=LPS-106339",
		"problem.section=#removed-portal-property-user-groups-copy-layouts-to-user-personal-site",
		"problem.version=7.3", "version=7.3"
	},
	service = FileMigrator.class
)
public class RemovedCopyLayoutsProperty extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("user.groups.copy.layouts.to.user.personal.site");
	}

}