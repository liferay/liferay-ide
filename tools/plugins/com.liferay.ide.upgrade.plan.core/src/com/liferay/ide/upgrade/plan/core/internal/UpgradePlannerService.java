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
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.upgrade.plan.core.IMemento;
import com.liferay.ide.upgrade.plan.core.UpgradeEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeListener;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanElementStatus;
import com.liferay.ide.upgrade.plan.core.UpgradePlanStartedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.core.UpgradeTask;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;

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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@Component
public class UpgradePlannerService implements UpgradePlanner {

	@Override
	public void addListener(UpgradeListener upgradeListener) {
		synchronized (this) {
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
	}

	@Override
	public void dispatch(UpgradeEvent upgradeEvent) {
		Collection<UpgradeListener> upgradeListeners;

		synchronized (this) {
			upgradeListeners = Collections.unmodifiableCollection(_upgradeListeners);

			_upgradeEvents.add(upgradeEvent);
		}

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

				IMemento ugpradeCategoriesMemento = upgradePlanMemento.getChild("categories");

				List<String> upgradeTaskCategoryIds = new ArrayList<>();

				if (ugpradeCategoriesMemento != null) {
					IMemento[] upgradeTaskCategoryMementos = ugpradeCategoriesMemento.getChildren("category");

					if (ListUtil.isNotEmpty(upgradeTaskCategoryMementos)) {
						upgradeTaskCategoryIds = Stream.of(
							upgradeTaskCategoryMementos
						).map(
							memento -> memento.getString("id")
						).collect(
							Collectors.toList()
						);
					}
				}

				_currentUpgradePlan = new StandardUpgradePlan(
					name, currentVersion, targetVersion, projectPath, upgradeTaskCategoryIds);

				String targetProjectLocationValue = upgradePlanMemento.getString("targetProjectLocation");

				if (targetProjectLocationValue != null) {
					_currentUpgradePlan.setTargetProjectLocation(Paths.get(targetProjectLocationValue));
				}

				_loadActionStatus(upgradePlanMemento, _currentUpgradePlan);

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
		List<String> upgradeTaskCategories) {

		return new StandardUpgradePlan(name, currentVersion, targetVersion, sourceCodeLocation, upgradeTaskCategories);
	}

	@Override
	public void removeListener(UpgradeListener upgradeListener) {
		synchronized (this) {
			_upgradeListeners.remove(upgradeListener);
		}
	}

	@Override
	public void restartStep(UpgradeTaskStep upgradeTaskStep) {
		List<UpgradeTaskStepAction> actions = upgradeTaskStep.getActions();

		Stream<UpgradeTaskStepAction> stream = actions.stream();

		stream.forEach(action -> action.setStatus(UpgradePlanElementStatus.INCOMPLETE));
	}

	@Override
	public void restartTask(UpgradeTask upgradeTask) {
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

			List<String> upgradeTaskCategories = upgradePlan.getUpgradeTaskCategories();

			IMemento categoriesMemento = upgradePlanMemento.createChild("categories");

			Stream<String> stream = upgradeTaskCategories.stream();

			stream.forEach(
				upgradeTaskCategory -> {
					IMemento childMemento = categoriesMemento.createChild("category");

					childMemento.putString("id", upgradeTaskCategory);
				});

			_saveActionStatus(upgradePlanMemento, upgradePlan);

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

	private void _loadActionStatus(IMemento memento, UpgradePlan upgradePlan) {
		List<UpgradeTask> tasks = upgradePlan.getTasks();

		for (UpgradeTask task : tasks) {
			IMemento taskMemento = memento.getChild(task.getId());

			if (taskMemento == null) {
				continue;
			}

			List<UpgradeTaskStep> steps = task.getSteps();

			for (UpgradeTaskStep step : steps) {
				IMemento stepMemento = taskMemento.getChild(step.getId());

				if (stepMemento == null) {
					continue;
				}

				List<UpgradeTaskStepAction> actions = step.getActions();

				String stepStatus = stepMemento.getString("status");

				if (stepStatus != null) {
					step.setStatus(UpgradePlanElementStatus.valueOf(stepStatus));
				}

				for (UpgradeTaskStepAction action : actions) {
					if (action == null) {
						continue;
					}

					IMemento actionMemento = stepMemento.getChild(action.getId());

					if (actionMemento != null) {
						String actionStatus = actionMemento.getString("status");

						action.setStatus(UpgradePlanElementStatus.valueOf(actionStatus));
					}
				}
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

	private void _saveActionStatus(IMemento memento, UpgradePlan upgradePlan) {
		List<UpgradeTask> tasks = upgradePlan.getTasks();

		for (UpgradeTask task : tasks) {
			IMemento taskMemento = memento.getChild(task.getId());

			if (taskMemento == null) {
				taskMemento = memento.createChild(task.getId());
			}

			List<UpgradeTaskStep> steps = task.getSteps();

			for (UpgradeTaskStep step : steps) {
				IMemento stepMemento = taskMemento.getChild(step.getId());

				if (stepMemento == null) {
					stepMemento = taskMemento.createChild(step.getId());
				}

				stepMemento.putString("status", String.valueOf(step.getStatus()));

				List<UpgradeTaskStepAction> actions = step.getActions();

				for (UpgradeTaskStepAction action : actions) {
					IMemento actionMemento = stepMemento.getChild(action.getId());

					if (actionMemento == null) {
						actionMemento = stepMemento.createChild(action.getId());
					}

					actionMemento.putString("status", String.valueOf(action.getStatus()));
				}
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
	private final List<UpgradeEvent> _upgradeEvents = new ArrayList<>();
	private final Set<UpgradeListener> _upgradeListeners = new LinkedHashSet<>();

}