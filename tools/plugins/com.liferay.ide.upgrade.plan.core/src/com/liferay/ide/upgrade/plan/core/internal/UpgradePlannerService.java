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
import com.liferay.ide.upgrade.plan.core.UpgradePlanAcessor;
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

import java.util.Arrays;
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
public class UpgradePlannerService implements UpgradePlanner, UpgradePlanAcessor {

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

				String categoriesValue = upgradePlanMemento.getString("categories");

				String[] categories = categoriesValue.split(",");

				_currentUpgradePlan = new StandardUpgradePlan(
					name, currentVersion, targetVersion, projectPath, Arrays.asList(categories));

				String targetProjectLocationValue = upgradePlanMemento.getString("targetProjectLocation");

				if (targetProjectLocationValue != null) {
					_currentUpgradePlan.setTargetProjectLocation(Paths.get(targetProjectLocationValue));
				}

				List<UpgradeStep> rootSteps = _currentUpgradePlan.getRootSteps();

				_loadStepsStatus(upgradePlanMemento, rootSteps);

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
	public UpgradePlan newUpgradePlan(
		String name, String currentVersion, String targetVersion, Path sourceCodeLocation,
		List<String> upgradeStepCategories) {

		return new StandardUpgradePlan(name, currentVersion, targetVersion, sourceCodeLocation, upgradeStepCategories);
	}

	@Override
	public void removeListener(UpgradeListener upgradeListener) {
		_upgradeListeners.remove(upgradeListener);
	}

	@Override
	public void restartStep(UpgradeStep upgradeStep) {
		upgradeStep.setStatus(UpgradeStepStatus.INCOMPLETE);

		Stream.of(
			upgradeStep.getChildIds()
		).map(
			this::getStep
		).forEach(
			this::restartStep
		);
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

			List<String> upgradeStepCategories = upgradePlan.getUpgradeStepCategories();

			upgradePlanMemento.putString(
				"categories", StringUtil.merge(upgradeStepCategories.toArray(new String[0]), ","));

			List<UpgradeStep> steps = upgradePlan.getRootSteps();

			_saveStepsStatus(upgradePlanMemento, steps);

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

		Stream.of(
			upgradeStep.getChildIds()
		).map(
			this::getStep
		).forEach(
			this::skipStep
		);
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

	private void _loadStepsStatus(IMemento memento, List<UpgradeStep> upgradeSteps) {
		for (UpgradeStep upgradeStep : upgradeSteps) {
			IMemento stepMemento = memento.getChild(upgradeStep.getId());

			if (stepMemento == null) {
				continue;
			}

			String status = stepMemento.getString("status");

			upgradeStep.setStatus(UpgradeStepStatus.valueOf(status));

			List<UpgradeStep> children = Stream.of(
				upgradeStep.getChildIds()
			).map(
				this::getStep
			).collect(
				Collectors.toList()
			);

			if (!children.isEmpty()) {
				_loadStepsStatus(stepMemento, children);
			}
		}
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

				long markerId = 0;

				try {
					markerId = Long.parseLong(upgradeProblemMemento.getString("markerId"));
				}
				catch (NumberFormatException nfe) {
				}

				Integer markerType = upgradeProblemMemento.getInteger("markerType");

				Integer startOffset = upgradeProblemMemento.getInteger("startOffset");
				int status = upgradeProblemMemento.getInteger("status");

				IFile[] resources = CoreUtil.findFilesForLocationURI(
					new File(upgradeProblemMemento.getString("resourceLocation")).toURI());

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

	private void _saveStepsStatus(IMemento memento, List<UpgradeStep> upgradeSteps) {
		for (UpgradeStep upgradeStep : upgradeSteps) {
			IMemento stepMemento = memento.getChild(upgradeStep.getId());

			if (stepMemento == null) {
				stepMemento = memento.createChild(upgradeStep.getId());
			}

			stepMemento.putString("status", String.valueOf(upgradeStep.getStatus()));

			List<UpgradeStep> children = Stream.of(
				upgradeStep.getChildIds()
			).map(
				this::getStep
			).collect(
				Collectors.toList()
			);

			if (!children.isEmpty()) {
				_saveStepsStatus(stepMemento, children);
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

	private UpgradePlan _currentUpgradePlan;
	private final Collection<UpgradeEvent> _upgradeEvents = new CopyOnWriteArrayList<>();
	private final Collection<UpgradeListener> _upgradeListeners = new CopyOnWriteArraySet<>();

}