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
 * @author Ethan Sun
 */
@Component(
	property = {
		"file.extensions=properties", "problem.title=Server-side Parallel Rendering Is No Longer Supported",
		"problem.summary=Properties with the prefix layout.parallel.render were removed, which means parallel rendering is only supported when AJAX rendering is enabled.",
		"problem.tickets=LPS-110359", "problem.section=#removed-portal-property-layout-parallel-render",
		"problem.version=7.3", "version=7.3"
	},
	service = FileMigrator.class
)
public class RemovedLayoutParallelRenderProperty extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("layout.parallel.render.*");
	}

}