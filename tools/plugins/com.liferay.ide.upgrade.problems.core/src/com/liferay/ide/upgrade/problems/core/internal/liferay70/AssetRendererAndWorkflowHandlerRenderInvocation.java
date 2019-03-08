/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.upgrade.problems.core.internal.liferay70;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=java,jsp,jspf",
	"problem.summary=The method render has been removed from the interfaces AssetRenderer and WorkflowHandler.",
	"problem.tickets=LPS-56705", "problem.title=Removed render Method from AssetRenderer API and WorkflowHandler API",
	"problem.section=#removed-render-method-from-assetrenderer-api-and-workflowhandler-api",
	 "version=7.0"
},
	service = FileMigrator.class)
public class AssetRendererAndWorkflowHandlerRenderInvocation extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();
		String[] assetRendererArgTypes = {"RenderRequest", "RenderResponse", "String"};
		String[] workflowHandlerArgTypes = {"long", "RenderRequest", "RenderResponse", "String"};

		// render method declarations

		List<FileSearchResult> declarations = javaFileChecker.findMethodDeclaration("render", assetRendererArgTypes, null);

		searchResults.addAll(declarations);

		declarations = javaFileChecker.findMethodDeclaration("render", workflowHandlerArgTypes, null);

		searchResults.addAll(declarations);

		// render method invocations

		List<FileSearchResult> invocations = javaFileChecker.findMethodInvocations(
			"AssetRenderer", null, "render", assetRendererArgTypes);

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations("WorkflowHandler", null, "render", workflowHandlerArgTypes);

		searchResults.addAll(invocations);

		return searchResults;
	}

}