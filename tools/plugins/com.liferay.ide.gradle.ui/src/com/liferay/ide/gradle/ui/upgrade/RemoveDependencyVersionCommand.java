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

package com.liferay.ide.gradle.ui.upgrade;

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.core.model.GradleBuildScript;
import com.liferay.ide.gradle.core.model.GradleDependency;
import com.liferay.ide.gradle.ui.LiferayGradleUI;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.commands.core.code.RemoveDependencyVersionKeys;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import java.io.File;
import java.io.IOException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Simon Jiang
 * @author Terry Jia
 * @author Seiphon Wang
 */
@Component(
	property = "id=" + RemoveDependencyVersionKeys.ID, scope = ServiceScope.PROTOTYPE, service = UpgradeCommand.class
)
public class RemoveDependencyVersionCommand implements UpgradeCommand {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		boolean removeDependencyVersions = UIUtil.promptQuestion(
			"Remove Dependency Versions", "Do you want remove dependency versions now?");

		if (!removeDependencyVersions) {
			return Status.CANCEL_STATUS;
		}

		Collection<File> buildGradleFiles = GradleUtil.getBuildGradleFiles();

		if (buildGradleFiles == null) {
			return Status.CANCEL_STATUS;
		}

		Stream<File> buildGradleStrem = buildGradleFiles.stream();

		buildGradleStrem.forEach(this::_removeDependencyVersion);

		GradleUtil.refreshProject(LiferayWorkspaceUtil.getWorkspaceProject());

		_upgradePlanner.dispatch(new UpgradeCommandPerformedEvent(this, Collections.singletonList(buildGradleFiles)));

		return Status.OK_STATUS;
	}

	private Artifact _dependencyToArtifact(GradleDependency gradleDependency) {
		return new Artifact(
			gradleDependency.getGroup(), gradleDependency.getName(), gradleDependency.getVersion(),
			gradleDependency.getConfiguration(), null);
	}

	private void _removeDependencyVersion(File buildGradleFile) {
		IWorkspaceProject liferayWorkspaceProject = LiferayWorkspaceUtil.getGradleWorkspaceProject();

		List<Artifact> targetPlatformArtifacts = liferayWorkspaceProject.getTargetPlatformArtifacts();

		try {
			if (FileUtil.notExists(buildGradleFile)) {
				return;
			}

			GradleBuildScript gradleBuildScript = new GradleBuildScript(buildGradleFile);

			List<GradleDependency> dependencies = gradleBuildScript.getDependencies();

			Stream<GradleDependency> dependenciesStream = dependencies.stream();

			List<GradleDependency> dependenciesWithoutVersion = dependenciesStream.map(
				dependency -> {
					dependency.setVersion(null);

					return dependency;
				}
			).filter(
				dependency -> targetPlatformArtifacts.contains(_dependencyToArtifact(dependency))
			).collect(
				Collectors.toList()
			);

			gradleBuildScript.updateDependencies(dependenciesWithoutVersion);
		}
		catch (IOException ioe) {
			LiferayGradleUI.logError(ioe);
		}
	}

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}