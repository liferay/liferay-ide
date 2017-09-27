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
		"problem.summary=The getEntries method was no longer used, and contained hardcoded references to classes that will be moved into OSGi bundles.",
		"problem.tickets=LPS-56247",
		"problem.title=Removed Method getEntries from DL, DLImpl, and DLUtil Classes",
		"problem.section=#removed-method-getentries-from-dl-dlimpl-and-dlutil-classes",
		"implName=DLGetEntriesInvocation"
	},
	service = FileMigrator.class
)
public class DLGetEntriesInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		final List<SearchResult> searchResults = new ArrayList<SearchResult>();

		final String[] argTypes = new String[] { "Hits" };

		searchResults.addAll(javaFileChecker.findMethodInvocations("DL", null,
				"getEntries", argTypes));

		searchResults.addAll(javaFileChecker.findMethodInvocations(null, "DL",
				"getEntries", argTypes));

		searchResults.addAll(javaFileChecker.findMethodInvocations("DLImpl",
				null, "getEntries", null));

		searchResults.addAll(javaFileChecker.findMethodInvocations(null,
				"DLImpl", "getEntries", argTypes));

		searchResults.addAll(javaFileChecker.findMethodInvocations("DLUtil",
				null, "getEntries", argTypes));

		searchResults.addAll(javaFileChecker.findMethodInvocations(null,
				"DLUtil", "getEntries", argTypes));

		return searchResults;
	}

}