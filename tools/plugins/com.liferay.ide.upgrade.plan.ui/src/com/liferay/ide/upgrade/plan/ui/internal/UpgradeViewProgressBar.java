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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeListener;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanStartedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStepStatus;
import com.liferay.ide.upgrade.plan.core.UpgradeStepStatusChangedEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public class UpgradeViewProgressBar extends Canvas implements UpgradeListener {

	public UpgradeViewProgressBar(Composite parent) {
		super(parent, SWT.NONE);

		_parentComposite = parent;

		addControlListener(
			new ControlAdapter() {

				@Override
				public void controlResized(ControlEvent e) {
					_colorBarWidth = _scale(_currentCompleteStepsCount);

					redraw();
				}

			});

		addPaintListener(
			new PaintListener() {

				@Override
				public void paintControl(PaintEvent e) {
					_paint(e);
				}

			});

		addDisposeListener(
			new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent e) {
					if ((_progressBarColor != null) && !_progressBarColor.isDisposed()) {
						_progressBarColor.dispose();
					}
				}

			});

		Bundle bundle = FrameworkUtil.getBundle(UpgradePlanViewer.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();

		_upgradePlanner = _serviceTracker.getService();

		_display = _parentComposite.getDisplay();

		_progressBarColor = new Color(_display, 95, 191, 95);

		_upgradePlanner.addListener(this);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget();

		Point size = new Point(_defaultWidth, _defaultHeight);

		if (wHint != SWT.DEFAULT) {
			size.x = wHint;
		}

		if (hHint != SWT.DEFAULT) {
			size.y = hHint;
		}

		return size;
	}

	@Override
	public void dispose() {
		_upgradePlanner.removeListener(this);
		_serviceTracker.close();

		Stream.of(
			_parentComposite.getChildren()
		).filter(
			control -> !control.isDisposed()
		).forEach(
			control -> control.dispose()
		);

		super.dispose();
	}

	@Override
	public void onUpgradeEvent(UpgradeEvent upgradeEvent) {
		if (upgradeEvent instanceof UpgradePlanStartedEvent) {
			UpgradePlanStartedEvent upgradePlanStartedEvent = (UpgradePlanStartedEvent)upgradeEvent;

			UpgradePlan upgradePlan = upgradePlanStartedEvent.getUpgradePlan();

			if (upgradePlan != null) {
				_completedUpgradeSteps.clear();

				Set<UpgradeStep> noChildrenUpgradeSteps = new HashSet<>();

				for (UpgradeStep upgradeStep : upgradePlan.getUpgradeSteps()) {
					_calculateNoChildrenSteps(upgradeStep, noChildrenUpgradeSteps);
				}

				_totalStepsCount = noChildrenUpgradeSteps.size();

				Stream<UpgradeStep> leafStepsStream = noChildrenUpgradeSteps.stream();

				_completedUpgradeSteps.addAll(
					leafStepsStream.filter(
						step -> step.completed()
					).collect(
						Collectors.toList()
					));
			}
		}
		else if (upgradeEvent instanceof UpgradeStepStatusChangedEvent) {
			UpgradeStepStatusChangedEvent statusEvent = Adapters.adapt(
				upgradeEvent, UpgradeStepStatusChangedEvent.class);

			UpgradeStepStatus newStatus = statusEvent.getNewStatus();

			UpgradeStep changeEventStep = statusEvent.getUpgradeStep();

			if ((changeEventStep != null) && ListUtil.isEmpty(changeEventStep.getChildren())) {
				if (newStatus.equals(UpgradeStepStatus.COMPLETED) || newStatus.equals(UpgradeStepStatus.SKIPPED)) {
					_completedUpgradeSteps.add(changeEventStep);
				}

				if (newStatus.equals(UpgradeStepStatus.INCOMPLETE)) {
					_completedUpgradeSteps.remove(changeEventStep);
				}
			}
		}

		UIUtil.async(
			() -> {
				_reset(_completedUpgradeSteps.size());
			});
	}

	private void _calculateNoChildrenSteps(UpgradeStep upgradeStep, Set<UpgradeStep> noChildrenUpgradeSteps) {
		List<UpgradeStep> childUpgradeSteps = upgradeStep.getChildren();

		if (ListUtil.isEmpty(childUpgradeSteps)) {
			noChildrenUpgradeSteps.add(upgradeStep);
		}
		else {
			for (UpgradeStep childStep : childUpgradeSteps) {
				_calculateNoChildrenSteps(childStep, noChildrenUpgradeSteps);
			}
		}
	}

	private void _drawBevelRect(GC gc, int x, int y, int w, int h, Color topleft, Color bottomRight) {
		gc.setForeground(topleft);
		gc.drawLine(x, y, x + w - 1, y);
		gc.drawLine(x, y, x, y + h - 1);

		gc.setForeground(bottomRight);
		gc.drawLine(x + w, y, x + w, y + h);
		gc.drawLine(x, y + h, x + w, y + h);
	}

	private void _paint(PaintEvent paintEvent) {
		GC gc = paintEvent.gc;

		if (isDisposed()) {
			return;
		}

		Rectangle rectangle = getClientArea();

		gc.fillRectangle(rectangle);

		_drawBevelRect(
			gc, rectangle.x, rectangle.y, rectangle.width - 1, rectangle.height - 1,
			_display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW),
			_display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));

		_setProgressColor(gc);

		_colorBarWidth = Math.min(rectangle.width - 2, _colorBarWidth);

		gc.fillRectangle(1, 1, _colorBarWidth, rectangle.height - 2);
	}

	private void _reset(int completedStepsCount) {
		boolean changed = false;

		if (_currentCompleteStepsCount != completedStepsCount) {
			changed = true;
		}

		_currentCompleteStepsCount = completedStepsCount;

		if (changed) {
			_colorBarWidth = _scale(completedStepsCount);

			if (isDisposed()) {
				return;
			}

			redraw();
		}
	}

	private int _scale(int value) {
		if (_totalStepsCount > 0) {
			if (isDisposed()) {
				return value;
			}

			Rectangle rectangle = getClientArea();

			if (rectangle.width != 0) {
				return Math.max(0, value * (rectangle.width - 2) / _totalStepsCount);
			}
		}

		return value;
	}

	private void _setProgressColor(GC gc) {
		gc.setBackground(_progressBarColor);
	}

	private static int _defaultHeight = 18;
	private static int _defaultWidth = 160;

	private int _colorBarWidth = 0;
	private Set<UpgradeStep> _completedUpgradeSteps = new CopyOnWriteArraySet<>();
	private int _currentCompleteStepsCount = 0;
	private Display _display;
	private Composite _parentComposite;
	private Color _progressBarColor;
	private ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;
	private int _totalStepsCount = 0;
	private UpgradePlanner _upgradePlanner;

}