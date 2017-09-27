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
		"problem.title=Changed Java Package Names for Portlets Extracted as Modules",
		"problem.summary=The Java package names changed for portlets that were extracted as OSGi modules in 7.0.",
		"problem.tickets=LPS-56383",
		"problem.section=#changed-java-package-names-for-portlets-extracted-as-modules",
		"implName=PortletsPackage"
	},
	service = FileMigrator.class
)
public class PortletsPackage extends JavaFileMigrator {

	private final static String[] packages = new String[] {
		"com.liferay.portlet.bookmarks.service.persistence",
		"com.liferay.portlet.dynamicdatalists.service.persistence",
		"com.liferay.portlet.journal.service.persistence",
		"com.liferay.portlet.polls.service.persistence",
		"com.liferay.portlet.wiki.service.persistence"
	};

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		final List<SearchResult> searchResults = new ArrayList<>();

		for (String packageName : packages) {
			final SearchResult packageResult = javaFileChecker
					.findPackage(packageName);

			if (packageResult != null) {
				searchResults.add(packageResult);
			}
		}

		return searchResults;
	}

}
