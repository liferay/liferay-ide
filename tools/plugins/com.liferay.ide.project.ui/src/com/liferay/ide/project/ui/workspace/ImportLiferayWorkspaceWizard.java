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

package com.liferay.ide.project.ui.workspace;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.workspace.ImportLiferayWorkspaceOp;
import com.liferay.ide.project.ui.BaseProjectWizard;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.ProjectExplorerLayoutUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.ui.IWorkbench;

/**
 * @author Andy Wu
 * @author Simon Jiang
 */
public class ImportLiferayWorkspaceWizard extends BaseProjectWizard<ImportLiferayWorkspaceOp> {

	public ImportLiferayWorkspaceWizard() {
		super(_createDefaultOp(), DefinitionLoader.sdef(ImportLiferayWorkspaceWizard.class).wizard());
	}

	public ImportLiferayWorkspaceWizard(Boolean showDownloadBundle) {
		super(_createDefaultOp(showDownloadBundle), DefinitionLoader.sdef(ImportLiferayWorkspaceWizard.class).wizard());
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	protected String getFirstErrorMessage() {
		return "Please select the workspace location.";
	}

	@Override
	protected void performPostFinish() {
		super.performPostFinish();

		ImportLiferayWorkspaceOp op = element().nearest(ImportLiferayWorkspaceOp.class);

		Path projectPath = get(op.getWorkspaceLocation());

		IProject newProject = CoreUtil.getProject(projectPath.lastSegment());

		try {
			addToWorkingSets(newProject);
		}
		catch (Exception ex) {
			ProjectUI.logError("Unable to add project to working set", ex);
		}

		openLiferayPerspective(newProject);

		ProjectExplorerLayoutUtil.setNested(true);
	}

	private static ImportLiferayWorkspaceOp _createDefaultOp() {
		return ImportLiferayWorkspaceOp.TYPE.instantiate();
	}

	private static ImportLiferayWorkspaceOp _createDefaultOp(Boolean showDownloadBundle) {
		ImportLiferayWorkspaceOp importWorkspaceOp = ImportLiferayWorkspaceOp.TYPE.instantiate();

		importWorkspaceOp.setShowDownloadBundle(showDownloadBundle);

		return importWorkspaceOp;
	}

}