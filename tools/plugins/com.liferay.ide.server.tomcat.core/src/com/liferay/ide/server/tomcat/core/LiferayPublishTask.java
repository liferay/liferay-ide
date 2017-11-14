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

package com.liferay.ide.server.tomcat.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jst.server.tomcat.core.internal.PublishTask;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.PublishOperation;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings({"rawtypes", "restriction", "unchecked"})
public class LiferayPublishTask extends PublishTask {

	public PublishOperation[] getTasks(IServer server, int kind, List modules, List kindList) {
		if (modules == null) {
			return null;
		}

		LiferayTomcatServerBehavior liferayServer = (LiferayTomcatServerBehavior)server.loadAdapter(
			LiferayTomcatServerBehavior.class, null);

		List tasks = new ArrayList();
		int size = modules.size();

		for (int i = 0; i < size; i++) {
			IModule[] module = (IModule[])modules.get(i);

			if (liferayServer.getRedeployModules() != null) {
				for (IModule[] moduleItem : liferayServer.getRedeployModules()) {
					String moduleItemId = moduleItem[0].getId();

					if (moduleItemId.equals(module[0].getId())) {
						int in = (Integer)kindList.get(i);

						tasks.add(new LiferayPublishOperation(liferayServer, kind, module, in));
					}
				}
			}
			else {
				int in = (Integer)kindList.get(i);

				tasks.add(new LiferayPublishOperation(liferayServer, kind, module, in));
			}
		}

		return (PublishOperation[])tasks.toArray(new PublishOperation[0]);
	}

}