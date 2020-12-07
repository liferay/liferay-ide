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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.ui.util.Editors;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class UpgradeProblemsActionProvider extends CommonActionProvider {

	public UpgradeProblemsActionProvider() {
	}

	@Override
	public void dispose() {
		super.dispose();

		ICommonActionExtensionSite commonActionExtensionSite = getActionSite();

		if (commonActionExtensionSite != null) {
			StructuredViewer structuredViewer = commonActionExtensionSite.getStructuredViewer();

			structuredViewer.removeDoubleClickListener(_doubleClickListener);
		}

		_doubleClickListener = null;
	}

	@Override
	public void fillContextMenu(IMenuManager menuManager) {
		ICommonViewerSite commonViewerSite = _commonActionExtensionSite.getViewSite();

		ISelectionProvider selectionProvider = commonViewerSite.getSelectionProvider();

		ISelection selection = selectionProvider.getSelection();

		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection)selection;

			if (treeSelection.size() > 1) {
				return;
			}

			Object element = treeSelection.getFirstElement();

			if (element instanceof UpgradeProblem) {
				UpgradeProblem upgradeProblem = (UpgradeProblem)element;

				menuManager.removeAll();

				menuManager.add(new MarkDoneAction(selectionProvider));
				menuManager.add(new MarkUndoneAction(selectionProvider));

				if (CoreUtil.isNotNullOrEmpty(upgradeProblem.getAutoCorrectContext())) {
					menuManager.add(new AutoCorrectAction(selectionProvider));
					menuManager.add(new AutoCorrectPreviewAction(selectionProvider));
				}

				menuManager.add(new IgnoreAction(selectionProvider));
				menuManager.add(new IgnoreAlwaysAction(selectionProvider));
			}
		}
	}

	@Override
	public void init(ICommonActionExtensionSite commandActionExtensionSite) {
		_commonActionExtensionSite = commandActionExtensionSite;

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

						File file = upgradeProblem.getResource();

						IFile resource = (IFile)CoreUtil.findResourceForLocationURI(file);

						Editors.open(resource, upgradeProblem.getMarkerId(), offset, length);
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

	private ICommonActionExtensionSite _commonActionExtensionSite;
	private IDoubleClickListener _doubleClickListener;

}