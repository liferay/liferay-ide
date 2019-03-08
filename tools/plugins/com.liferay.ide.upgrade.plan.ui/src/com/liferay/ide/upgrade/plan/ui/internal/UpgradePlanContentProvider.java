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
import com.liferay.ide.upgrade.plan.core.UpgradeTask;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradePlanContentProvider implements ITreeContentProvider {

	public static final Object NO_TASKS = new Object() {

		@Override
		public String toString() {
			return "NO_TASKS";
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
		if (parentElement instanceof UpgradeTask) {
			UpgradeTask upgradeTask = (UpgradeTask)parentElement;

			List<UpgradeTaskStep> upgradeTaskSteps = upgradeTask.getSteps();

			return upgradeTaskSteps.toArray(new UpgradeTaskStep[0]);
		}
		else if (parentElement instanceof UpgradeTaskStep) {
			UpgradeTaskStep upgradeTaskStep = (UpgradeTaskStep)parentElement;

			List<UpgradeTaskStepAction> upgradeActions = upgradeTaskStep.getActions();

			return upgradeActions.toArray(new UpgradeTaskStepAction[0]);
		}

		return null;
	}

	@Override
	public Object[] getElements(Object element) {
		if (NO_UPGRADE_PLAN_ACTIVE.equals(element)) {
			return new Object[] {NO_TASKS};
		}
		else if (element instanceof UpgradePlan) {
			UpgradePlan upgradePlan = (UpgradePlan)element;

			List<UpgradeTask> upgradeTasks = upgradePlan.getTasks();

			return upgradeTasks.toArray(new UpgradeTask[0]);
		}

		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (NO_TASKS.equals(element)) {
			return null;
		}

		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (NO_TASKS.equals(element)) {
			return false;
		}
		else if (element instanceof UpgradeTask) {
			return true;
		}
		else if (element instanceof UpgradeTaskStep) {
			UpgradeTaskStep upgradeTaskStep = (UpgradeTaskStep)element;

			List<UpgradeTaskStepAction> actions = upgradeTaskStep.getActions();

			return !actions.isEmpty();
		}

		return false;
	}

}