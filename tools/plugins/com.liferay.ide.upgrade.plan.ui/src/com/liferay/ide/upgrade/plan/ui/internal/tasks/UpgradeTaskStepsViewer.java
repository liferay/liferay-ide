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

import com.liferay.ide.upgrade.plan.core.UpgradeTask;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.ui.Disposable;
import com.liferay.ide.upgrade.plan.ui.UpgradePlanUIPlugin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradeTaskStepsViewer implements ISelectionProvider {

	public UpgradeTaskStepsViewer(Composite compositeParent, UpgradeTasksViewer upgradeTasksViewer) {
		_formToolkit = new FormToolkit(compositeParent.getDisplay());

		_scrolledForm = _formToolkit.createScrolledForm(compositeParent);

		_scrolledForm.setData("novarrows", Boolean.TRUE);
		_scrolledForm.setDelayedReflow(true);

		Composite composite = _scrolledForm.getBody();

		composite.setLayout(new TableWrapLayout());

		_disposables.add(() -> _formToolkit.dispose());
		_disposables.add(() -> _scrolledForm.dispose());

		_updateFromSelection(upgradeTasksViewer.getSelection());

		upgradeTasksViewer.addPostSelectionChangedListener(
			selectionChangedEvent -> {
				ISelection selection = selectionChangedEvent.getSelection();

				_updateFromSelection(selection);
			});
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.add(listener);
	}

	public void dispose() {
		for (Disposable disposable : _disposables) {
			try {
				disposable.dispose();
			}
			catch (Throwable t) {
			}
		}
	}

	@Override
	public ISelection getSelection() {
		return null;
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
	}

	private void _fireSelectionChanged(SelectionChangedEvent selectionChangedEvent) {
		_listeners.forEach(
			selectionChangedListener -> {
				try {
					selectionChangedListener.selectionChanged(selectionChangedEvent);
				}
				catch (Exception e) {
					UpgradePlanUIPlugin.logError("Error in selection changed listener.", e);
				}
			});
	}

	private UpgradeTask _getSelectedUpgradeTask(ISelection selection) {
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection)selection;

			Object object = structuredSelection.getFirstElement();

			if (object instanceof UpgradeTask) {
				return (UpgradeTask)object;
			}
		}

		return null;
	}

	private void _updateFromSelection(ISelection selection) {
		UpgradeTask upgradeTask = _getSelectedUpgradeTask(selection);

		if (upgradeTask != null) {
			_updateTaskStepItems(upgradeTask);
		}
	}

	private void _updateTaskStepItems(UpgradeTask upgradeTask) {
		for (UpgradeTaskStepItem upgradeTaskStepItem : _upgradeTaskStepItems) {
			upgradeTaskStepItem.dispose();

			_disposables.remove(upgradeTaskStepItem);
		}

		_upgradeTaskStepItems.clear();

		_scrolledForm.setText(upgradeTask.getTitle());

		for (UpgradeTaskStep upgradeTaskStep : upgradeTask.getSteps()) {
			UpgradeTaskStepItem upgradeTaskStepItem = new UpgradeTaskStepItem(_scrolledForm, upgradeTaskStep);

			_disposables.add(upgradeTaskStepItem);
			_upgradeTaskStepItems.add(upgradeTaskStepItem);

			upgradeTaskStepItem.addSelectionChangedListener(this::_fireSelectionChanged);
		}

		_scrolledForm.reflow(true);
	}

	private List<Disposable> _disposables = new ArrayList<>();
	private FormToolkit _formToolkit;
	private ListenerList<ISelectionChangedListener> _listeners = new ListenerList<>();
	private ScrolledForm _scrolledForm;
	private List<UpgradeTaskStepItem> _upgradeTaskStepItems = new ArrayList<>();

}