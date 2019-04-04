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

import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeListener;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanStartedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStepStatus;
import com.liferay.ide.upgrade.plan.core.UpgradeStepStatusChangedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class UpgradePlanViewer implements UpgradeListener, IDoubleClickListener {

	public UpgradePlanViewer(Composite parentComposite) {
		_treeViewer = new TreeViewer(parentComposite);

		_treeViewer.addDoubleClickListener(this);
		_treeViewer.setContentProvider(new UpgradePlanContentProvider());
		_treeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new UpgradePlanLabelProvider()));

		_treeViewer.setInput(UpgradePlanContentProvider.NO_UPGRADE_PLAN_ACTIVE);

		IContentProvider contentProvider = _treeViewer.getContentProvider();

		Bundle bundle = FrameworkUtil.getBundle(UpgradePlanViewer.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();

		_upgradePlanner = _serviceTracker.getService();

		_upgradePlanner.addListener(this);

		if (contentProvider == null) {
			return;
		}

		_treeContentProvider = Adapters.adapt(contentProvider, ITreeContentProvider.class);
	}

	public void addPostSelectionChangedListener(ISelectionChangedListener selectionChangedListener) {
		_treeViewer.addPostSelectionChangedListener(selectionChangedListener);
	}

	public void dispose() {
		_upgradePlanner.removeListener(this);

		_serviceTracker.close();
	}

	@Override
	public void doubleClick(DoubleClickEvent doubleClickEvent) {
		Optional<Object> selectOptional = Optional.of(
			doubleClickEvent.getSelection()
		).filter(
			selection -> selection instanceof IStructuredSelection
		).map(
			IStructuredSelection.class::cast
		).map(
			IStructuredSelection::getFirstElement
		);

		selectOptional.filter(
			item -> item instanceof UpgradeStep
		).filter(
			_treeContentProvider::hasChildren
		).ifPresent(
			s -> {
				_treeViewer.setExpandedState(s, !_treeViewer.getExpandedState(s));

				_treeViewer.refresh();
			}
		);

		selectOptional.filter(
			UpgradePlanContentProvider.NO_STEPS::equals
		).ifPresent(
			s -> {
				Viewer viewer = doubleClickEvent.getViewer();

				Control control = viewer.getControl();

				WizardDialog wizardDialog = new WizardDialog(control.getShell(), new NewUpgradePlanWizard());

				wizardDialog.open();
			}
		);
	}

	public Object getInput() {
		return _treeViewer.getInput();
	}

	public ISelection getSelection() {
		if (_treeViewer != null) {
			return _treeViewer.getSelection();
		}

		return TreeSelection.EMPTY;
	}

	public Object[] getTreeExpansion() {
		return _treeViewer.getExpandedElements();
	}

	public TreeViewer getTreeViewer() {
		return _treeViewer;
	}

	public void initTreeExpansion(UpgradePlan upgradePlan, List<String> titles) {
		List<UpgradeStep> upgradeSteps = upgradePlan.getUpgradeSteps();
		List<UpgradeStep> matchedSteps = new ArrayList<>();

		for (UpgradeStep upgradeStep : upgradeSteps) {
			_findTitleMathcedSteps(upgradeStep, titles, matchedSteps);
		}

		_treeViewer.setExpandedElements(matchedSteps.toArray(new UpgradeStep[0]));

		refresh();
	}

	@Override
	public void onUpgradeEvent(UpgradeEvent upgradeEvent) {
		if (upgradeEvent instanceof UpgradePlanStartedEvent) {
			UpgradePlanStartedEvent upgradePlanStartedEvent = (UpgradePlanStartedEvent)upgradeEvent;

			UpgradePlan upgradePlan = upgradePlanStartedEvent.getUpgradePlan();

			UIUtil.sync(() -> _treeViewer.setInput(upgradePlan));
		}
		else if (upgradeEvent instanceof UpgradeStepStatusChangedEvent) {
			UIUtil.async(
				() -> {
					UpgradeStepStatusChangedEvent statusEvent = Adapters.adapt(
						upgradeEvent, UpgradeStepStatusChangedEvent.class);

					UpgradeStepStatus newStatus = statusEvent.getNewStatus();

					UpgradeStep changeEventStep = statusEvent.getUpgradeStep();

					StructuredSelection newSelection = new StructuredSelection(changeEventStep);

					if (newStatus.equals(UpgradeStepStatus.COMPLETED) || newStatus.equals(UpgradeStepStatus.SKIPPED)) {
						boolean hasChildren = _treeContentProvider.hasChildren(changeEventStep);

						if ((changeEventStep != null) && !hasChildren) {
							_changeSelection(newSelection);
						}
					}

					refresh();
				});
		}
	}

	public void refresh() {
		if (_treeViewer != null) {
			Object[] elements = _treeViewer.getExpandedElements();
			TreePath[] treePaths = _treeViewer.getExpandedTreePaths();
			_treeViewer.refresh(true);
			_treeViewer.setExpandedElements(elements);
			_treeViewer.setExpandedTreePaths(treePaths);
		}
	}

	private void _changeSelection(ISelection selection) {
		IStructuredSelection structureSelection = (IStructuredSelection)selection;

		Object selectedObject = structureSelection.getFirstElement();

		Object parent = _treeContentProvider.getParent(selectedObject);

		if (parent == null) {
			return;
		}

		UpgradeStep upgradeStep = Adapters.adapt(selectedObject, UpgradeStep.class);

		UpgradeStep nextStep = _findNextStep(upgradeStep, parent);

		if (nextStep != null) {
			_treeViewer.expandToLevel(nextStep, 1, true);

			StructuredSelection newSelection = new StructuredSelection(nextStep);

			_treeViewer.setSelection(newSelection);
		}
		else {
			_treeViewer.collapseToLevel(parent, 1);

			ISelection newSelection = new StructuredSelection(parent);

			_treeViewer.setSelection(newSelection);

			_changeSelection(newSelection);
		}
	}

	private UpgradeStep _findNextStep(UpgradeStep currentUpgradeStep, Object parent) {
		Object[] children = _treeContentProvider.getChildren(parent);

		if (children == null) {
			return null;
		}

		boolean found = false;

		for (Object childObject : children) {
			UpgradeStep upgradeStep = Adapters.adapt(childObject, UpgradeStep.class);

			if (found) {
				return upgradeStep;
			}

			if (upgradeStep.equals(currentUpgradeStep)) {
				found = true;
			}
		}

		return null;
	}

	private void _findTitleMathcedSteps(UpgradeStep upgradeStep, List<String> titles, List<UpgradeStep> resultSteps) {
		if (titles.contains(upgradeStep.getTitle())) {
			resultSteps.add(upgradeStep);
		}

		List<UpgradeStep> childUpgradeSteps = upgradeStep.getChildren();

		for (UpgradeStep childUpgradeStep : childUpgradeSteps) {
			_findTitleMathcedSteps(childUpgradeStep, titles, resultSteps);
		}
	}

	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;
	private ITreeContentProvider _treeContentProvider;
	private TreeViewer _treeViewer;
	private final UpgradePlanner _upgradePlanner;

}