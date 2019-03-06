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

package com.liferay.ide.upgrade.tasks.core.internal.prerequisite;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepActionStatus;
import com.liferay.ide.upgrade.tasks.core.internal.UpgradeTasksCorePlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
@Component(
	property = {
		"id=check_installed_jdks", "order=1", "stepId=" + CheckInstallationPrerequisitesStepKeys.ID,
		"title=Check Installed JDKs"
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class CheckInstalledJDKsAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		IVMInstall defaultVMInstall = JavaRuntime.getDefaultVMInstall();

		IVMInstallType vmInstallType = defaultVMInstall.getVMInstallType();

		String name = vmInstallType.getName();

		boolean java8Installed = false;

		if ("Standard VM".equals(name) && defaultVMInstall instanceof IVMInstall2) {
			String jvmVersion = ((IVMInstall2)defaultVMInstall).getJavaVersion();

			String[] jvmVersionParts = jvmVersion.split("\\.");

			try {
				int major = Integer.parseInt(jvmVersionParts[0]);

				int minor = Integer.parseInt(jvmVersionParts[1]);

				if ((major == 1) && (minor == 8)) {
					java8Installed = true;
				}
			}
			catch (NumberFormatException nfe) {
			}
		}

		if (java8Installed) {
			UpgradeTaskStepActionStatus status = getStatus();

			if (status == UpgradeTaskStepActionStatus.FAILED) {
				status = UpgradeTaskStepActionStatus.COMPLETED;
			}

			return new Status(IStatus.OK, UpgradeTasksCorePlugin.ID, "");
		}
		else {
			setStatus(UpgradeTaskStepActionStatus.FAILED);

			return UpgradeTasksCorePlugin.createErrorStatus("no compatible jdk found");
		}
	}

}