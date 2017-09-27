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
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

import java.io.File;
import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.summary=All Microblogs APIs previously exposed as Liferay Portal API in 6.2 have been move out from portal-service into separate OSGi modules",
		"problem.tickets=LPS-56041",
		"problem.title=Microblogs APIs migrated to OSGi module",
		"problem.section=#legacy",
		"implName=MicroblogsLegacyAPI"
	},
	service = FileMigrator.class
)
public class MicroblogsLegacyAPI extends JavaFileMigrator {

	private static final String[] SERVICE_API_PREFIXES =  {
		"com.liferay.microblogs.service.MicroblogsEntry",
	};

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findServiceAPIs(SERVICE_API_PREFIXES);
	}
}