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
import com.liferay.blade.upgrade.liferay70.JSPFileMigrator;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=jsp,jspf",
		"problem.title=Custom AUI Validators Are No Longer Implicitly Required",
		"problem.section=#custom-aui-validators-are-no-longer-implicitly-required",
		"problem.summary=The AUI Validator tag no longer forces custom validators to be required",
		"problem.tickets=LPS-60995",
		"implName=CustomAUIValidatorTags"
	},
	service = FileMigrator.class
)
public class CustomAUIValidatorTags extends JSPFileMigrator {

	public CustomAUIValidatorTags() {
		super(_attrNames, new String[0], _attrValues, new String[0], _tagNames, new String[0]);
	}

	private static final String[] _tagNames = new String[] { "aui:validator" };
	private static final String[] _attrNames = new String[] { "name" };
	private static final String[] _attrValues = new String[] { "custom" };

}