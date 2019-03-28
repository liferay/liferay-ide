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

package com.liferay.ide.upgrade.commands.core.internal.sdk;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.upgrade.commands.core.WorkspaceSupport;
import com.liferay.ide.upgrade.commands.core.internal.UpgradeCommandsCorePlugin;
import com.liferay.ide.upgrade.commands.core.sdk.UpdateSDKBuildPropertiesCommandKeys;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

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
 * @author Terry Jia
 */
@Component(
	property = "id=" + UpdateSDKBuildPropertiesCommandKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = UpgradeCommand.class
)
public class UpdateSDKBuildPropertiesCommand implements UpgradeCommand, WorkspaceSupport {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Path currentProjectLocation = upgradePlan.getCurrentProjectLocation();

		if (currentProjectLocation == null) {
			return UpgradeCommandsCorePlugin.createErrorStatus(
				"There is no current project location configured for current plan.");
		}

		Path targetProjectLocation = upgradePlan.getTargetProjectLocation();

		IPath bundleLcoation = getHomeLocation(targetProjectLocation);

		PortalBundle liferayPortalBundle = LiferayServerCore.newPortalBundle(bundleLcoation);

		if (liferayPortalBundle == null) {
			return UpgradeCommandsCorePlugin.createErrorStatus(
				"There is no liferay bundle in current liferay workspace.");
		}

		IPath pluginSdkPath = getPluginsSDKLocation(targetProjectLocation);

		SDK sdk = SDKUtil.createSDKFromLocation(pluginSdkPath);

		if (sdk == null) {
			return UpgradeCommandsCorePlugin.createErrorStatus("There is no plugins sdk in current liferay workspace.");
		}

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

			_upgradePlanner.dispatch(
				new UpgradeCommandPerformedEvent(
					this, Collections.singletonList(upgradePlan.getTargetProjectLocation())));

			return Status.OK_STATUS;
		}
		catch (Exception e) {
			return UpgradeCommandsCorePlugin.createErrorStatus(e.getMessage());

		}
	}

	@Reference
	private UpgradePlanner _upgradePlanner;

}