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

import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.liferay70.JSPFileMigrator;

@Component(
	property = {
		"file.extensions=jsp,jspf",
		"problem.title=Renamed URI Attribute Used to Generate AUI Tag Library",
		"problem.section=#renamed-uri-attribute-used-to-generate-aui-tag-library",
		"problem.summary=We should use the new AUI URI declaration:http://liferay.com/tld/aui",
		"problem.tickets=LPS-57809",
		"auto.correct=jsptag",
		"implName=AUILiferayTags"
	},
	service = {
		AutoMigrator.class,
		FileMigrator.class
	}
)
public class AUILiferayTags extends JSPFileMigrator {

	@Override
	protected String[] getTagNames() {
		return new String[] {
			"jsp:directive.taglib"
		};
	}

	@Override
	protected String[] getAttrNames() {
		return new String[] {
			"uri"
		};
	}

	@Override
	protected String[] getAttrValues() {
		return new String[] {
			"http://alloy.liferay.com/tld/aui"
		};
	}

	@Override
	protected String[] getNewAttrValues() {
		return new String[] {
			"http://liferay.com/tld/aui"
		};
	}

}