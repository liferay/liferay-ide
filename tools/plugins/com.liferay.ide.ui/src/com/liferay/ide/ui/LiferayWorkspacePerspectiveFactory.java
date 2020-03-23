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

package com.liferay.ide.ui;

import com.liferay.ide.ui.util.ProjectExplorerLayoutUtil;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Lovett Li
 * @author Terry Jia
 */
public class LiferayWorkspacePerspectiveFactory extends AbstractPerspectiveFactory {

	public static final String ID = "com.liferay.ide.eclipse.ui.perspective.liferayworkspace";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		createLayout(layout);
		addShortcuts(layout);
		setupActions(layout);
	}

	protected void addShortcuts(IPageLayout layout) {
		layout.addNewWizardShortcut(ID_NEW_COMPONENT_WIZARD);
		layout.addNewWizardShortcut(ID_NEW_MODULE_FRAGMENT_FILES_WIZARD);
		layout.addNewWizardShortcut(ID_NEW_MODULE_PROJECT_WIZARD);
		layout.addNewWizardShortcut(ID_NEW_JSF_MODULE_WIZARD);
		layout.addNewWizardShortcut(ID_NEW_MODULE_FRAGMENT_PROJECT_WIZARD);
		layout.addNewWizardShortcut(ID_NEW_WORKSPACE_PROJECT_WIZARD);
	}

	protected void createLayout(IPageLayout layout) {

		// Editors are placed for free.

		String editorArea = layout.getEditorArea();

		// Top left.

		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.20F, editorArea);

		topLeft.addView(ID_PROJECT_EXPLORER_VIEW);
		topLeft.addPlaceholder(ID_PACKAGE_EXPLORER_VIEW);
		topLeft.addPlaceholder(ID_J2EE_HIERARCHY_VIEW);
		topLeft.addPlaceholder(JavaUI.ID_TYPE_HIERARCHY);
		topLeft.addPlaceholder(JavaUI.ID_PACKAGES_VIEW);

		// Top right.

		IFolderLayout topRight = layout.createFolder("topRight", IPageLayout.RIGHT, 0.68F, editorArea);

		addViewIfExist(layout, topRight, ID_GRADLE_TASK_VIEW);

		topRight.addPlaceholder(IPageLayout.ID_BOOKMARKS);

		try {
			IFolderLayout upgradeFolder = layout.createFolder("topRightRight", IPageLayout.RIGHT, 0.5F, "fast");

			upgradeFolder.addPlaceholder("com.liferay.ide.project.ui.upgradeView");
		}
		catch (Exception e) {
			topRight.addPlaceholder("com.liferay.ide.project.ui.upgradeView");
		}

		IFolderLayout topRightBottom = layout.createFolder("topRightBottom", IPageLayout.BOTTOM, 0.7F, "topRight");

		addViewIfExist(layout, topRightBottom, ID_GRADLE_EXECUTIONS_VIEW);

		IFolderLayout bottomTopLeft = layout.createFolder("bottomTopLeft", IPageLayout.BOTTOM, 0.7F, "topLeft");

		bottomTopLeft.addView(ID_SERVERS_VIEW);

		// Bottom

		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.7F, editorArea);

		bottom.addView(ID_MARKERS_VIEW);
		bottom.addView(ID_CONSOLE_VIEW);

		bottom.addPlaceholder(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);
		bottom.addPlaceholder(ID_SEARCH_VIEW);

		ProjectExplorerLayoutUtil.setNested(true);
	}

	protected void setupActions(IPageLayout layout) {
		layout.addActionSet("com.liferay.ide.eclipse.ui.shortcuts.actionSet");
	}

}