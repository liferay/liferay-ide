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

package com.liferay.ide.upgrade.problems.ui.internal;

import com.liferay.ide.ui.util.Editors;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;

import java.io.File;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradeProblemsActionProvider extends CommonActionProvider {

	public UpgradeProblemsActionProvider() {
	}

	@Override
	public void dispose() {
		super.dispose();

		ICommonActionExtensionSite commonActionExtensionSite = getActionSite();

		StructuredViewer structuredViewer = commonActionExtensionSite.getStructuredViewer();

		structuredViewer.removeDoubleClickListener(_doubleClickListener);

		_doubleClickListener = null;
	}

	@Override
	public void fillContextMenu(IMenuManager menuManager) {
	}

	@Override
	public void init(ICommonActionExtensionSite commandActionExtensionSite) {
		_doubleClickListener = new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();

				if (selection instanceof TreeSelection) {
					TreeSelection treeSelection = (TreeSelection)selection;

					Object element = treeSelection.getFirstElement();

					if (element instanceof UpgradeProblem) {
						UpgradeProblem upgradeProblem = (UpgradeProblem)element;

						int offset = upgradeProblem.getStartOffset();

						int length = upgradeProblem.getEndOffset() - offset;

						Editors.open(upgradeProblem.getResource(), upgradeProblem.getMarkerId(), offset, length);
					}
					else if (element instanceof FileProblemsContainer) {
						FileProblemsContainer fileProblemsContainer = (FileProblemsContainer)element;

						File file = fileProblemsContainer.getFile();

						Editors.open(file);
					}
				}
			}

		};

		StructuredViewer structuredViewer = commandActionExtensionSite.getStructuredViewer();

		structuredViewer.addDoubleClickListener(_doubleClickListener);
	}

	private IDoubleClickListener _doubleClickListener;

}