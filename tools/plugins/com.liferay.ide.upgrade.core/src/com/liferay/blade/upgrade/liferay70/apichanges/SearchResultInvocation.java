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
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.title=Methods removed in SearchResult API",
		"problem.section=#removed-mbmessages-and-fileentrytuples-attributes-from-app-view-search-entr",
		"problem.summary=Removed getMbMessages , getFileEntryTuples and addMbMessage Methods from SearchResult Class",
		"problem.tickets=LPS-55886",
		"implName=SearchResultInvocation"
	},
	service = FileMigrator.class
)
public class SearchResultInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		final List<SearchResult> results = new ArrayList<SearchResult>();

		results.addAll(javaFileChecker.findMethodInvocations("SearchResult",
				null, "getMBMessages", null));

		results.addAll(javaFileChecker.findMethodInvocations("SearchResult",
				null, "getFileEntryTuples", null));

		results.addAll(javaFileChecker.findMethodInvocations("SearchResult",
				null, "addMBMessage", new String[] { "MBMessage" }));

		return results;
	}

}
