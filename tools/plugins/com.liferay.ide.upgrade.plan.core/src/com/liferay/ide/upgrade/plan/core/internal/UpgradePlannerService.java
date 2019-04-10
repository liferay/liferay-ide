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

package com.liferay.ide.upgrade.plan.core.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.upgrade.plan.core.IMemento;
import com.liferay.ide.upgrade.plan.core.UpgradeEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeListener;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanStartedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStepStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
@Component
public class UpgradePlannerService implements UpgradePlanner {

	@Override
	public void addListener(UpgradeListener upgradeListener) {
		if (_upgradeListeners.contains(upgradeListener)) {
			return;
		}

		_upgradeListeners.add(upgradeListener);

		for (UpgradeEvent upgradeEvent : _upgradeEvents) {
			try {
				upgradeListener.onUpgradeEvent(upgradeEvent);
			}
			catch (Exception e) {
				UpgradePlanCorePlugin.logError("onUpgradeEvent error", e);
			}
		}
	}

	@Override
	public void completeStep(UpgradeStep upgradeStep) {
		upgradeStep.setStatus(UpgradeStepStatus.COMPLETED);

		List<UpgradeStep> children = upgradeStep.getChildren();

		for (UpgradeStep child : children) {
			completeStep(child);
		}
	}

	@Override
	public void dispatch(UpgradeEvent upgradeEvent) {
		Collection<UpgradeListener> upgradeListeners = Collections.unmodifiableCollection(_upgradeListeners);

		_upgradeEvents.add(upgradeEvent);

		for (UpgradeListener upgradeListener : upgradeListeners) {
			try {
				upgradeListener.onUpgradeEvent(upgradeEvent);
			}
			catch (Exception e) {
				UpgradePlanCorePlugin.logError("onUpgradeEvent error", e);
			}
		}
	}

	public void dispose(UpgradePlan upgradePlan) {
		if (upgradePlan != null) {
			List<UpgradeStep> upgradeSteps = upgradePlan.getUpgradeSteps();

			Stream<UpgradeStep> stream = upgradeSteps.stream();

			stream.forEach(UpgradeStep::dispose);
		}
	}

	@Override
	public UpgradePlan getCurrentUpgradePlan() {
		return _currentUpgradePlan;
	}

	@Override
	public UpgradePlan loadUpgradePlan(String name) {
		try (InputStream inputStream = new FileInputStream(_getUpgradePlannerStorageFile())) {
			IMemento rootMemento = XMLMemento.loadMemento(inputStream);

			if (rootMemento == null) {
				return null;
			}

			Optional<IMemento> upgradePlanMementoOptional = Stream.of(
				rootMemento.getChildren("upgradePlan")
			).filter(
				memento -> name.equals(memento.getString("upgradePlanName"))
			).findFirst();

			if (upgradePlanMementoOptional.isPresent()) {
				IMemento upgradePlanMemento = upgradePlanMementoOptional.get();

				String currentVersion = upgradePlanMemento.getString("currentVersion");
				String targetVersion = upgradePlanMemento.getString("targetVersion");

				String currentProjectLocation = upgradePlanMemento.getString("currentProjectLocation");

				Path projectPath = null;

				if (currentProjectLocation != null) {
					projectPath = Paths.get(currentProjectLocation);
				}

				List<UpgradeStep> upgradeSteps = new ArrayList<>();

				_loadUpgradeSteps(upgradePlanMemento, upgradeSteps, null);

				_currentUpgradePlan = new StandardUpgradePlan(
					name, currentVersion, targetVersion, projectPath, upgradeSteps);

				String targetProjectLocationValue = upgradePlanMemento.getString("targetProjectLocation");

				if (targetProjectLocationValue != null) {
					_currentUpgradePlan.setTargetProjectLocation(Paths.get(targetProjectLocationValue));
				}

				_loadUpgradeProblems(upgradePlanMemento, _currentUpgradePlan);

				return _currentUpgradePlan;
			}
		}
		catch (IOException ioe) {
			UpgradePlanCorePlugin.logError("Could not load upgrade plan " + name, ioe);
		}

		return null;
	}

	@Override
	public UpgradePlan newUpgradePlan(String name, String currentVersion, String targetVersion, Path sourceCodeLocation)
		throws IOException {

		UpgradeStepsBuilder upgradeStepsBuilder = new UpgradeStepsBuilder(
			UpgradePlannerService.class.getResourceAsStream(_LIFERAY_UPGRADE_PLUGIN_MARKDOWN));

		List<UpgradeStep> upgradeSteps = upgradeStepsBuilder.build();

		return new StandardUpgradePlan(name, currentVersion, targetVersion, sourceCodeLocation, upgradeSteps);
	}

	@Override
	public void removeListener(UpgradeListener upgradeListener) {
		_upgradeListeners.remove(upgradeListener);
	}

	@Override
	public void restartStep(UpgradeStep upgradeStep) {
		upgradeStep.setStatus(UpgradeStepStatus.INCOMPLETE);

		List<UpgradeStep> children = upgradeStep.getChildren();

		for (UpgradeStep child : children) {
			restartStep(child);
		}
	}

	@Override
	public void saveUpgradePlan(UpgradePlan upgradePlan) {
		XMLMemento xmlMemento = null;

		try (InputStream inputStream = new FileInputStream(_getUpgradePlannerStorageFile())) {
			IMemento rootMemento = XMLMemento.loadMemento(inputStream);

			if (rootMemento == null) {
				rootMemento = XMLMemento.createWriteRoot("upgradePlanner");
			}

			String name = upgradePlan.getName();

			Optional<IMemento> upgradePlanMementoOptional = Stream.of(
				rootMemento.getChildren("upgradePlan")
			).filter(
				memento -> name.equals(memento.getString("upgradePlanName"))
			).findFirst();

			IMemento upgradePlanMemento = upgradePlanMementoOptional.orElse(rootMemento.createChild("upgradePlan"));

			upgradePlanMemento.putString("upgradePlanName", upgradePlan.getName());
			upgradePlanMemento.putString("currentVersion", upgradePlan.getCurrentVersion());
			upgradePlanMemento.putString("targetVersion", upgradePlan.getTargetVersion());

			Path currentProjectLocation = upgradePlan.getCurrentProjectLocation();

			if (currentProjectLocation != null) {
				upgradePlanMemento.putString("currentProjectLocation", currentProjectLocation.toString());
			}

			Path targetProjectLocation = upgradePlan.getTargetProjectLocation();

			if (targetProjectLocation != null) {
				upgradePlanMemento.putString("targetProjectLocation", targetProjectLocation.toString());
			}

			List<UpgradeStep> rootUpgradeSteps = upgradePlan.getUpgradeSteps();

			_saveUpgradeSteps(upgradePlanMemento, rootUpgradeSteps);

			_saveUpgradeProblems(upgradePlanMemento, upgradePlan);

			xmlMemento = (XMLMemento)rootMemento;
		}
		catch (IOException ioe) {
			UpgradePlanCorePlugin.logError("Unable to save upgrade plan to storage.", ioe);
		}

		if (xmlMemento != null) {
			try (FileOutputStream fileOutputStream = new FileOutputStream(_getUpgradePlannerStorageFile())) {
				xmlMemento.save(fileOutputStream);
			}
			catch (IOException ioe) {
				UpgradePlanCorePlugin.logError("Unable to save upgrade plan to storage.", ioe);
			}
		}
	}

	@Override
	public void skipStep(UpgradeStep upgradeStep) {
		upgradeStep.setStatus(UpgradeStepStatus.SKIPPED);

		List<UpgradeStep> children = upgradeStep.getChildren();

		for (UpgradeStep child : children) {
			skipStep(child);
		}
	}

	@Override
	public void startUpgradePlan(UpgradePlan upgradePlan) {
		_currentUpgradePlan = upgradePlan;

		UpgradeEvent upgradeEvent = new UpgradePlanStartedEvent(upgradePlan);

		dispatch(upgradeEvent);
	}

	private File _getUpgradePlannerStorageFile() throws IOException {
		UpgradePlanCorePlugin upgradePlanCorePlugin = UpgradePlanCorePlugin.getInstance();

		IPath stateLocation = upgradePlanCorePlugin.getStateLocation();

		IPath xmlFile = stateLocation.append("upgradePlanner.xml");

		File file = xmlFile.toFile();

		if (!file.exists()) {
			file.createNewFile();
		}

		return file;
	}

	private void _loadUpgradeProblems(IMemento memento, UpgradePlan upgradePlan) {
		IMemento[] upgradeProblemsMemento = memento.getChildren("upgradeProblem");

		List<UpgradeProblem> upgradeProblems = Stream.of(
			upgradeProblemsMemento
		).map(
			upgradeProblemMemento -> {
				String autoCorrectContext = upgradeProblemMemento.getString("autoCorrectContext");
				String html = upgradeProblemMemento.getString("html");
				String summary = upgradeProblemMemento.getString("summary");
				String ticket = upgradeProblemMemento.getString("ticket");
				String title = upgradeProblemMemento.getString("title");
				String type = upgradeProblemMemento.getString("type");
				String uuid = upgradeProblemMemento.getString("uuid");
				String version = upgradeProblemMemento.getString("version");
				int endOffset = upgradeProblemMemento.getInteger("endOffset");
				int lineNumber = upgradeProblemMemento.getInteger("lineNumber");
				long markerId = upgradeProblemMemento.getLong("markerId");
				int markerType = upgradeProblemMemento.getInteger("markerType");
				int startOffset = upgradeProblemMemento.getInteger("startOffset");
				int status = upgradeProblemMemento.getInteger("status");

				IFile[] resources = CoreUtil.findFilesForLocationURI(
					new File(
						upgradeProblemMemento.getString("resourceLocation")
					).toURI());

				UpgradeProblem upgradeProblem = new UpgradeProblem(
					uuid, title, summary, type, ticket, version, resources[0], lineNumber, startOffset, endOffset, html,
					autoCorrectContext, status, markerId, markerType);

				return upgradeProblem;
			}
		).collect(
			Collectors.toList()
		);

		upgradePlan.addUpgradeProblems(upgradeProblems);
	}

	private void _loadUpgradeSteps(IMemento memento, List<UpgradeStep> upgradeSteps, UpgradeStep parentUpgradeStep) {
		IMemento[] upgradeStepMementos = memento.getChildren("upgradeStep");

		for (IMemento upgradeStepMemento : upgradeStepMementos) {
			String title = upgradeStepMemento.getString("title");
			String description = upgradeStepMemento.getString("title");
			String commandId = upgradeStepMemento.getString("commandId");
			String icon = upgradeStepMemento.getString("imagePath");
			String url = upgradeStepMemento.getString("url");
			String status = upgradeStepMemento.getString("status");
			String requirement = upgradeStepMemento.getString("requirement");

			UpgradeStep upgradeStep = new UpgradeStep(
				title, description, icon, url, requirement, UpgradeStepStatus.valueOf(status), commandId,
				parentUpgradeStep);

			if (parentUpgradeStep == null) {
				upgradeSteps.add(upgradeStep);
			}
			else {
				parentUpgradeStep.appendChild(upgradeStep);
			}

			IMemento[] childUpgradeStepMementos = upgradeStepMemento.getChildren("upgradeStep");

			if (childUpgradeStepMementos.length > 0) {
				_loadUpgradeSteps(upgradeStepMemento, upgradeSteps, upgradeStep);
			}
		}
	}

	private void _saveUpgradeProblems(IMemento memento, UpgradePlan upgradePlan) {
		memento.removeChildren("upgradeProblem");

		Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

		for (UpgradeProblem upgradeProblem : upgradeProblems) {
			IMemento upgradeProblemMemento = memento.createChild("upgradeProblem");

			IResource resource = upgradeProblem.getResource();

			IPath location = resource.getLocation();

			if (location == null) {
				continue;
			}

			upgradeProblemMemento.putString("autoCorrectContext", upgradeProblem.getAutoCorrectContext());
			upgradeProblemMemento.putString("html", upgradeProblem.getHtml());
			upgradeProblemMemento.putString("summary", upgradeProblem.getSummary());
			upgradeProblemMemento.putString("ticket", upgradeProblem.getTicket());
			upgradeProblemMemento.putString("title", upgradeProblem.getTitle());
			upgradeProblemMemento.putString("type", upgradeProblem.getType());
			upgradeProblemMemento.putString("uuid", upgradeProblem.getUuid());
			upgradeProblemMemento.putString("version", upgradeProblem.getVersion());
			upgradeProblemMemento.putInteger("endOffset", upgradeProblem.getEndOffset());
			upgradeProblemMemento.putInteger("lineNumber", upgradeProblem.getLineNumber());

			long markerId = upgradeProblem.getMarkerId();

			upgradeProblemMemento.putString("markerId", String.valueOf(markerId));

			upgradeProblemMemento.putInteger("markerType", upgradeProblem.getMarkerType());
			upgradeProblemMemento.putInteger("number", upgradeProblem.getNumber());

			upgradeProblemMemento.putString("resourceLocation", location.toOSString());

			upgradeProblemMemento.putInteger("startOffset", upgradeProblem.getStartOffset());
			upgradeProblemMemento.putInteger("status", upgradeProblem.getStatus());
		}
	}

	private void _saveUpgradeSteps(IMemento memento, List<UpgradeStep> upgradeSteps) {
		IMemento[] childMementos = memento.getChildren("upgradeStep");

		for (UpgradeStep upgradeStep : upgradeSteps) {
			IMemento stepMemento = null;

			for (IMemento childMemento : childMementos) {
				String title = childMemento.getString("title");

				if (StringUtil.equals(upgradeStep.getTitle(), title)) {
					stepMemento = childMemento;

					break;
				}
			}

			if (stepMemento == null) {
				stepMemento = memento.createChild("upgradeStep");
			}

			stepMemento.putString("title", upgradeStep.getTitle());
			stepMemento.putString("description", upgradeStep.getDescription());
			stepMemento.putString("imagePath", upgradeStep.getImagePath());
			stepMemento.putString("url", upgradeStep.getUrl());
			stepMemento.putString("commandId", upgradeStep.getCommandId());
			stepMemento.putString("requirement", String.valueOf(upgradeStep.getRequirement()));
			stepMemento.putString("status", String.valueOf(upgradeStep.getStatus()));

			List<UpgradeStep> children = upgradeStep.getChildren();

			if (!children.isEmpty()) {
				_saveUpgradeSteps(stepMemento, children);
			}
		}
	}

	private static final String _LIFERAY_UPGRADE_PLUGIN_MARKDOWN = "liferay-upgrade-plan.markdown";

	private UpgradePlan _currentUpgradePlan;
	private final Collection<UpgradeEvent> _upgradeEvents = new CopyOnWriteArrayList<>();
	private final Collection<UpgradeListener> _upgradeListeners = new CopyOnWriteArraySet<>();

}