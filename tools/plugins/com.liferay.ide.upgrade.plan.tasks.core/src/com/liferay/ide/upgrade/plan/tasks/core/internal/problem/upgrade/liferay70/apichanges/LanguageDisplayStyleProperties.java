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

package com.liferay.ide.upgrade.plan.tasks.core.internal.problem.upgrade.liferay70.apichanges;

import com.liferay.ide.upgrade.plan.tasks.core.internal.problem.upgrade.PropertiesFileMigrator;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.FileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=properties", "problem.title=Language Display Style Properties",
	"problem.summary=Replaced the Language Portlet's Display Styles with ADTs", "problem.tickets=LPS-54419",
	"problem.section=#replaced-the-language-portlets-display-styles-with-adts",
	"implName=LanguageDisplayStyleProperties", "version=7.0"
},
	service = FileMigrator.class)
public class LanguageDisplayStyleProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("language.display.style.default");
		properties.add("language.display.style.options");
	}

}