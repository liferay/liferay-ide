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

package com.liferay.ide.bndtools.core;

import aQute.bnd.build.Project;

import bndtools.central.Central;

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.ILiferayProjectProvider;

import org.eclipse.core.resources.IProject;

/**
 * @author Gregory Amerson
 */
public class BndtoolsProvider extends AbstractLiferayProjectProvider implements ILiferayProjectProvider {

	public BndtoolsProvider() {
		super(new Class<?>[] {IProject.class});
	}

	@Override
	public ILiferayProject provide(Class<?> type, Object adaptable) {
		if ((type != null) && !type.isAssignableFrom(BndtoolsProject.class)) {
			return null;
		}

		ILiferayProject retval = null;

		if (adaptable instanceof IProject) {
			IProject project = (IProject)adaptable;

			try {
				Project bndProject = Central.getProject(project);

				if (bndProject != null) {
					retval = new BndtoolsProject(project, bndProject);
				}
			}
			catch (Exception e) {
			}
		}

		return retval;
	}

}