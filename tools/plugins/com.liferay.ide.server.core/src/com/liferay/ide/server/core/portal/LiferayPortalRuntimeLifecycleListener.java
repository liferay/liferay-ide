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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.docker.PortalDockerRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Objects;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.util.RuntimeLifecycleAdapter;

/**
 * @author Simon Jiang
 */
public class LiferayPortalRuntimeLifecycleListener extends RuntimeLifecycleAdapter {

	public void runtimeRemoved(IRuntime runtime) {
		PortalRuntime portalRuntime = (PortalRuntime)runtime.loadAdapter(
			PortalRuntime.class, new NullProgressMonitor());

		if (Objects.isNull(portalRuntime) || (portalRuntime instanceof PortalDockerRuntime)) {
			return;
		}

		try {
			IPath portalDir = portalRuntime.getAppServerPortalDir();

			if (Objects.nonNull(portalDir) && FileUtil.exists(portalDir.toFile())) {
				String portalDirKey = CoreUtil.createStringDigest(portalDir.toPortableString());

				ServerUtil.removeConfigInfoFromCache(portalDirKey);
			}
		}
		catch (Exception exception) {
			LiferayServerCore.logError(
				"Faild to remove portal version cache for portal rutnime " + portalRuntime.getLiferayHome(), exception);
		}
	}

}