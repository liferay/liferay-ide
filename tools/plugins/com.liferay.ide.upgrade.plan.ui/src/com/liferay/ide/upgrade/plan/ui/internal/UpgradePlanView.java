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

import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanStartedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStepPerformedEvent;
import com.liferay.ide.upgrade.plan.core.util.ServicesLookup;
import com.liferay.ide.upgrade.plan.ui.internal.steps.UpgradeStepViewer;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class UpgradePlanView extends ViewPart implements ISelectionProvider {

	public static final String ID = "com.liferay.ide.upgrade.plan.view";

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.add(listener);
	}

	@Override
	public void createPartControl(Composite parentComposite) {
		_createPartControl(parentComposite);

		IViewSite viewSite = getViewSite();

		viewSite.setSelectionProvider(this);
	}

	@Override
	public void dispose() {
		super.dispose();

		if (_upgradePlanViewer != null) {
			_upgradePlanViewer.dispose();
		}

		if (_upgradeStepViewer != null) {
			_upgradeStepViewer.dispose();
		}
	}

	@Override
	public ISelection getSelection() {
		return _upgradeStepViewer.getSelection();
	}

	public UpgradePlanViewer getUpgradePlanViewer() {
		return _upgradePlanViewer;
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		_memento = memento;

		UpgradePlanner upgradePlanner = ServicesLookup.getSingleService(UpgradePlanner.class);

		Optional.ofNullable(
			memento
		).map(
			m -> m.getString("activeUpgradePlanName")
		).filter(
			Objects::nonNull
		).ifPresent(
			upgradePlanName -> {
				UpgradePlan upgradePlan = upgradePlanner.loadUpgradePlan(upgradePlanName);

				upgradePlanner.startUpgradePlan(upgradePlan);
			}
		);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.remove(listener);
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);

		Object upgradePlanViewerInput = _upgradePlanViewer.getInput();

		if (upgradePlanViewerInput instanceof UpgradePlan) {
			UpgradePlan upgradePlan = (UpgradePlan)upgradePlanViewerInput;

			UpgradePlanner upgradePlanner = ServicesLookup.getSingleService(UpgradePlanner.class);

			_saveTreeExpansion(memento, _upgradePlanViewer.getTreeExpansion());

			upgradePlanner.saveUpgradePlan(upgradePlan);

			memento.putString("activeUpgradePlanName", upgradePlan.getName());
		}
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void setSelection(ISelection selection) {
		_upgradeStepViewer.setSelection(selection);
	}

	private void _createPartControl(Composite parentComposite) {
		parentComposite.setLayout(new FillLayout());

		SashForm sashForm = new SashForm(parentComposite, SWT.HORIZONTAL);

		_upgradePlanViewer = new UpgradePlanViewer(sashForm);

		_upgradePlanViewer.addPostSelectionChangedListener(this::_fireSelectionChanged);

		UpgradePlanner upgradePlanner = ServicesLookup.getSingleService(UpgradePlanner.class);

		upgradePlanner.addListener(
			upgradeEvent -> {
				if (upgradeEvent instanceof UpgradePlanStartedEvent) {
					UpgradePlanStartedEvent upgradePlanStartedEvent = (UpgradePlanStartedEvent)upgradeEvent;

					UpgradePlan upgradePlan = upgradePlanStartedEvent.getUpgradePlan();

					if (upgradePlan != null) {
						UIUtil.async(
							() -> {
								setContentDescription("Active upgrade plan: " + upgradePlan.getName());

								if (_memento != null) {
									_upgradePlanViewer.initTreeExpansion(_loadTreeExpansion());
								}
							});
					}
				}
			});

		upgradePlanner.addListener(
			upgradeEvent -> {
				if (upgradeEvent instanceof UpgradeStepPerformedEvent) {
					UIUtil.refreshCommonView("org.eclipse.ui.navigator.ProjectExplorer");
				}
			});

		_upgradeStepViewer = new UpgradeStepViewer(sashForm, _upgradePlanViewer);

		_upgradeStepViewer.addSelectionChangedListener(this::_fireSelectionChanged);

		setContentDescription(
			"No active upgrade plan. Use view menu 'New Upgrade Plan' action to start a new upgrade.");
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

	private String[] _loadTreeExpansion() {
		String stepIdsValue = _memento.getString("stepIds");

		if (stepIdsValue == null) {
			return new String[0];
		}

		String[] stepIds = stepIdsValue.split(",");

		return stepIds;
	}

	private void _saveTreeExpansion(IMemento memento, Object[] expansions) {
		String[] stepIds = Stream.of(
			expansions
		).map(
			expansion -> (UpgradeStep)expansion
		).map(
			upgradeStep -> upgradeStep.getId()
		).toArray(
			String[]::new
		);

		memento.putString("stepIds", StringUtil.merge(stepIds, ","));
	}

	private ListenerList<ISelectionChangedListener> _listeners = new ListenerList<>();
	private IMemento _memento;
	private UpgradePlanViewer _upgradePlanViewer;
	private UpgradeStepViewer _upgradeStepViewer;

}