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

package com.liferay.ide.project.ui.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;
import com.liferay.ide.project.ui.BaseProjectWizard;
import com.liferay.ide.project.ui.ProjectUI;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.ui.def.DefinitionLoader;

/**
 * @author Simon Jiang
 */
public class NewLiferayJSFModuleProjectWizard extends BaseProjectWizard<NewLiferayJSFModuleProjectOp> {

	public NewLiferayJSFModuleProjectWizard() {
		super(_createDefaultOp(), DefinitionLoader.sdef(NewLiferayJSFModuleProjectWizard.class).wizard());
	}

	@Override
	protected void performPostFinish() {
		super.performPostFinish();

		NewLiferayJSFModuleProjectOp op = element().nearest(NewLiferayJSFModuleProjectOp.class);

		IProject project = CoreUtil.getProject(get(op.getProjectName()));

		try {
			addToWorkingSets(project);
		}
		catch (Exception ex) {
			ProjectUI.logError("Unable to add project to working set", ex);
		}

		openLiferayPerspective(project);
	}

	private static NewLiferayJSFModuleProjectOp _createDefaultOp() {
		return NewLiferayJSFModuleProjectOp.TYPE.instantiate();
	}

}