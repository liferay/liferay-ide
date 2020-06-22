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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.wst.server.core.IRuntime;

import org.osgi.framework.Version;

/**
 * @author Seiphon Wang
 */
public class FragmentProjectLiferayVersionValidationService
	extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		NewModuleFragmentOp op = _op();

		Version liferayVersion = Version.parseVersion(get(op.getLiferayVersion()));

		String liferayRuntimeName = get(op.getLiferayRuntimeName());

		if (!liferayRuntimeName.equals("<None>")) {
			IRuntime runtime = ServerUtil.getRuntime(liferayRuntimeName);

			PortalBundle newPortalBundle = LiferayServerCore.newPortalBundle(runtime.getLocation());

			Version bundleVersion = Version.parseVersion(newPortalBundle.getVersion());

			if ((bundleVersion.getMajor() != liferayVersion.getMajor()) ||
				(bundleVersion.getMinor() != liferayVersion.getMinor())) {

				return Status.createErrorStatus("Current liferay version can not match liferay runtime version.");
			}
		}

		return retval;
	}

	private NewModuleFragmentOp _op() {
		return context(NewModuleFragmentOp.class);
	}

}