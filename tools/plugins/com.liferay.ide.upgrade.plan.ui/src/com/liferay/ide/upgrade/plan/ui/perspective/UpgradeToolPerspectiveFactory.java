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

package com.liferay.ide.upgrade.plan.ui.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Terry Jia
 */
public class UpgradeToolPerspectiveFactory implements IPerspectiveFactory {

	public static final String ID = "com.liferay.ide.eclipse.ui.perspective.upgradeToolPerspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		createLayout(layout);
	}

	protected void createLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.4F, editorArea);

		topLeft.addView("org.eclipse.ui.navigator.ProjectExplorer");

		IFolderLayout bottomTopLeft = layout.createFolder("bottomTopLeft", IPageLayout.BOTTOM, 0.7F, "topLeft");

		bottomTopLeft.addView("com.liferay.ide.upgrade.plan.ui.upgradePlanView");

		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.7F, editorArea);

		bottom.addView("com.liferay.ide.upgrade.plan.ui.upgradeInfoView");
		bottom.addView("org.eclipse.ui.views.AllMarkersView");
		bottom.addView("org.eclipse.ui.console.ConsoleView");

		bottom.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);
	}

}