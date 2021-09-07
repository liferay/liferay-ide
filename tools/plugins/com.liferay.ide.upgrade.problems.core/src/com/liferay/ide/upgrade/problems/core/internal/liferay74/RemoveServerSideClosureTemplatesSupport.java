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
 * @author Seiphon Wang
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.summary= Some modules and the classes they exported to allow Soy rendering server-side have been removed",
		"problem.tickets=LPS-122956", "problem.title=Removed Server-side Closure Templates (Soy) Support",
		"problem.section=#removed-server-side-closure-templates-support", "version=7.4"
	},
	service = FileMigrator.class
)
public class RemoveServerSideClosureTemplatesSupport extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<FileSearchResult> results = new ArrayList<>();

		results.add(javaFileChecker.findImport("com.liferay.flags.taglib.servlet.taglib.soy.FlagsTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.AlertTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.BadgeTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.ButtonTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.CheckboxTag"));
		results.add(
			javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.DropdownActionsTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.DropdownMenuTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.FileCardTag"));
		results.add(
			javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.HorizontalCardTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.IconTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.ImageCardTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.LabelTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.LinkTag"));
		results.add(
			javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.ManagementToolbarTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.MultiSelectTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.NavigationBarTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.ProgressBarTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.RadioTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.SelectTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.StickerTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.StripeTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.TableTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.UserCardTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.VerticalCardTag"));
		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.servlet.taglib.soy.BaseClayTag"));

		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.soy.servlet.taglib.ComponentRendererTag"));

		results.addAll(javaFileChecker.findSuperClass("ComponentRendererTag"));

		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.soy.servlet.taglib.TemplateRendererTag"));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.TemplateResource"));
		results.addAll(javaFileChecker.findImplementsInterface("TemplateResource"));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.SoyTemplateResourceFactory"));
		results.addAll(javaFileChecker.findImplementsInterface("SoyTemplateResourceFactory"));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.constants.SoyTemplateConstants"));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.data.SoyDataFactory"));
		results.addAll(javaFileChecker.findImplementsInterface("SoyDataFactory"));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.data.SoyRawData"));
		results.addAll(javaFileChecker.findImplementsInterface("SoyRawData"));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.util.SoyContext"));
		results.addAll(javaFileChecker.findImplementsInterface("SoyContext"));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.util.SoyContextFactory"));
		results.addAll(javaFileChecker.findImplementsInterface("SoyContextFactory"));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.util.SoyContextFactoryUtil"));
		results.addAll(javaFileChecker.findMethodInvocations(null, "SoyContextFactoryUtil", "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.util.SoyRawData"));
		results.addAll(javaFileChecker.findImplementsInterface("SoyRawData"));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.util.SoyTemplateResourcesProvider"));
		results.addAll(javaFileChecker.findImplementsInterface("SoyTemplateResourcesProvider"));

		results.add(
			javaFileChecker.findImport(
				"com.liferay.portal.template.soy.context.contributor.internal.template.ThemeDisplaySoyTemplateContext" +
					"Contributor"));
		results.addAll(
			javaFileChecker.findMethodInvocations("ThemeDisplaySoyTemplateContextContributor", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.SoyContextImpl"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyContextImpl", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.BaseTemplateManager"));
		results.addAll(javaFileChecker.findMethodInvocations("BaseTemplateManager", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.SoyManagerCleaner"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyManagerCleaner", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.SoyMsgBundleBridge"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyMsgBundleBridge", null, "*", null));

		results.add(
			javaFileChecker.findImport("com.liferay.portal.template.soy.internal.SoyProviderCapabilityBundleRegister"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyProviderCapabilityBundleRegister", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.SoyTemplate"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyTemplate", null, "*", null));

		results.add(
			javaFileChecker.findImport("com.liferay.portal.template.soy.internal.SoyTemplateBundleResourceParser"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyTemplateBundleResourceParser", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.SoyTemplateContextHelper"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyTemplateContextHelper", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.SoyTemplateRecord"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyTemplateRecord", null, "*", null));

		results.add(
			javaFileChecker.findImport(
				"com.liferay.portal.template.soy.internal.SoyTemplateResourceBundleTrackerCustomizer"));
		results.addAll(
			javaFileChecker.findMethodInvocations("SoyTemplateResourceBundleTrackerCustomizer", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.SoyTemplateResourceCache"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyTemplateResourceCache", null, "*", null));

		results.add(
			javaFileChecker.findImport("com.liferay.portal.template.soy.internal.SoyTemplateResourceFactoryImpl"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyTemplateResourceFactoryImpl", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.SoyTemplateResourceLoader"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyTemplateResourceLoader", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.SoyTofuCacheBag"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyTofuCacheBag", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.SoyTofuCacheHandler"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyTofuCacheHandler", null, "*", null));

		results.add(
			javaFileChecker.findImport(
				"com.liferay.portal.template.soy.internal.activator.PortalTemplateSoyImplBundleActivator"));
		results.addAll(javaFileChecker.findMethodInvocations("PortalTemplateSoyImplBundleActivator", null, "*", null));

		results.add(
			javaFileChecker.findImport(
				"com.liferay.portal.template.soy.internal.configuration.SoyTemplateEngineConfiguration"));
		results.addAll(javaFileChecker.findImplementsInterface("SoyTemplateEngineConfiguration"));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.data.SoyDataFactoryImpl"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyDataFactoryImpl", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.data.SoyDataFactoryProvider"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyDataFactoryProvider", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.data.SoyHTMLDataImpl"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyHTMLDataImpl", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.data.SoyRawDataImpl"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyRawDataImpl", null, "*", null));

		results.add(
			javaFileChecker.findImport(
				"com.liferay.portal.template.soy.internal.upgrade.PortalTemplateSoyImplUpgrade"));
		results.addAll(javaFileChecker.findMethodInvocations("PortalTemplateSoyImplUpgrade", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.util.SoyContextFactoryImpl"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyContextFactoryImpl", null, "*", null));

		results.add(
			javaFileChecker.findImport(
				"com.liferay.portal.template.soy.internal.util.SoyTemplateResourcesCollectorUtil"));
		results.addAll(javaFileChecker.findMethodInvocations(null, "SoyTemplateResourcesCollectorUtil", "*", null));

		results.add(
			javaFileChecker.findImport(
				"com.liferay.portal.template.soy.internal.util.SoyTemplateResourcesProviderImpl"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyTemplateResourcesProviderImpl", null, "*", null));

		results.add(javaFileChecker.findImport("com.liferay.portal.template.soy.internal.util.SoyTemplateUtil"));
		results.addAll(javaFileChecker.findMethodInvocations(null, "SoyTemplateUtil", "*", null));

		results.add(javaFileChecker.findImport("com.liferay.frontend.taglib.clay.internal.SoyDataFactoryProvider"));
		results.addAll(javaFileChecker.findMethodInvocations("SoyDataFactoryProvider", null, "*", null));

		return results;
	}

}