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

package com.liferay.ide.upgrade.problems.core.internal.liferay74;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ethan Sun
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf", "problem.tickets=LPS-122955",
		"problem.title=Remove SoyPortlet and related code", "problem.summary=SoyPortlet is no longer available",
		"problem.section=#removed-soy-portlet", "problem.version=7.4", "version=7.4"
	},
	service = FileMigrator.class
)
public class RemovedSoyPortlet extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile fileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		searchResults.addAll(fileChecker.findImports(_imports));

		searchResults.addAll(fileChecker.findImplementsInterface("SoyPortletRegister"));

		searchResults.addAll(fileChecker.findMethodInvocations(null, "NPMResolverProvider", "getNPMResolver", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"SoyPortletRegister", null, "getJavaScriptRequiredModules", new String[] {"String"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"SoyPortlet", null, "init", new String[] {"javax.portlet.PortletConfig"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"SoyPortlet", null, "render",
				new String[] {"javax.portlet.RenderRequest", "javax.portlet.RenderResponse"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"SoyPortlet", null, "serveResource",
				new String[] {"javax.portlet.ResourceRequest", "javax.portlet.ResourceResponse"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"SoyPortletHelper", null, "getJavaScriptLoaderModule", new String[] {"String"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"SoyPortletHelper", null, "getRouterJavaScript",
				new String[] {"String", "String", "String", "String", "com.liferay.portal.kernel.template.Template"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"SoyPortletHelper", null, "serializeTemplate",
				new String[] {"com.liferay.portal.kernel.template.Template"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"SoyPortletRequestFactory", null, "createActionRequest",
				new String[] {"javax.portlet.ResourceRequest"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"SoyPortletRequestFactory", null, "createActionResponse",
				new String[] {"javax.portlet.ActionRequest", "javax.portlet.ResourceResponse"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"SoyPortletRequestFactory", null, "createRenderRequest",
				new String[] {"javax.portlet.ResourceRequest", "javax.portlet.ResourceResponse"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"SoyPortletRequestFactory", null, "createRenderResponse",
				new String[] {"javax.portlet.RenderRequest", "javax.portlet.ResourceResponse"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "SoyTemplateResourceFactoryUtil", "createSoyTemplateResource",
				new String[] {"List<com.liferay.portal.kernel.template.TemplateResource>"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "SoyTemplateResourcesProviderUtil", "getBundleTemplateResources",
				new String[] {"org.osgi.framework.Bundle", "String"}));

		return searchResults;
	}

	private static String[] _imports = {
		"com.liferay.flags.taglib.internal.frontend.js.loader.modules.extender.npm.NPMResolverProvider",
		"com.liferay.flags.taglib.servlet.taglib.soy.FlagsTag",
		"com.liferay.portal.portlet.bridge.soy.SoyPortletRegister",
		"com.liferay.portal.portlet.bridge.soy.internal.SoyPortlet",
		"com.liferay.portal.portlet.bridge.soy.internal.SoyPortletHelper",
		"com.liferay.portal.portlet.bridge.soy.internal.SoyPortletRegisterTracker",
		"com.liferay.portal.portlet.bridge.soy.internal.SoyPortletRequestFactory",
		"com.liferay.portal.portlet.bridge.soy.internal.util.SoyTemplateResourceFactoryUtil",
		"com.liferay.portal.portlet.bridge.soy.internal.util.SoyTemplateResourcesProviderUtil"
	};

}