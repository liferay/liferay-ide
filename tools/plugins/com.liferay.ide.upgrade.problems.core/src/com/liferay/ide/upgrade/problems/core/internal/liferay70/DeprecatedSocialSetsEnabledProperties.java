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

import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.PropertiesFileMigrator;

/**
 * @author Haoyi Sun
 */
@Component(property = {
	"file.extensions=properties", "problem.title=Social Activity Properties Deprecated",
	"problem.summary=Deprecated the social.activity.sets.enabled Property", "problem.tickets=LPS-63635",
	"problem.section=#deprecated-the-social-activity-sets-enabled-Property",
	 "version=7.0"
},
	service = FileMigrator.class)
public class DeprecatedSocialSetsEnabledProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("social.activity.sets.enabled");
	}

}