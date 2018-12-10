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

package com.liferay.ide.upgrade.plan.base;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.LiferayWorkspaceProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author Terry Jia
 */
public abstract class WorkspaceUpgradeTaskStep extends ProjectUpgradeTaskStep {

	protected ViewerFilter getFilter() {
		return new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				IProject project = (IProject)element;

				if (LiferayCore.create(LiferayWorkspaceProject.class, project) != null) {
					return true;
				}

				return false;
			}

		};
	}

	@Override
	protected boolean selectAllDefault() {
		return true;
	}

}