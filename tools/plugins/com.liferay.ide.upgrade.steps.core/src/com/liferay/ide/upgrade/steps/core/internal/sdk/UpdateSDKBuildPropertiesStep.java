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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStepPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeStepStatus;
import com.liferay.ide.upgrade.steps.core.internal.UpgradeStepsCorePlugin;
import com.liferay.ide.upgrade.steps.core.sdk.MigratePluginsSDKProjectsStepKeys;
import com.liferay.ide.upgrade.steps.core.sdk.UpdateSDKBuildPropertiesStepKeys;

import java.nio.file.Path;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Simon Jiang
 */
@Component(
	property = {
		"description=" + UpdateSDKBuildPropertiesStepKeys.DESCRIPTION, "id=" + UpdateSDKBuildPropertiesStepKeys.ID,
		"imagePath=icons/update_sdk_propperties.png", "requirement=required", "order=2",
		"parentId=" + MigratePluginsSDKProjectsStepKeys.ID, "title=" + UpdateSDKBuildPropertiesStepKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeStep.class
)
public class UpdateSDKBuildPropertiesStep extends BaseUpgradeStep {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Path currentProjectLocation = upgradePlan.getCurrentProjectLocation();

		Path targetProjectLocation = upgradePlan.getTargetProjectLocation();

		if (currentProjectLocation == null) {
			return UpgradeStepsCorePlugin.createErrorStatus(
				"There is no current project location configured for current plan.");
		}

		IStatus updatePluginsSdkPropertiesStatus = _updatePluginsSdkProperties(targetProjectLocation);

		if (updatePluginsSdkPropertiesStatus.isOK()) {
			setStatus(UpgradeStepStatus.COMPLETED);
		}
		else {
			setStatus(UpgradeStepStatus.FAILED);
		}

		_upgradePlanner.dispatch(
			new UpgradeStepPerformedEvent(this, Collections.singletonList(upgradePlan.getTargetProjectLocation())));

		return Status.OK_STATUS;
	}

	private IStatus _updatePluginsSdkProperties(Path targetProjectLocation) {
		Path pluginsSDKLoaction = targetProjectLocation.resolve("plugins-sdk");

		IProject workspaceProject = CoreUtil.getProject(targetProjectLocation.toFile());

		if (FileUtil.exists(pluginsSDKLoaction.toFile())) {
			String pluginsSDKDirName = LiferayWorkspaceUtil.getPluginsSDKDir(targetProjectLocation.toString());

			if (CoreUtil.isNullOrEmpty(pluginsSDKDirName)) {
				return UpgradeStepsCorePlugin.createErrorStatus("Not found plugins sdk folder.");
			}

			IPath workspaceProjectPath = workspaceProject.getLocation();

			IPath pluginSdkPath = workspaceProjectPath.append(pluginsSDKDirName);

			SDK sdk = SDKUtil.createSDKFromLocation(pluginSdkPath);

			if (sdk != null) {
				IPath bundleLcoation = LiferayWorkspaceUtil.getHomeLocation(workspaceProject);

				PortalBundle liferayPortalBundle = LiferayServerCore.newPortalBundle(bundleLcoation);

				if (liferayPortalBundle != null) {
					try {
						Map<String, String> appServerPropertiesMap = new HashMap<>();

						appServerPropertiesMap.put(
							"app.server.deploy.dir", FileUtil.toOSString(liferayPortalBundle.getAppServerDeployDir()));
						appServerPropertiesMap.put(
							"app.server.dir", FileUtil.toOSString(liferayPortalBundle.getAppServerDir()));
						appServerPropertiesMap.put(
							"app.server.lib.global.dir",
							FileUtil.toOSString(liferayPortalBundle.getAppServerLibGlobalDir()));
						appServerPropertiesMap.put(
							"app.server.parent.dir", FileUtil.toOSString(liferayPortalBundle.getLiferayHome()));
						appServerPropertiesMap.put(
							"app.server.portal.dir", FileUtil.toOSString(liferayPortalBundle.getAppServerPortalDir()));
						appServerPropertiesMap.put("app.server.type", liferayPortalBundle.getType());

						sdk.addOrUpdateServerProperties(appServerPropertiesMap);

						IProject sdkProject = CoreUtil.getProject(pluginsSDKLoaction.toFile());

						if (sdkProject != null) {
							sdkProject.refreshLocal(IResource.DEPTH_INFINITE, null);
						}

						sdk.validate(true);
					}
					catch (Exception e) {
						return UpgradeStepsCorePlugin.createErrorStatus(e.getMessage());
					}
				}
			}
		}

		return Status.OK_STATUS;

	}

	@Reference private UpgradePlanner _upgradePlanner;

}