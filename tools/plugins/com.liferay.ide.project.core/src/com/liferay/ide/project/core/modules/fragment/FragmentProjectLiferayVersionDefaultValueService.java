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
import com.liferay.ide.project.core.service.TargetLiferayVersionDefaultValueService;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.wst.server.core.IRuntime;

import org.osgi.framework.Version;

/**
 * @author Seiphon Wang
 */
public class FragmentProjectLiferayVersionDefaultValueService
	extends TargetLiferayVersionDefaultValueService implements SapphireContentAccessor {

	@Override
	protected String compute() {
		NewModuleFragmentOp op = _op();

		String liferayRuntimeName = get(op.getLiferayRuntimeName());

		if (!liferayRuntimeName.equals("<None>")) {
			IRuntime runtime = ServerUtil.getRuntime(liferayRuntimeName);

			PortalBundle newPortalBundle = LiferayServerCore.newPortalBundle(runtime.getLocation());

			Version bundleVersion = Version.parseVersion(newPortalBundle.getVersion());

			Integer major = Integer.valueOf(bundleVersion.getMajor());

			Integer minor = Integer.valueOf(bundleVersion.getMinor());

			return major.toString() + "." + minor.toString();
		}

		return super.compute();
	}

	private NewModuleFragmentOp _op() {
		return context(NewModuleFragmentOp.class);
	}

}