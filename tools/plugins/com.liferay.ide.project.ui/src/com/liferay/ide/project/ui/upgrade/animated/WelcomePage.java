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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 * @author Terry Jia
 */
public class WelcomePage extends Page {

	public WelcomePage(final Composite parent, int style, LiferayUpgradeDataModel dataModel) {
		super(parent, style, dataModel, welcomePageId, false);
	}

	public void createSpecialDescriptor(Composite parent, int style) {
		final StringBuilder desriptors = new StringBuilder(
			"The Liferay Code Upgrade Tool helps you upgrade Liferay 6.2 plugin ");

		desriptors.append("projects into Liferay 7.0 and 7.1 projects. ");
		desriptors.append("Or upgrade Liferay Workspace 7.0 to 7.1.\n\n");
		desriptors.append("The key functions are described below:\n");
		desriptors.append("       1. Convert Liferay Plugins SDK 6.2 to Liferay Workspace 7.0 or 7.1\n");
		desriptors.append("       2. Convert Liferay Workspace 7.0 to 7.1\n");
		desriptors.append("       3. Update Descriptor files from 6.2 to 7.0 or 7.1, or from 7.0 to 7.1\n");
		desriptors.append("       4. Find Breaking Changes in the API stages that need to be migrated to Liferay 7\n");
		desriptors.append("       5. Update Layout Template files from 6.2 to 7.0 format\n");
		desriptors.append("       6. Automatically Convert Custom JSP Hooks to OSGi modules\n\n");
		desriptors.append("Note:\n");
		desriptors.append("       It is highly recommended that you back-up copies of your original plugin ");
		desriptors.append("files before continuing.\n");
		desriptors.append("       Ext projects are not supported to upgrade in this tool currently.\n");
		desriptors.append("       For more details, please see <a>From Liferay 6 to Liferay 7</a>.\n\n");
		desriptors.append("How to use this tool:\n");
		desriptors.append("       In order to move through various upgrade steps,\n");
		desriptors.append(
			"       use left, right, ✓, X and clicking on each gear to move between the upgrade steps.\n");
		desriptors.append("       When you are finished with an upgrade step, mark it done by selecting ✓ button,\n");
		desriptors.append("       or, if it is not complete or you want to come back to it later, mark it with an X.");

		String url = "https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/from-liferay-6-to-liferay-7";

		Link link = SWTUtil.createHyperLink(this, style, desriptors.toString(), 1, url);

		link.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
	}

	@Override
	public int getGridLayoutCount() {
		return 2;
	}

	@Override
	public boolean getGridLayoutEqualWidth() {
		return false;
	}

	@Override
	public String getPageTitle() {
		return "Welcome to the Liferay Code Upgrade Tool";
	}

}