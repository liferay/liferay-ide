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

import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanAcessor;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class UpgradePlanContentProvider implements ITreeContentProvider, UpgradePlanAcessor {

	public static final Object NO_STEPS = new Object() {

		@Override
		public String toString() {
			return "NO_STEPS";
		}

	};

	public static final Object NO_UPGRADE_PLAN_ACTIVE = new Object() {

		@Override
		public String toString() {
			return "NO_UPGRADE_PLAN_ACTIVE";
		}

	};

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof UpgradeStep) {
			UpgradeStep upgradeStep = (UpgradeStep)parentElement;

			return Stream.of(
				upgradeStep.getChildIds()
			).map(
				this::getStep
			).toArray();
		}

		return null;
	}

	@Override
	public Object[] getElements(Object element) {
		if (NO_UPGRADE_PLAN_ACTIVE.equals(element)) {
			return new Object[] {NO_STEPS};
		}
		else if (element instanceof UpgradePlan) {
			UpgradePlan upgradePlan = (UpgradePlan)element;

			List<UpgradeStep> upgradeSteps = upgradePlan.getRootSteps();

			return upgradeSteps.toArray(new UpgradeStep[0]);
		}

		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (NO_STEPS.equals(element)) {
			return null;
		}
		else if (element instanceof UpgradeStep) {
			UpgradeStep upgradeStep = (UpgradeStep)element;

			return getStep(upgradeStep.getParentId());
		}

		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (NO_STEPS.equals(element)) {
			return false;
		}
		else if (element instanceof UpgradeStep) {
			UpgradeStep upgradeStep = (UpgradeStep)element;

			List<UpgradeStep> children = Stream.of(
				upgradeStep.getChildIds()
			).map(
				this::getStep
			).collect(
				Collectors.toList()
			);

			return !children.isEmpty();
		}

		return false;
	}

}