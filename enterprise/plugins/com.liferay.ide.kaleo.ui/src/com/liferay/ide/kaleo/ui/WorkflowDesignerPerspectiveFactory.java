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

package com.liferay.ide.kaleo.ui;

import com.liferay.ide.ui.LiferayPerspectiveFactory;

import org.eclipse.gef.ui.views.palette.PaletteView;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Gregory Amerson
 */
public class WorkflowDesignerPerspectiveFactory extends LiferayPerspectiveFactory {

	public static final String ID = "com.liferay.ide.eclipse.kaleo.ui.perspective.designer";

	public static final String ID_NEW_WORKFLOW_DEFINITION_WIZARD = "com.liferay.ide.kaleo.ui.new.definition";

	protected void addShortcuts(IPageLayout layout) {
		layout.addNewWizardShortcut(ID_NEW_PLUGIN_PROJECT_WIZARD);
		layout.addNewWizardShortcut(ID_NEW_PLUGIN_PROJECT_WIZARD_EXISTING_SOURCE);
		layout.addNewWizardShortcut(ID_NEW_WORKFLOW_DEFINITION_WIZARD);
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
		layout.addNewWizardShortcut("org.eclipse.ui.editors.wizards.UntitledTextFileWizard");
		layout.addPerspectiveShortcut("com.liferay.ide.eclipse.ui.perspective.liferay");
		layout.addPerspectiveShortcut("org.eclipse.jst.j2ee.J2EEPerspective");
		layout.addPerspectiveShortcut("org.eclipse.jdt.ui.JavaPerspective");
		layout.addPerspectiveShortcut("org.eclipse.debug.ui.DebugPerspective");
		layout.addShowViewShortcut(ANT_VIEW_ID);

		IWorkbench workBench = PlatformUI.getWorkbench();

		IPerspectiveRegistry registry = workBench.getPerspectiveRegistry();

		IPerspectiveDescriptor desc = registry.findPerspectiveWithId("org.eclipse.team.cvs.ui.cvsPerspective");

		if (desc != null) {
			layout.addPerspectiveShortcut("org.eclipse.team.cvs.ui.cvsPerspective");
		}

		String svnPerspectiveQName = "org.tigris.subversion.subclipse.ui.svnPerspective";

		desc = registry.findPerspectiveWithId(svnPerspectiveQName);

		if (desc != null) {
			layout.addPerspectiveShortcut(svnPerspectiveQName);
		}

		String repositoryPerspectiveQName = "org.eclipse.team.svn.ui.repository.RepositoryPerspective";

		desc = registry.findPerspectiveWithId(repositoryPerspectiveQName);

		if (desc != null) {
			layout.addPerspectiveShortcut(repositoryPerspectiveQName);
		}
	}

	protected void createLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.20F, editorArea);

		topLeft.addView(ID_PACKAGE_EXPLORER_VIEW);

		topLeft.addPlaceholder(ID_J2EE_HIERARCHY_VIEW);
		//topLeft.addPlaceholder(IPageLayout.ID_RES_NAV);
		topLeft.addPlaceholder(JavaUI.ID_TYPE_HIERARCHY);
		topLeft.addPlaceholder(JavaUI.ID_PACKAGES_VIEW);

		IFolderLayout topRight = layout.createFolder("topRight", IPageLayout.RIGHT, 0.68F, editorArea);

		topRight.addView(PaletteView.ID);
		topRight.addPlaceholder(IPageLayout.ID_BOOKMARKS);

		IFolderLayout topRightBottom = layout.createFolder("topRightBottom", IPageLayout.BOTTOM, 0.55F, "topRight");

		topRightBottom.addView(IPageLayout.ID_OUTLINE);

		IFolderLayout bottomTopLeft = layout.createFolder("bottomTopLeft", IPageLayout.BOTTOM, 0.7F, "topLeft");

		bottomTopLeft.addView(ID_SERVERS_VIEW);

		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.55F, editorArea);

		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addView(ID_MARKERS_VIEW);
		bottom.addPlaceholder(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);
		bottom.addPlaceholder(ID_SEARCH_VIEW);
	}

}