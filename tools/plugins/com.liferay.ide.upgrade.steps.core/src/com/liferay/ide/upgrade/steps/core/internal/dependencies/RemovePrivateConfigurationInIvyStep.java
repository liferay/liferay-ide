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

package com.liferay.ide.upgrade.steps.core.internal.dependencies;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStepPerformedEvent;
import com.liferay.ide.upgrade.steps.core.WorkspaceSupport;
import com.liferay.ide.upgrade.steps.core.dependencies.RemovePrivateConfigurationInIvyStepKeys;
import com.liferay.ide.upgrade.steps.core.dependencies.UpdatePluginsSDKDependenciesStepKeys;
import com.liferay.ide.upgrade.steps.core.internal.UpgradeStepsCorePlugin;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
@Component(
	property = {
		"description=" + RemovePrivateConfigurationInIvyStepKeys.DESCRIPTION,
		"id=" + RemovePrivateConfigurationInIvyStepKeys.ID, "imagePath=icons/update_sdk_ivy.png",
		"requirement=required", "order=3", "parentId=" + UpdatePluginsSDKDependenciesStepKeys.ID,
		"title=" + RemovePrivateConfigurationInIvyStepKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeStep.class
)
@SuppressWarnings("unchecked")
public class RemovePrivateConfigurationInIvyStep extends BaseUpgradeStep implements WorkspaceSupport {

	@Activate
	public void activate(ComponentContext componentContext) {
		super.activate(_upgradePlanner, componentContext);
	}

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Path currentProjectLocation = upgradePlan.getCurrentProjectLocation();

		if (currentProjectLocation == null) {
			return UpgradeStepsCorePlugin.createErrorStatus(
				"There is no current project location configured for current plan.");
		}

		Path targetProjectLocation = upgradePlan.getTargetProjectLocation();

		if (targetProjectLocation == null) {
			return UpgradeStepsCorePlugin.createErrorStatus(
				"There is no target project location configured for current plan.");
		}

		IPath pluginsSDKLocation = getPluginsSDKLocation(targetProjectLocation);

		if (FileUtil.notExists(pluginsSDKLocation)) {
			return UpgradeStepsCorePlugin.createErrorStatus("There is no plugins sdk.");
		}

		IPath ivySettingFile = pluginsSDKLocation.append("ivy-settings.xml");

		if (FileUtil.notExists(ivySettingFile)) {
			return UpgradeStepsCorePlugin.createErrorStatus("There is no ivy-settings.xml in plugins sdk.");
		}

		File file = ivySettingFile.toFile();

		IStatus status = _removeIvyPrivateSetting(file.toPath());

		_upgradePlanner.dispatch(
			new UpgradeStepPerformedEvent(this, Collections.singletonList(upgradePlan.getTargetProjectLocation())));

		return status;
	}

	private IStatus _removeIvyPrivateSetting(Path ivySettingsPath) {
		SAXBuilder builder = new SAXBuilder(false);

		builder.setValidation(false);

		builder.setFeature("http://xml.org/sax/features/validation", false);
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

		try (InputStream input = Files.newInputStream(ivySettingsPath)) {
			Document document = builder.build(input);

			Element rootElement = document.getRootElement();

			List<Element> resolversElements = rootElement.getChildren("resolvers");

			Stream<Element> resolversStream = resolversElements.stream();

			@SuppressWarnings("serial")
			Filter resolverFilter = new ElementFilter("resolver") {

				public boolean matches(Object obj) {
					if (super.matches(obj)) {
						String ref = ((Element)obj).getAttributeValue("ref");

						return "liferay-private".equals(ref);
					}

					return false;
				}

			};

			resolversStream.map(
				resolvers -> resolvers.getChildren("chain")
			).flatMap(
				chains -> chains.stream()
			).forEach(
				chain -> ((Element)chain).removeContent(resolverFilter)
			);

			resolversStream = resolversElements.stream();

			@SuppressWarnings("serial")
			Filter ibiblioFilter = new ElementFilter("ibiblio") {

				public boolean matches(Object obj) {
					if (super.matches(obj)) {
						String ref = ((Element)obj).getAttributeValue("name");

						return "liferay-private".equals(ref);
					}

					return false;
				}

			};

			resolversStream.forEach(resolvers -> resolvers.removeContent(ibiblioFilter));

			XMLOutputter xmlOutputter = new XMLOutputter();

			try (OutputStream output = Files.newOutputStream(ivySettingsPath)) {
				xmlOutputter.output(document, output);
			}
		}
		catch (Exception e) {
			return UpgradeStepsCorePlugin.createErrorStatus(e.getMessage());
		}

		return Status.OK_STATUS;
	}

	@Reference
	private UpgradePlanner _upgradePlanner;

}