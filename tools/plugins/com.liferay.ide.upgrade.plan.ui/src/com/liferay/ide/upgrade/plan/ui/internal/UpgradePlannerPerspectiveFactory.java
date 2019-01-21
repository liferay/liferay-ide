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

package com.liferay.ide.upgrade.plan.ui.internal;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradePlannerPerspectiveFactory implements IPerspectiveFactory {

	public static final String ID = "com.liferay.ide.upgrade.planner.perspective";

	@Override
	public void createInitialLayout(IPageLayout pageLayout) {
		String editorArea = pageLayout.getEditorArea();

		IFolderLayout topLeftFolderLayout = pageLayout.createFolder("topLeft", IPageLayout.LEFT, 0.5F, editorArea);

		topLeftFolderLayout.addView("org.eclipse.ui.navigator.ProjectExplorer");

		IFolderLayout bottomTopLeftFolderLayout = pageLayout.createFolder(
			"bottomTopLeft", IPageLayout.BOTTOM, 0.5F, "topLeft");

		bottomTopLeftFolderLayout.addView(UpgradePlanView.ID);

		IFolderLayout bottomFolderLayout = pageLayout.createFolder("bottom", IPageLayout.BOTTOM, 0.7F, editorArea);

		bottomFolderLayout.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);
		bottomFolderLayout.addView(UpgradePlanInfoView.ID);
		bottomFolderLayout.addView("org.eclipse.ui.views.AllMarkersView");
		bottomFolderLayout.addView("org.eclipse.ui.console.ConsoleView");
	}

}