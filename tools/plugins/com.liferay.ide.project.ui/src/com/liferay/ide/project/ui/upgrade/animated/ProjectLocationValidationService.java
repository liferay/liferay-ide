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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.ImportLiferayModuleProjectOpMethods;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

import org.osgi.framework.Version;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Terry Jia
 */
public class ProjectLocationValidationService extends ValidationService {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		int countPossibleWorkspaceSDKProjects = SDKUtil.countPossibleWorkspaceSDKProjects();

		if (countPossibleWorkspaceSDKProjects > 1) {
			return StatusBridge.create(ProjectCore.createErrorStatus("This workspace has more than one SDK."));
		}

		Path location = SapphireUtil.getContent(_op().getSdkLocation());

		if ((location == null) || location.isEmpty()) {
			return StatusBridge.create(
				ProjectCore.createErrorStatus("Liferay Plugins SDK, Maven or Liferay Workspace location is empty."));
		}

		if (LiferayWorkspaceUtil.isValidWorkspaceLocation(location.toPortableString())) {
			return retval;
		}

		IStatus buildType = ImportLiferayModuleProjectOpMethods.getBuildType(
			FileUtil.toPortableString(location.removeFileExtension()));

		SDK sdk = SDKUtil.createSDKFromLocation(PathBridge.create(location));

		if (sdk != null) {
			String version = sdk.getVersion();

			if (version != null) {
				Version sdkVersion = new Version(version);

				int result = sdkVersion.compareTo(new Version("6.1.0"));

				if (result < 0) {
					return StatusBridge.create(ProjectCore.createErrorStatus("This tool does not support 6.0.x."));
				}
			}
		}
		else if (!"maven".equals(buildType.getMessage())) {
			return StatusBridge.create(
				ProjectCore.createErrorStatus("Plugins SDK, Maven or Liferay Workspace location is not valid."));
		}

		return retval;
	}

	private LiferayUpgradeDataModel _op() {
		return context(LiferayUpgradeDataModel.class);
	}

}