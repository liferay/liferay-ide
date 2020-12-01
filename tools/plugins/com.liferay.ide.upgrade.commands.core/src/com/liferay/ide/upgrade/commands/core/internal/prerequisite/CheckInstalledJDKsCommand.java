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

package com.liferay.ide.upgrade.commands.core.internal.prerequisite;

import com.liferay.ide.upgrade.commands.core.UpgradeCommandsCorePlugin;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.JavaRuntime;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
@Component(
	property = "id=" + CheckInstalledJDKsCommandKeys.ID, scope = ServiceScope.PROTOTYPE, service = UpgradeCommand.class
)
public class CheckInstalledJDKsCommand implements UpgradeCommand {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		IVMInstall defaultVMInstall = JavaRuntime.getDefaultVMInstall();

		boolean java8Installed = false;

		if (defaultVMInstall instanceof IVMInstall2) {
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

		IStatus status = Status.OK_STATUS;

		if (java8Installed) {
			status = UpgradeCommandsCorePlugin.createInfoStatus("JDK8 is installed and the default VM install.");
		}
		else {
			status = UpgradeCommandsCorePlugin.createErrorStatus(
				"JDK8 is not installed or is not the default VM in Eclipse preferences.");
		}

		_upgradePlanner.dispatch(new UpgradeCommandPerformedEvent(this, null));

		return status;
	}

	@Reference
	private UpgradePlanner _upgradePlanner;

}