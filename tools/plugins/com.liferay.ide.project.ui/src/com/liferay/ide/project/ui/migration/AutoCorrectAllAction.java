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

package com.liferay.ide.project.ui.migration;

import com.liferay.blade.api.AutoMigrateException;
import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.Problem;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.upgrade.FileProblems;
import com.liferay.ide.project.core.upgrade.ProblemsContainer;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.core.upgrade.UpgradeProblems;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.project.core.upgrade.BreakingChangeSelectedProject;
import com.liferay.ide.project.core.upgrade.BreakingChangeSimpleProject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author Terry Jia
 */
public class AutoCorrectAllAction extends Action {

	public AutoCorrectAllAction(List<ProblemsContainer> problemsContainerList) {
		_problemsContainerList = problemsContainerList;
	}

	public void run() {
		final BundleContext context = FrameworkUtil.getBundle(AutoCorrectAction.class).getBundleContext();

		WorkspaceJob job = new WorkspaceJob("Auto correcting all of migration problem.") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) {
				IStatus retval = Status.OK_STATUS;

				try {
					if ((_problemsContainerList != null) && (_problemsContainerList.size() > 0)) {
						for (ProblemsContainer problemsContainer : _problemsContainerList) {
							for (UpgradeProblems upgradeProblems : problemsContainer.getProblemsArray()) {
								FileProblems[] fileProblemsArray = upgradeProblems.getProblems();

								for (FileProblems fileProblems : fileProblemsArray) {
									List<Problem> problems = fileProblems.getProblems();

									Set<String> fixed = new HashSet<>();

									for (Problem problem : problems) {
										final IResource file = MigrationUtil.getIResourceFromProblem(problem);

										if ( FileUtil.notExists(file) ) {
											continue;
										}

										String fixedKey =
											file.getLocation().toString() + "," + problem.autoCorrectContext;

										if ((problem.autoCorrectContext == null) || fixed.contains(fixedKey)) {
											continue;
										}

										String autoCorrectKey = null;

										final int filterKeyIndex = problem.autoCorrectContext.indexOf(":");

										if (filterKeyIndex > -1) {
											autoCorrectKey = problem.autoCorrectContext.substring(0, filterKeyIndex);
										}
										else {
											autoCorrectKey = problem.autoCorrectContext;
										}

										final Collection<ServiceReference<AutoMigrator>> refs =
											context.getServiceReferences(
												AutoMigrator.class, "(auto.correct=" + autoCorrectKey + ")");

										for (ServiceReference<AutoMigrator> ref : refs) {
											final AutoMigrator autoMigrator = context.getService(ref);

											int problemsCorrected = autoMigrator.correctProblems(
												problem.file, problems);

											fixed.add(fixedKey);

											if ((problemsCorrected > 0) && (file != null)) {
												IMarker problemMarker = file.findMarker(problem.markerId);

												if ((problemMarker != null) && problemMarker.exists()) {
													problemMarker.delete();
												}
											}
										}

										file.refreshLocal(IResource.DEPTH_ONE, monitor);
									}
								}
							}
						}
					}

					UIUtil.sync(
						new Runnable() {

							@Override
							public void run() {
								IViewPart view = UIUtil.findView(UpgradeView.ID);

								try {
									BreakingChangeSelectedProject selectedProject =
										UpgradeAssistantSettingsUtil.getObjectFromStore(
											BreakingChangeSelectedProject.class);

									StructuredSelection projectSelection = null;
									List<IProject> projects = new ArrayList<>();

									if (selectedProject != null) {
										List<BreakingChangeSimpleProject> selectedProjects =
											selectedProject.getSelectedProjects();

										selectedProjects.stream().forEach(
											breakingProject -> projects.add(
												CoreUtil.getProject(breakingProject.getName())));

										projectSelection = new StructuredSelection(projects.toArray(new IProject[0]));
									}

									new RunMigrationToolAction(
										"Run Migration Tool", view.getViewSite().getShell(), projectSelection).run();
								}
								catch (IOException ioe) {
									ProjectUI.logError(ioe);
								}
							}
						});
				}
				catch (AutoMigrateException | CoreException | InvalidSyntaxException e) {
					return retval = ProjectUI.createErrorStatus("Unable to auto correct problem", e);
				}

				return retval;
			}
		};

		job.schedule();
	}

	private List<ProblemsContainer> _problemsContainerList;

}