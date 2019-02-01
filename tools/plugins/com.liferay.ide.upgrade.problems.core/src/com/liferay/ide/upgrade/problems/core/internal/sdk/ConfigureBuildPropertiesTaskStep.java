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

package com.liferay.ide.upgrade.problems.core.internal.sdk;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepStatus;
import com.liferay.ide.upgrade.plan.tasks.core.WorkspaceTaskStep;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"id=configure_build_properties", "requirement=required", "order=0", "taskId=migrate_plugins_sdk",
		"title=Configure Build Properties"
	},
	service = UpgradeTaskStep.class
)
public class ConfigureBuildPropertiesTaskStep extends WorkspaceTaskStep {

	@Override
	public IStatus execute(IProject project, IProgressMonitor progressMonitor) {
		IPath projectLocation = project.getLocation();

		IPath bundlesLocation = projectLocation.append("bundles");

		if (FileUtil.notExists(bundlesLocation)) {
			return Status.CANCEL_STATUS;
		}

		PortalBundle bundle = LiferayServerCore.newPortalBundle(bundlesLocation);

		IPath pluginsSdkLoaction = projectLocation.append("plugins-sdk");

		SDK sdk = SDKUtil.createSDKFromLocation(pluginsSdkLoaction);

		Map<String, String> appServerPropertiesMap = new HashMap<>();

		appServerPropertiesMap.put("app.server.deploy.dir", FileUtil.toOSString(bundle.getAppServerDeployDir()));
		appServerPropertiesMap.put("app.server.dir", FileUtil.toOSString(bundle.getAppServerDir()));
		appServerPropertiesMap.put("app.server.lib.global.dir", FileUtil.toOSString(bundle.getAppServerLibGlobalDir()));
		appServerPropertiesMap.put("app.server.parent.dir", FileUtil.toOSString(bundle.getLiferayHome()));
		appServerPropertiesMap.put("app.server.portal.dir", FileUtil.toOSString(bundle.getAppServerPortalDir()));
		appServerPropertiesMap.put("app.server.type", bundle.getType());

		try {
			sdk.addOrUpdateServerProperties(appServerPropertiesMap);
		}
		catch (IOException ioe) {
		}

		IProject sdkProject = CoreUtil.getProject("plugins-sdk");

		try {
			sdkProject.refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
		}
		catch (CoreException ce) {
		}

		return Status.OK_STATUS;
	}

	@Override
	public UpgradeTaskStepStatus getStatus() {
		return UpgradeTaskStepStatus.INCOMPLETE;
	}

}