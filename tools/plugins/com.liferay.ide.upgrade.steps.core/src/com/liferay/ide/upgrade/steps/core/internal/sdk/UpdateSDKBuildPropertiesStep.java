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
import com.liferay.ide.upgrade.steps.core.WorkspaceSupport;
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
		"description=" + UpdateSDKBuildPropertiesStepKeys.DESCRIPTION, "id=" + UpdateSDKBuildPropertiesStepKeys.ID,
		"imagePath=icons/update_sdk_propperties.png", "requirement=required", "order=2",
		"parentId=" + MigratePluginsSDKProjectsStepKeys.ID, "title=" + UpdateSDKBuildPropertiesStepKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeStep.class
)
public class UpdateSDKBuildPropertiesStep extends BaseUpgradeStep implements WorkspaceSupport {

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

		IPath bundleLcoation = getHomeLocation(targetProjectLocation);

		PortalBundle liferayPortalBundle = LiferayServerCore.newPortalBundle(bundleLcoation);

		if (liferayPortalBundle == null) {
			return UpgradeStepsCorePlugin.createErrorStatus("There is no liferay bundle in current liferay workspace.");
		}

		IPath pluginSdkPath = getPluginsSDKLocation(targetProjectLocation);

		SDK sdk = SDKUtil.createSDKFromLocation(pluginSdkPath);

		if (sdk == null) {
			return UpgradeStepsCorePlugin.createErrorStatus("There is no plugins sdk in current liferay workspace.");
		}

		IStatus status = Status.OK_STATUS;

		try {
			Map<String, String> appServerPropertiesMap = new HashMap<>();

			appServerPropertiesMap.put(
				"app.server.deploy.dir", FileUtil.toOSString(liferayPortalBundle.getAppServerDeployDir()));
			appServerPropertiesMap.put("app.server.dir", FileUtil.toOSString(liferayPortalBundle.getAppServerDir()));
			appServerPropertiesMap.put(
				"app.server.lib.global.dir", FileUtil.toOSString(liferayPortalBundle.getAppServerLibGlobalDir()));
			appServerPropertiesMap.put(
				"app.server.parent.dir", FileUtil.toOSString(liferayPortalBundle.getLiferayHome()));
			appServerPropertiesMap.put(
				"app.server.portal.dir", FileUtil.toOSString(liferayPortalBundle.getAppServerPortalDir()));
			appServerPropertiesMap.put("app.server.type", liferayPortalBundle.getType());

			sdk.addOrUpdateServerProperties(appServerPropertiesMap);

			IProject sdkProject = CoreUtil.getProject(pluginSdkPath);

			if (sdkProject != null) {
				sdkProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			}

			sdk.validate(true);

			setStatus(UpgradeStepStatus.COMPLETED);
		}
		catch (Exception e) {
			status = UpgradeStepsCorePlugin.createErrorStatus(e.getMessage());

			setStatus(UpgradeStepStatus.FAILED);
		}

		_upgradePlanner.dispatch(
			new UpgradeStepPerformedEvent(this, Collections.singletonList(upgradePlan.getTargetProjectLocation())));

		return status;
	}

	@Reference
	private UpgradePlanner _upgradePlanner;

}