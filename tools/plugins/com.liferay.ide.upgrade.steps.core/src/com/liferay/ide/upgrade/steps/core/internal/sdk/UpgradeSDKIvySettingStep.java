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

package com.liferay.ide.upgrade.steps.core.internal.sdk;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStepPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeStepStatus;
import com.liferay.ide.upgrade.steps.core.internal.UpgradeStepsCorePlugin;
import com.liferay.ide.upgrade.steps.core.sdk.MigratePluginsSDKProjectsStepKeys;
import com.liferay.ide.upgrade.steps.core.sdk.UpdateSDKIvySettingStepKeys;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Simon Jiang
 */
@Component(
	property = {
		"description=" + UpdateSDKIvySettingStepKeys.DESCRIPTION, "id=" + UpdateSDKIvySettingStepKeys.ID,
		"imagePath=icons/update_sdk_ivy.png", "requirement=required", "order=3",
		"parentId=" + MigratePluginsSDKProjectsStepKeys.ID, "title=" + UpdateSDKIvySettingStepKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeStep.class
)
public class UpgradeSDKIvySettingStep extends BaseUpgradeStep {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Path currentProjectLocation = upgradePlan.getCurrentProjectLocation();

		Path targetProjectLocation = upgradePlan.getTargetProjectLocation();

		if (currentProjectLocation == null) {
			return UpgradeStepsCorePlugin.createErrorStatus(
				"There is no current project location configured for current plan.");
		}

		Path pluginsSDKLoaction = targetProjectLocation.resolve("plugins-sdk");

		IStatus removeIvyPrivateSettingStatus = _removeIvyPrivateSetting(pluginsSDKLoaction);

		if (removeIvyPrivateSettingStatus.isOK()) {
			setStatus(UpgradeStepStatus.COMPLETED);
		}
		else {
			setStatus(UpgradeStepStatus.FAILED);
		}

		_upgradePlanner.dispatch(
			new UpgradeStepPerformedEvent(this, Collections.singletonList(upgradePlan.getTargetProjectLocation())));

		return removeIvyPrivateSettingStatus;
	}

	@SuppressWarnings("unchecked")
	private IStatus _removeIvyPrivateSetting(Path sdkLocation) {
		Path ivySettingPath = sdkLocation.resolve("ivy-settings.xml");

		File ivySettingFile = ivySettingPath.toFile();

		SAXBuilder builder = new SAXBuilder(false);

		builder.setValidation(false);
		builder.setFeature("http://xml.org/sax/features/validation", false);
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

		if (FileUtil.notExists(ivySettingFile)) {
			return Status.OK_STATUS;
		}

		try (InputStream ivyInput = Files.newInputStream(ivySettingFile.toPath())) {
			Document doc = builder.build(ivyInput);

			Element itemRem = null;

			Element elementRoot = doc.getRootElement();

			List<Element> resolversElements = elementRoot.getChildren("resolvers");

			for (Iterator<Element> resolversIterator = resolversElements.iterator(); resolversIterator.hasNext();) {
				Element resolversElement = resolversIterator.next();

				List<Element> chainElements = resolversElement.getChildren("chain");

				for (Iterator<Element> chainIterator = chainElements.iterator(); chainIterator.hasNext();) {
					Element chainElement = chainIterator.next();

					List<Element> resolverElements = chainElement.getChildren("resolver");

					Iterator<Element> resolverIterator = resolverElements.iterator();

					while (resolverIterator.hasNext()) {
						Element resolverItem = resolverIterator.next();

						String resolverRefItem = resolverItem.getAttributeValue("ref");

						if (resolverRefItem.equals("liferay-private")) {
							resolverIterator.remove();

							itemRem = resolverItem;
						}
					}
				}

				elementRoot.removeContent(itemRem);

				List<Element> ibiblioElements = resolversElement.getChildren("ibiblio");

				for (Iterator<Element> ibiblioIterator = ibiblioElements.iterator(); ibiblioIterator.hasNext();) {
					Element ibiblioElement = ibiblioIterator.next();

					String liferayPrivateName = ibiblioElement.getAttributeValue("name");

					if (liferayPrivateName.equals("liferay-private")) {
						ibiblioIterator.remove();
						itemRem = ibiblioElement;
					}
				}

				elementRoot.removeContent(itemRem);
			}

			XMLOutputter out = new XMLOutputter();

			try (OutputStream fos = Files.newOutputStream(ivySettingFile.toPath())) {
				out.output(doc, fos);
			}
			catch (Exception e) {
				return UpgradeStepsCorePlugin.createErrorStatus(e.getMessage());
			}
		}
		catch (IOException | JDOMException e) {
			return UpgradeStepsCorePlugin.createErrorStatus(e.getMessage());
		}

		return Status.OK_STATUS;
	}

	@Reference private UpgradePlanner _upgradePlanner;

}