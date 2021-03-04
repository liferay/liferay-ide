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

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.PropertiesFileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=properties",
		"problem.title=Convert Process Classes do not support convert.processes Portal Property",
		"problem.summary=The implementation class com.liferay.portal.convert.ConvertProcess was renamed com.liferay.portal. convert.BaseConvertProcess. An interface named com.liferay.portal.convert.ConvertProcess was created for it. The convert.processes key was removed from portal.properties. Consequentially, ConvertProcess implementations must register as OSGi components.",
		"problem.tickets=LPS-50604",
		"problem.section=#convert-process-classes-are-no-longer-specified-via-the-convert-processes-p", "version=7.0"
	},
	service = FileMigrator.class
)
public class ConvertProcessProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("convert.processes");
	}

}