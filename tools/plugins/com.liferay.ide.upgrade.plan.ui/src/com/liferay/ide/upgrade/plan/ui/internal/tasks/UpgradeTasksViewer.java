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

package com.liferay.ide.upgrade.plan.ui.internal.tasks;

import com.liferay.ide.upgrade.plan.core.UpgradeEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeListener;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanStartedEvent;
import com.liferay.ide.upgrade.plan.ui.internal.NewUpgradePlanDialog;

import java.util.Optional;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradeTasksViewer implements UpgradeListener, IDoubleClickListener {

	public UpgradeTasksViewer(Composite parentComposite) {
		_tableViewer = new TableViewer(parentComposite);

		_tableViewer.setContentProvider(new UpgradeTasksContentProvider());
		_tableViewer.setLabelProvider(new UpgradeTasksLabelProvider());
		_tableViewer.addDoubleClickListener(this);

		_tableViewer.setInput(UpgradeTasksContentProvider.NO_UPGRADE_PLAN_ACTIVE);
	}

	public void addPostSelectionChangedListener(ISelectionChangedListener selectionChangedListener) {
		_tableViewer.addPostSelectionChangedListener(selectionChangedListener);
	}

	@Override
	public void doubleClick(DoubleClickEvent doubleClickEvent) {
		Optional.of(
			doubleClickEvent.getSelection()
		).filter(
			selection -> selection instanceof IStructuredSelection
		).map(
			IStructuredSelection.class::cast
		).map(
			IStructuredSelection::getFirstElement
		).filter(
			UpgradeTasksContentProvider.NO_TASKS::equals
		).ifPresent(
			s -> {
				Viewer viewer = doubleClickEvent.getViewer();

				Control control = viewer.getControl();

				NewUpgradePlanDialog newUpgradePlanDialog = new NewUpgradePlanDialog(control.getShell());

				newUpgradePlanDialog.open();
			}
		);
	}

	public Object getInput() {
		return _tableViewer.getInput();
	}

	public ISelection getSelection() {
		if (_tableViewer != null) {
			return _tableViewer.getSelection();
		}

		return null;
	}

	@Override
	public void onUpgradeEvent(UpgradeEvent upgradeEvent) {
		if (upgradeEvent instanceof UpgradePlanStartedEvent) {
			UpgradePlanStartedEvent upgradePlanStartedEvent = (UpgradePlanStartedEvent)upgradeEvent;

			UpgradePlan upgradePlan = upgradePlanStartedEvent.getUpgradePlan();

			_tableViewer.setInput(upgradePlan);
		}
	}

	private TableViewer _tableViewer;

}