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
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.upgrade.plan.core.IMemento;
import com.liferay.ide.upgrade.plan.core.UpgradeEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeListener;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanCorePlugin;
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

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
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

	public List<UpgradePlan> loadAllUpgradePlans() {
		try (InputStream inputStream = new FileInputStream(_getUpgradePlannerStorageFile())) {
			IMemento rootMemento = XMLMemento.loadMemento(inputStream);

			if (rootMemento == null) {
				return Collections.emptyList();
			}

			return Stream.of(
				rootMemento.getChildren("upgradePlan")
			).filter(
				Objects::nonNull
			).map(
				this::_initUpgradePlan
			).filter(
				Objects::nonNull
			).collect(
				Collectors.toList()
			);
		}
		catch (IOException ioe) {
			UpgradePlanCorePlugin.logError("Could not load upgrade plan list.", ioe);
		}

		return Collections.emptyList();
	}

	@Override
	public UpgradePlan loadUpgradePlan(String name) {
		if (name == null) {
			return null;
		}

		try (InputStream inputStream = new FileInputStream(_getUpgradePlannerStorageFile())) {
			IMemento rootMemento = XMLMemento.loadMemento(inputStream);

			if (rootMemento == null) {
				return null;
			}

			Optional<IMemento> upgradePlanMementoOptional = Stream.of(
				rootMemento.getChildren("upgradePlan")
			).filter(
				Objects::nonNull
			).filter(
				memento -> name.equals(memento.getString("upgradePlanName"))
			).findFirst();

			if (upgradePlanMementoOptional.isPresent()) {
				IMemento upgradePlanMemento = upgradePlanMementoOptional.get();

				return _initUpgradePlan(upgradePlanMemento);
			}
		}
		catch (IOException ioe) {
			UpgradePlanCorePlugin.logError("Could not load upgrade plan " + name, ioe);
		}

		return null;
	}

	@Override
	public UpgradePlan newUpgradePlan(
			String name, String currentVersion, String targetVersion, String upgradePlanOutline,
			Map<String, String> upgradeContext)
		throws IOException {

		URL url = new URL(upgradePlanOutline);

		UpgradeStepsBuilder upgradeStepsBuilder = new UpgradeStepsBuilder(url);

		List<UpgradeStep> upgradeSteps = upgradeStepsBuilder.build();

		return new StandardUpgradePlan(
			name, currentVersion, targetVersion, upgradePlanOutline, upgradeSteps, upgradeContext);
	}

	@Override
	public void removeListener(UpgradeListener upgradeListener) {
		_upgradeListeners.remove(upgradeListener);
	}

	@Override
	public void removeUpgradePlan(UpgradePlan upgradePlan) {
		try (InputStream inputStream = new FileInputStream(_getUpgradePlannerStorageFile())) {
			IMemento rootMemento = XMLMemento.loadMemento(inputStream);

			IMemento[] upgradePlanMementos = rootMemento.getChildren("upgradePlan");

			for (IMemento upgradePlanMemento : upgradePlanMementos) {
				String upgradePlanName = upgradePlanMemento.getString("upgradePlanName");

				if (upgradePlanName.equals(upgradePlan.getName())) {
					rootMemento.removeChild(upgradePlanMemento);

					break;
				}
			}

			try (FileOutputStream fileOutputStream = new FileOutputStream(_getUpgradePlannerStorageFile())) {

				((XMLMemento)rootMemento).save(fileOutputStream);
			}
		}
		catch (Exception e) {
			UpgradePlanCorePlugin.logError("Unable to remove upgrade plan " + upgradePlan.getName(), e);
		}
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

		if (ListUtil.isEmpty(upgradePlan.getUpgradeSteps())) {
			return;
		}

		try (InputStream inputStream = new FileInputStream(_getUpgradePlannerStorageFile())) {
			final IMemento rootMemento = Optional.ofNullable(
				XMLMemento.loadMemento(inputStream)
			).orElseGet(
				() -> XMLMemento.createWriteRoot("upgradePlanner")
			);

			String name = upgradePlan.getName();

			IMemento upgradePlanMemento = Stream.of(
				rootMemento.getChildren("upgradePlan")
			).filter(
				memento -> name.equals(memento.getString("upgradePlanName"))
			).findFirst(
			).orElseGet(
				() -> rootMemento.createChild("upgradePlan")
			);

			upgradePlanMemento.putString("upgradePlanName", upgradePlan.getName());
			upgradePlanMemento.putString("currentVersion", upgradePlan.getCurrentVersion());
			upgradePlanMemento.putString("targetVersion", upgradePlan.getTargetVersion());
			upgradePlanMemento.putString("upgradePlanOutline", upgradePlan.getUpgradePlanOutline());

			Map<String, String> upgradeContext = upgradePlan.getUpgradeContext();

			IMemento[] existingUpgradeContextEntries = upgradePlanMemento.getChildren("upgradeContext");

			for (Entry<String, String> entry : upgradeContext.entrySet()) {
				String key = entry.getKey();

				IMemento upgradeContextMemento = null;

				for (IMemento existingUpgradeContextEntry : existingUpgradeContextEntries) {
					String existingKey = existingUpgradeContextEntry.getString("key");

					if (existingKey.equals(key)) {
						upgradeContextMemento = existingUpgradeContextEntry;

						break;
					}
				}

				if (upgradeContextMemento == null) {
					upgradeContextMemento = upgradePlanMemento.createChild("upgradeContext");
				}

				upgradeContextMemento.putString("key", key);
				upgradeContextMemento.putString("value", entry.getValue());
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

	private UpgradePlan _initUpgradePlan(IMemento upgradePlanMemento) {
		String upgradePlanName = upgradePlanMemento.getString("upgradePlanName");

		if (CoreUtil.isNullOrEmpty(upgradePlanName)) {
			return null;
		}

		String currentVersion = upgradePlanMemento.getString("currentVersion");
		String targetVersion = upgradePlanMemento.getString("targetVersion");

		List<UpgradeStep> upgradeSteps = new ArrayList<>();

		_loadUpgradeSteps(upgradePlanMemento, upgradeSteps, null);

		String upgradePlanOutline = upgradePlanMemento.getString("upgradePlanOutline");

		IMemento[] upgradeContextEntries = upgradePlanMemento.getChildren("upgradeContext");

		Map<String, String> upgradeContext = new HashMap<>();

		for (IMemento upgradeContextEntry : upgradeContextEntries) {
			String key = upgradeContextEntry.getString("key");
			String value = upgradeContextEntry.getString("value");

			upgradeContext.put(key, value);
		}

		UpgradePlan currentUpgradePlan = new StandardUpgradePlan(
			upgradePlanName, currentVersion, targetVersion, upgradePlanOutline, upgradeSteps, upgradeContext);

		_loadUpgradeProblems(upgradePlanMemento, currentUpgradePlan);

		return currentUpgradePlan;
	}

	private void _loadUpgradeProblems(IMemento memento, UpgradePlan upgradePlan) {
		IMemento[] upgradeProblemsMemento = memento.getChildren("upgradeProblem");

		List<UpgradeProblem> upgradeProblems = Stream.of(
			upgradeProblemsMemento
		).filter(
			upgradeProblemMemento -> {
				IFile[] resources = CoreUtil.findFilesForLocationURI(
					new File(
						upgradeProblemMemento.getString("resourceLocation")
					).toURI());

				return resources.length > 0;
			}
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
				String resourceLocation = upgradeProblemMemento.getString("resourceLocation");

				UpgradeProblem upgradeProblem = new UpgradeProblem(
					uuid, title, summary, type, ticket, version, new File(resourceLocation), lineNumber, startOffset,
					endOffset, html, autoCorrectContext, status, markerId, markerType);

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
			String summary = upgradeStepMemento.getString("summary");
			String commandId = upgradeStepMemento.getString("commandId");
			String icon = upgradeStepMemento.getString("imagePath");
			String url = upgradeStepMemento.getString("url");
			String status = upgradeStepMemento.getString("status");
			String requirement = upgradeStepMemento.getString("requirement");

			UpgradeStep upgradeStep = new UpgradeStep(
				title, summary, icon, url, requirement, UpgradeStepStatus.valueOf(status), commandId,
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

			File resource = upgradeProblem.getResource();

			String resourceLocation = resource.getAbsolutePath();

			if (CoreUtil.isNullOrEmpty(resourceLocation)) {
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

			upgradeProblemMemento.putString("resourceLocation", resourceLocation);

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
			stepMemento.putString("summary", upgradeStep.getSummary());
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

	private UpgradePlan _currentUpgradePlan;
	private final Collection<UpgradeEvent> _upgradeEvents = new CopyOnWriteArrayList<>();
	private final Collection<UpgradeListener> _upgradeListeners = new CopyOnWriteArraySet<>();

}