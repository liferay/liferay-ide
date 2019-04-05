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

package com.liferay.ide.hook.core.model.internal;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.model.StrutsAction;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.services.ServiceContext;

/**
 * @author Gregory Amerson
 */
public class StrutsActionPathPossibleValuesService extends PossibleValuesService {

	@Override
	protected void compute(Set<String> values) {
		if (FileUtil.exists(_portalDir)) {
			if (_possibleValues == null) {
				IPath strutsConfigPath = _portalDir.append("WEB-INF/struts-config.xml");

				ServiceContext serviceContext = context();

				StrutsActionPathPossibleValuesCacheService cacheService =
					serviceContext.service(StrutsActionPathPossibleValuesCacheService.class);

				_possibleValues = cacheService.getPossibleValuesForPath(strutsConfigPath);
			}

			values.addAll(_possibleValues);

			// add the value that is current set by the user

			StrutsAction strutsAction = context(StrutsAction.class);

			Value<String> strutsActionPathValue = strutsAction.getStrutsActionPath();

			String actionPath = strutsActionPathValue.content(false);

			if (!empty(actionPath)) {
				values.add(actionPath);
			}
		}
	}

	protected Hook hook() {
		ServiceContext serviceContext = context();

		return serviceContext.find(Hook.class);
	}

	@Override
	protected void initPossibleValuesService() {
		super.initPossibleValuesService();

		ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, project());

		if (liferayProject != null) {
			ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

			if (portal != null) {
				_portalDir = portal.getAppServerPortalDir();
			}
		}
	}

	protected IProject project() {
		Hook hook = hook();

		return hook.adapt(IProject.class);
	}

	private IPath _portalDir;
	private TreeSet<String> _possibleValues;

}