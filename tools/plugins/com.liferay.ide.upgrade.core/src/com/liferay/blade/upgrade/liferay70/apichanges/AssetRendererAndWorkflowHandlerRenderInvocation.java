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
		"problem.summary=The method render has been removed from the interfaces AssetRenderer and WorkflowHandler.",
		"problem.tickets=LPS-56705",
		"problem.title=Removed render Method from AssetRenderer API and WorkflowHandler API",
		"problem.section=#removed-render-method-from-assetrenderer-api-and-workflowhandler-api",
		"implName=AssetRendererAndWorkflowHandlerRenderInvocation"
	},
	service = FileMigrator.class
)
public class AssetRendererAndWorkflowHandlerRenderInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {

		final List<SearchResult> searchResults = new ArrayList<SearchResult>();
		final String[] assetRendererArgTypes = new String[] { "RenderRequest",
				"RenderResponse", "String" };
		final String[] workflowHandlerArgTypes = new String[] { "long",
				"RenderRequest", "RenderResponse", "String" };

		// render method declarations
		List<SearchResult> declarations = javaFileChecker
				.findMethodDeclaration("render", assetRendererArgTypes, null);
		searchResults.addAll(declarations);

		declarations = javaFileChecker.findMethodDeclaration("render",
				workflowHandlerArgTypes, null);
		searchResults.addAll(declarations);

		// render method invocations
		List<SearchResult> invocations = javaFileChecker.findMethodInvocations(
				"AssetRenderer", null, "render", assetRendererArgTypes);
		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations("WorkflowHandler",
				null, "render", workflowHandlerArgTypes);
		searchResults.addAll(invocations);

		return searchResults;
	}

}