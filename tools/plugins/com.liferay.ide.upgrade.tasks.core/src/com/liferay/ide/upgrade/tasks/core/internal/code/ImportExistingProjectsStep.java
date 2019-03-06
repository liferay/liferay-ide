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

package com.liferay.ide.upgrade.tasks.core.internal.code;

import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.tasks.core.code.ImportExistingProjectsStepKeys;
import com.liferay.ide.upgrade.tasks.core.code.SetupDevelopmentEnvironmentTaskKeys;

import java.nio.file.Path;

import org.eclipse.core.runtime.IPath;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"description=" + ImportExistingProjectsStepKeys.DESCRIPTION, "id=" + ImportExistingProjectsStepKeys.ID,
		"imagePath=icons/import_projects.png", "requirement=required", "order=1",
		"taskId=" + SetupDevelopmentEnvironmentTaskKeys.ID, "title=" + ImportExistingProjectsStepKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStep.class
)
public class ImportExistingProjectsStep extends BaseUpgradeTaskStep {

	@Override
	public boolean appliesTo(UpgradePlan upgradePlan) {
		Path currentProjectLocation = upgradePlan.getCurrentProjectLocation();

		if (currentProjectLocation == null) {
			return super.appliesTo(upgradePlan);
		}

		currentProjectLocation = currentProjectLocation.toAbsolutePath();

		IPath path = new org.eclipse.core.runtime.Path(currentProjectLocation.toString());

		if (SDKUtil.isValidSDKLocation(path)) {
			return false;
		}

		return super.appliesTo(upgradePlan);
	}

}