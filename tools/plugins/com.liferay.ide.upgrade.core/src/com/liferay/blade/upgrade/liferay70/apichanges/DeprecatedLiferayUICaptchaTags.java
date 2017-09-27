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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JSPFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JSPFileMigrator;

@Component(
	property = {
		"file.extensions=jsp,jspf",
		"problem.title=Deprecated the liferay-ui:captcha Tag and Replaced with liferay-captcha:captcha",
		"problem.section=#deprecated-the-liferay-uicaptcha-tag-and-replaced-with-liferay-captchacaptc",
		"problem.summary=Deprecated the liferay-ui:captcha Tag and Replaced with liferay-captcha:captcha",
		"problem.tickets=LPS-69383",
		"implName=DeprecatedLiferayUICaptchaTags"
	},
	service = FileMigrator.class
)
public class DeprecatedLiferayUICaptchaTags extends JSPFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JSPFile jspFileChecker) {
		List<SearchResult> result = new ArrayList<SearchResult>();

		result.addAll(jspFileChecker.findJSPTags("liferay-ui:captcha"));

		return result;
	}

}
