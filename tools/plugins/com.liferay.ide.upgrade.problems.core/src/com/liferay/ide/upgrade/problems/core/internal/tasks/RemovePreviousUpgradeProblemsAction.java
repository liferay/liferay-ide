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

package com.liferay.ide.upgrade.problems.core.internal.tasks;

import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepActionPerformedEvent;
import com.liferay.ide.upgrade.tasks.core.MessagePrompt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"description=" + RemovePreviousUpgradeProblemsStepKeys.DESCRIPTION, "id=remove_previous_upgrade_problems",
		"order=1", "stepId=remove_previous_upgrade_problems", "title=" + RemovePreviousUpgradeProblemsStepKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class RemovePreviousUpgradeProblemsAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		boolean result = _messagePrompt.prompt("Remove Previous Result", "Are you sure to remove the previous result?");

		if (result) {
			UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

			Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

			Stream<UpgradeProblem> stream = upgradeProblems.stream();

			stream.map(
				this::_findMarker
			).filter(
				MarkerUtil::exists
			).forEach(
				this::_deleteMarker
			);

			_upgradePlanner.dispatch(new UpgradeTaskStepActionPerformedEvent(this, new ArrayList<>(upgradeProblems)));

			upgradeProblems.clear();
		}

		return Status.OK_STATUS;
	}

	private void _deleteMarker(IMarker marker) {
		try {
			marker.delete();
		}
		catch (CoreException ce) {
		}
	}

	private IMarker _findMarker(UpgradeProblem upgradeProblem) {
		IResource resource = upgradeProblem.getResource();

		try {
			return resource.findMarker(upgradeProblem.getMarkerId());
		}
		catch (CoreException ce) {
			return null;
		}
	}

	@Reference
	private MessagePrompt _messagePrompt;

	@Reference
	private UpgradePlanner _upgradePlanner;

}