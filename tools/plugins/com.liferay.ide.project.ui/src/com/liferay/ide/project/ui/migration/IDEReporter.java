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

package com.liferay.ide.project.ui.migration;

import com.liferay.blade.api.Problem;
import com.liferay.blade.api.Reporter;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.ui.upgrade.animated.FindBreakingChangesPage;
import com.liferay.ide.project.ui.upgrade.animated.Page;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView;
import com.liferay.ide.ui.util.UIUtil;

import java.io.OutputStream;

import org.eclipse.jface.viewers.TreeViewer;

/**
 * @author Gregory Amerson
 * @author Lovett Li
 * @author Simon Jiang
 */
public class IDEReporter implements Reporter {

	@Override
	public void beginReporting(int format, OutputStream output) {
	}

	@Override
	public void endReporting() {
		UIUtil.async(
			new Runnable() {

				@Override
				public void run() {
					FindBreakingChangesPage page = UpgradeView.getPage(
						Page.findbreackingchangesPageId, FindBreakingChangesPage.class);

					if (page != null) {
						TreeViewer treeViewer = page.getTreeViewer();

						treeViewer.setInput(CoreUtil.getWorkspaceRoot());

						page.setButtonState(true);
					}
				}

			});
	}

	@Override
	public void report(Problem problem) {
	}

}