/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=properties",
		"problem.title=Convert Process Classes don't support convert.processes Portal Property",
		"problem.summary=The implementation class com.liferay.portal.convert.ConvertProcess was renamed com.liferay.portal.convert.BaseConvertProcess. "
                + "An interface named com.liferay.portal.convert.ConvertProcess was created for it."
                + " The convert.processes key was removed from portal.properties. "
                + "Consequentially, ConvertProcess implementations must register as OSGi components.",
		"problem.tickets=LPS-50604",
		"problem.section=#convert-process-classes-are-no-longer-specified-via-the-convert-processes-p",
		"implName=ConvertProcessProperties"
	},
	service = FileMigrator.class
)
public class ConvertProcessProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("convert.processes");
	}

}
