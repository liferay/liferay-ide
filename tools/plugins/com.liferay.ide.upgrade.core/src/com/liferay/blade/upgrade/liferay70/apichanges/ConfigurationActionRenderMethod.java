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
		"file.extensions=java",
		"problem.summary=Removed render Method from ConfigurationAction API",
		"problem.tickets=LPS-56300",
		"problem.title=ConfigurationAction render method",
		"problem.section=#removed-render-method-from-configurationaction-api",
		"implName=ConfigurationActionRenderMethod"
	},
	service = FileMigrator.class
)
public class ConfigurationActionRenderMethod extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		final List<SearchResult> searchResults = new ArrayList<>();

		// render method declarations
		List<SearchResult> declarations = javaFileChecker.findMethodDeclaration("render",
				new String[] { "PortletConfig", "RenderRequest", "RenderResponse" }, null);

		searchResults.addAll(declarations);

		// render method invocations
		List<SearchResult> invocations = javaFileChecker.findMethodInvocations(
				"ConfigurationAction", null, "render", null);

		searchResults.addAll(invocations);

		return searchResults;
	}
}