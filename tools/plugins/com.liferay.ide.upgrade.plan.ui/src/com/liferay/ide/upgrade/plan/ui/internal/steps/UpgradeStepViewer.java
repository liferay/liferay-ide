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

package com.liferay.ide.upgrade.plan.ui.internal.steps;

import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.ui.Disposable;
import com.liferay.ide.upgrade.plan.ui.internal.UpgradePlanUIPlugin;
import com.liferay.ide.upgrade.plan.ui.internal.UpgradePlanViewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class UpgradeStepViewer implements ISelectionProvider {

	public UpgradeStepViewer(Composite parentComposite, UpgradePlanViewer upgradePlanViewer) {
		_formToolkit = new FormToolkit(parentComposite.getDisplay());

		_scrolledForm = _formToolkit.createScrolledForm(parentComposite);

		_formToolkit.decorateFormHeading(_scrolledForm.getForm());

		Composite composite = _scrolledForm.getBody();

		GridLayoutFactory gridLayoutFactory = GridLayoutFactory.fillDefaults();

		composite.setLayout(gridLayoutFactory.create());

		GridDataFactory gridDataFactory = GridDataFactory.fillDefaults();

		gridDataFactory.grab(true, true);

		composite.setLayoutData(gridDataFactory.create());

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

	private void _clearScrolledForm() {
		if (_upgradeStepItem != null) {
			_upgradeStepItem.dispose();
		}

		Composite bodyComposite = _scrolledForm.getBody();

		if (!bodyComposite.isDisposed()) {
			for (Control control : bodyComposite.getChildren()) {
				if (!control.isDisposed()) {
					control.dispose();
				}
			}
		}
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

	private UpgradeStep _getSelectedUpgradeStep(ISelection selection) {
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection)selection;

			Object object = structuredSelection.getFirstElement();

			if (object instanceof UpgradeStep) {
				return (UpgradeStep)object;
			}
		}

		return null;
	}

	private void _updateFromSelection(ISelection selection) {
		_clearScrolledForm();

		UpgradeStep upgradeStep = _getSelectedUpgradeStep(selection);

		if (upgradeStep != null) {
			_scrolledForm.setText(upgradeStep.getTitle());

			_upgradeStepItem = new UpgradeStepItem(_formToolkit, _scrolledForm, upgradeStep);

			_upgradeStepItem.addSelectionChangedListener(this::_fireSelectionChanged);
		}

		_scrolledForm.reflow(true);
	}

	private List<Disposable> _disposables = new ArrayList<>();
	private FormToolkit _formToolkit;
	private ListenerList<ISelectionChangedListener> _listeners = new ListenerList<>();
	private ScrolledForm _scrolledForm;
	private UpgradeStepItem _upgradeStepItem;

}