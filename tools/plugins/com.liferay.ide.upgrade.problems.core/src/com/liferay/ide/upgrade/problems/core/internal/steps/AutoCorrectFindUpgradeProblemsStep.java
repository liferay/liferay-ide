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

package com.liferay.ide.upgrade.problems.core.internal.steps;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStepPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeStepStatus;
import com.liferay.ide.upgrade.problems.core.FileMigration;
import com.liferay.ide.upgrade.problems.core.MarkerSupport;
import com.liferay.ide.upgrade.problems.core.steps.AutoCorrectFindUpgradeProblemsStepKeys;
import com.liferay.ide.upgrade.problems.core.steps.AutoCorrectUpgradeProblemsStepKeys;
import com.liferay.ide.upgrade.steps.core.ResourceSelection;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"description=" + AutoCorrectFindUpgradeProblemsStepKeys.DESCRIPTION,
		"imagePath=icons/auto_correct_problems.png", "id=" + AutoCorrectFindUpgradeProblemsStepKeys.ID, "order=1",
		"requirement=required", "parentId=" + AutoCorrectUpgradeProblemsStepKeys.ID,
		"title=" + AutoCorrectFindUpgradeProblemsStepKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeStep.class
)
public class AutoCorrectFindUpgradeProblemsStep extends BaseUpgradeStep implements MarkerSupport {

	@Activate
	public void activate(ComponentContext componentContext) {
		super.activate(_upgradePlanner, componentContext);
	}

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select projects to search for upgrade problems.", true, ResourceSelection.JAVA_PROJECTS);

		if (projects.isEmpty()) {
			return Status.CANCEL_STATUS;
		}

		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		List<String> upgradeVersions = upgradePlan.getUpgradeVersions();

		Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

		Stream<UpgradeProblem> ugradeProblemsStream = upgradeProblems.stream();

		ugradeProblemsStream.map(
			upgradeProblem -> findMarker(upgradeProblem.getResource(), upgradeProblem.getMarkerId())
		).filter(
			this::markerExists
		).forEach(
			this::deleteMarker
		);

		upgradeProblems.clear();

		Stream<IProject> stream = projects.stream();

		stream.forEach(
			project -> {
				File searchFile = FileUtil.getFile(project);

				List<UpgradeProblem> foundUpgradeProblems = _fileMigration.findUpgradeProblems(
					searchFile, upgradeVersions, Collections.singleton("auto.correct"), progressMonitor);

				upgradePlan.addUpgradeProblems(foundUpgradeProblems);

				_addMarkers(foundUpgradeProblems);
			});

		setStatus(UpgradeStepStatus.COMPLETED);

		_upgradePlanner.dispatch(new UpgradeStepPerformedEvent(this, new ArrayList<>(upgradeProblems)));

		return Status.OK_STATUS;
	}

	private void _addMarkers(List<UpgradeProblem> upgradeProblems) {
		Stream<UpgradeProblem> stream = upgradeProblems.stream();

		stream.filter(
			upgradeProblem -> FileUtil.exists(upgradeProblem.getResource())
		).forEach(
			upgradeProblem -> {
				IResource resource = upgradeProblem.getResource();

				try {
					IMarker marker = resource.createMarker(UpgradeProblem.MARKER_TYPE);

					upgradeProblem.setMarkerId(marker.getId());

					_upgradeProblemToMarker(upgradeProblem, marker);
				}
				catch (CoreException ce) {
				}
			}
		);
	}

	private void _upgradeProblemToMarker(UpgradeProblem upgradeProblem, IMarker marker) throws CoreException {
		marker.setAttribute(IMarker.CHAR_START, upgradeProblem.getStartOffset());
		marker.setAttribute(IMarker.CHAR_END, upgradeProblem.getEndOffset());
		marker.setAttribute(IMarker.LINE_NUMBER, upgradeProblem.getLineNumber());
		marker.setAttribute(IMarker.MESSAGE, upgradeProblem.getTitle());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_AUTOCORRECTCONTEXT, upgradeProblem.getAutoCorrectContext());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_HTML, upgradeProblem.getHtml());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_SUMMARY, upgradeProblem.getSummary());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_STATUS, upgradeProblem.getStatus());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_TICKET, upgradeProblem.getTicket());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_TYPE, upgradeProblem.getType());

		IResource resource = upgradeProblem.getResource();

		marker.setAttribute(IMarker.LOCATION, resource.getName());

		marker.setAttribute(IMarker.SEVERITY, upgradeProblem.getMarkerType());
	}

	@Reference
	private FileMigration _fileMigration;

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}