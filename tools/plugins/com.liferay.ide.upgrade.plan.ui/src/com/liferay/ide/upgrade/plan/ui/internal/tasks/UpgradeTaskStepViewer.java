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

import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.ui.Disposable;
import com.liferay.ide.upgrade.plan.ui.internal.UpgradePlanUIPlugin;

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
public class UpgradeTaskStepViewer implements ISelectionProvider {

	public UpgradeTaskStepViewer(Composite compositeParent, UpgradePlanViewer upgradePlanViewer) {
		_formToolkit = new FormToolkit(compositeParent.getDisplay());

		_scrolledForm = _formToolkit.createScrolledForm(compositeParent);

		_scrolledForm.setData("novarrows", Boolean.TRUE);
		_scrolledForm.setDelayedReflow(true);

		Composite composite = _scrolledForm.getBody();

		composite.setLayout(new TableWrapLayout());

		_disposables.add(() -> _formToolkit.dispose());
		_disposables.add(() -> _scrolledForm.dispose());

		_updateFromSelection(upgradePlanViewer.getSelection());

		upgradePlanViewer.addPostSelectionChangedListener(
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

	private UpgradeTaskStep _getSelectedUpgradeTaskStep(ISelection selection) {
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection)selection;

			Object object = structuredSelection.getFirstElement();

			if (object instanceof UpgradeTaskStep) {
				return (UpgradeTaskStep)object;
			}
		}

		return null;
	}

	private void _updateFromSelection(ISelection selection) {
		UpgradeTaskStep upgradeTaskStep = _getSelectedUpgradeTaskStep(selection);

		if (upgradeTaskStep != null) {
			_updateTaskStepItems(upgradeTaskStep);
		}
	}

	private void _updateTaskStepItems(UpgradeTaskStep upgradeTaskStep) {
		for (Disposable upgradeTaskStepItem : _upgradeTaskStepActionItems) {
			upgradeTaskStepItem.dispose();

			_disposables.remove(upgradeTaskStepItem);
		}

		_upgradeTaskStepActionItems.clear();

		_scrolledForm.setText(upgradeTaskStep.getTitle());

		UpgradeTaskStepIntroItem upgradeTaskStepIntroItem = new UpgradeTaskStepIntroItem(
			_scrolledForm, upgradeTaskStep);

		_disposables.add(upgradeTaskStepIntroItem);
		_upgradeTaskStepActionItems.add(upgradeTaskStepIntroItem);

		for (UpgradeTaskStepAction upgradeTaskStepAction : upgradeTaskStep.getActions()) {
			UpgradeTaskStepActionItem upgradeTaskStepActionItem = new UpgradeTaskStepActionItem(
				_scrolledForm, upgradeTaskStepAction);

			_disposables.add(upgradeTaskStepActionItem);
			_upgradeTaskStepActionItems.add(upgradeTaskStepActionItem);

			upgradeTaskStepActionItem.addSelectionChangedListener(this::_fireSelectionChanged);
		}

		_scrolledForm.reflow(true);
	}

	private List<Disposable> _disposables = new ArrayList<>();
	private FormToolkit _formToolkit;
	private ListenerList<ISelectionChangedListener> _listeners = new ListenerList<>();
	private ScrolledForm _scrolledForm;
	private List<Disposable> _upgradeTaskStepActionItems = new ArrayList<>();

}