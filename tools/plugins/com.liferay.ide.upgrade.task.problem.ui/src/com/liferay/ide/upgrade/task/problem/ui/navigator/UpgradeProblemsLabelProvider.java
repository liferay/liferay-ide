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

package com.liferay.ide.upgrade.task.problem.ui.navigator;

import com.liferay.ide.ui.navigator.AbstractLabelProvider;
import com.liferay.ide.upgrade.plan.api.Summary;
import com.liferay.ide.upgrade.task.problem.api.FileProblems;
import com.liferay.ide.upgrade.task.problem.api.MigrationProblemsContainer;
import com.liferay.ide.upgrade.task.problem.api.Problem;
import com.liferay.ide.upgrade.task.problem.api.ProjectProblems;
import com.liferay.ide.upgrade.task.problem.ui.UpgradeProblemUI;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;

/**
 * @author Terry Jia
 */
public class UpgradeProblemsLabelProvider extends AbstractLabelProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof ProjectProblems) {
			return getImageRegistry().get("ProjectMigrationProblems");
		}
		else if (element instanceof MigrationProblemsContainer) {
			return getImageRegistry().get("MigrationProblemsContainer");
		}
		else if (element instanceof FileProblems) {
			return getImageRegistry().get("FileProblems");
		}
		else if (element instanceof Problem) {
			return getImageRegistry().get("Problem");
		}

		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Summary) {
			Summary summary = (Summary)element;

			return summary.doLabel();
		}

		return null;
	}

	@Override
	protected void initalizeImageRegistry(ImageRegistry imageRegistry) {
		imageRegistry.put(
			"MigrationProblemsContainer",
			UpgradeProblemUI.imageDescriptorFromPlugin(UpgradeProblemUI.PLUGIN_ID, "icons/liferay_logo_16.png"));
		imageRegistry.put(
			"ProjectMigrationProblems",
			UpgradeProblemUI.imageDescriptorFromPlugin(UpgradeProblemUI.PLUGIN_ID, ISharedImages.IMG_OBJ_FOLDER));
		imageRegistry.put(
			"FileProblems",
			UpgradeProblemUI.imageDescriptorFromPlugin(UpgradeProblemUI.PLUGIN_ID, ISharedImages.IMG_OBJ_FILE));
		imageRegistry.put(
			"Problem",
			UpgradeProblemUI.imageDescriptorFromPlugin(UpgradeProblemUI.PLUGIN_ID, ISharedImages.IMG_OBJS_ERROR_TSK));
	}

}