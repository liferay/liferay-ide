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

package com.liferay.ide.upgrade.plan.ui.view.plan;

import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.api.UpgradeTask;
import com.liferay.ide.upgrade.plan.api.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.ui.view.info.UpgradeInfoPage;
import com.liferay.ide.upgrade.plan.ui.view.info.UpgradeInfoView;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Terry Jia
 */
public class TaskStepsViewer implements ISelectionChangedListener {

	public TaskStepsViewer(IViewPart viewPart, Composite parent) {
		_viewPart = viewPart;

		_toolkit = new FormToolkit(parent.getDisplay());

		_form = _toolkit.createScrolledForm(parent);

		_form.setData("novarrows", Boolean.TRUE);
		_form.setText(_title);
		_form.setDelayedReflow(true);

		Composite body = _form.getBody();

		body.setLayout(new TableWrapLayout());
	}

	public void dispose() {
		if (_form != null) {
			_form.dispose();
		}

		if (_toolkit != null) {
			_toolkit.dispose();
		}

		_form = null;
		_toolkit = null;
	}

	public ScrolledForm getForm() {
		return _form;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();

		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection)selection;

			Object element = structuredSelection.getFirstElement();

			if (element instanceof UpgradeTask) {
				_upgradeTask = (UpgradeTask)element;

				_redraw();
			}
		}
	}

	private void _addListener() {
		IViewPart upgradeInfoView = UIUtil.findView("com.liferay.ide.upgrade.plan.ui.upgradeInfoView");

		IExpansionListener expansionListener = null;

		if (upgradeInfoView != null) {
			UpgradeInfoPage upgradeInfoPage = ((UpgradeInfoView)upgradeInfoView).getUpgradeInfoPage(_viewPart);

			if (upgradeInfoPage != null) {
				expansionListener = (IExpansionListener)upgradeInfoPage;
			}
		}

		if (expansionListener != null) {
			for (ViewItem item : _stepItems) {
				item.addExpansionListener(expansionListener);
			}
		}
	}

	private void _redraw() {
		for (ViewItem stepItem : _stepItems) {
			stepItem.dispose();
		}

		_stepItems.clear();

		_form.setText((String)_upgradeTask.getProperty("task.title"));

		ViewItem introItem = new ViewItem(this, new IntroStep(_upgradeTask));

		_stepItems.add(introItem);

		for (UpgradeTaskStep step : _upgradeTask.getSteps()) {
			ViewItem stepItem = new ViewItem(this, step);

			_stepItems.add(stepItem);
		}

		_addListener();

		_form.reflow(true);
	}

	private ScrolledForm _form;
	private List<ViewItem> _stepItems = new ArrayList<>();
	private String _title;
	private FormToolkit _toolkit;
	private UpgradeTask _upgradeTask;
	private IViewPart _viewPart;

}