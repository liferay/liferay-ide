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

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.liferay70.JSPFileMigrator;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=jsp,jspf",
		"problem.title=Deprecated the liferay-ui:captcha Tag and Replaced with liferay-captcha:captcha",
		"problem.section=#deprecated-the-liferay-uicaptcha-tag-and-replaced-with-liferay-captchacaptc",
		"problem.summary=Deprecated the liferay-ui:captcha Tag and Replaced with liferay-captcha:captcha",
		"problem.tickets=LPS-69383",
		"auto.correct=jsptag",
		"implName=DeprecatedLiferayUICaptchaTags"
	},
	service = {
		AutoMigrator.class,
		FileMigrator.class
	}
)
public class DeprecatedLiferayUICaptchaTags extends JSPFileMigrator {

	public DeprecatedLiferayUICaptchaTags() {
		super(new String[0], new String[0], new String[0], new String[0], _tagNames, _newTagNames);
	}

	private static final String[] _tagNames = new String[] { "liferay-ui:captcha" };
	private static final String[] _newTagNames = new String[] { "liferay-captcha:captcha" };

}
