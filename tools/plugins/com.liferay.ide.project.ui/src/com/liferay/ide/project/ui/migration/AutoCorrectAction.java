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
import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.upgrade.animated.FindBreakingChangesPage;
import com.liferay.ide.project.ui.upgrade.animated.LiferayUpgradeDataModel;
import com.liferay.ide.project.ui.upgrade.animated.Page;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Lovett Li
 */
public class AutoCorrectAction extends ProblemAction {

	public AutoCorrectAction(ISelectionProvider provider) {
		super(provider, "Correct automatically");

		_provider = provider;
	}

	@Override
	public void run() {
		List<Problem> problems = MigrationUtil.getProblemsFromSelection(getSelection());

		runWithAutoCorrect(problems);
	}

	public IStatus runWithAutoCorrect(List<Problem> problems) {

		FindBreakingChangesPage findBreakingChangesPage = UpgradeView.getPage(
			Page.findbreackingchangesPageId, FindBreakingChangesPage.class);

		LiferayUpgradeDataModel liferayUpgradeDataModel = findBreakingChangesPage.getDataModel();

		String upgradeVersion = SapphireUtil.getContent(liferayUpgradeDataModel.getUpgradeVersion());

		_upgradeVersion = upgradeVersion;

		WorkspaceJob job = new WorkspaceJob("Auto correcting breaking changes.") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) {
				IStatus retval = Status.OK_STATUS;

				try {
					Problem problem = problems.get(0);

					String autoCorrectKey = null;

					int filterKeyIndex = problem.autoCorrectContext.indexOf(":");

					if (filterKeyIndex > -1) {
						autoCorrectKey = problem.autoCorrectContext.substring(0, filterKeyIndex);
					}
					else {
						autoCorrectKey = problem.autoCorrectContext;
					}

					Bundle bundle = FrameworkUtil.getBundle(AutoCorrectAction.class);
					BundleContext context = bundle.getBundleContext();

					IResource file = MigrationUtil.getIResourceFromProblem(problems.get(0));

					Collection<ServiceReference<AutoMigrator>> refs = context.getServiceReferences(
						AutoMigrator.class, "(auto.correct=" + autoCorrectKey + ")");

					for (ServiceReference<AutoMigrator> ref : refs) {
						AutoMigrator autoMigrator = context.getService(ref);

						int problemsCorrected = autoMigrator.correctProblems(
							problem.file, Collections.singletonList(problem));

						if (problemsCorrected > 0) {
							IResource resource = MigrationUtil.getIResourceFromProblem(problem);

							if (resource != null) {
								IMarker problemMarker = resource.findMarker(problem.markerId);

								if (MarkerUtil.exists(problemMarker)) {
									problemMarker.delete();
								}
							}
						}
					}

					file.refreshLocal(IResource.DEPTH_ONE, monitor);

					MigrateProjectHandler migrateHandler = new MigrateProjectHandler();

					File problemFile = problem.getFile();

					Path path = new Path(problemFile.getPath());

					String projectName = "";
					IProject project = CoreUtil.getProject(problem.getFile());

					if (FileUtil.exists(project)) {
						projectName = project.getName();
					}

					for (Problem p : problems) {
						new MarkDoneAction().run(p, _provider);
					}

					if (!projectName.equals("")) {
						migrateHandler.findMigrationProblems(
							new Path[] {path}, new String[] {projectName}, _upgradeVersion);
					}
				}
				catch (AutoMigrateException | CoreException | InvalidSyntaxException e) {
					return retval = ProjectUI.createErrorStatus("Unable to auto correct problem", e);
				}

				return retval;
			}

		};

		job.schedule();

		return Status.OK_STATUS;
	}

	@Override
	public void selectionChanged(IStructuredSelection selection) {
		if (selection.isEmpty()) {
			setEnabled(false);
		}
		else {
			boolean selectionCompatible = true;

			Iterator<?> items = selection.iterator();
			Object lastItem = null;

			while (items.hasNext()) {
				Object item = items.next();

				if (!(item instanceof Problem)) {
					selectionCompatible = false;

					break;
				}

				Problem problem = (Problem)item;

				if (problem.autoCorrectContext == null) {
					selectionCompatible = false;

					break;
				}

				if (lastItem != null) {
					String prCurrentKey =
						((Problem)item).autoCorrectContext.substring(0, problem.autoCorrectContext.indexOf(":"));
					String prLastKey =
						((Problem)lastItem).autoCorrectContext.substring(0, problem.autoCorrectContext.indexOf(":"));

					if (!prCurrentKey.equals(prLastKey)) {
						selectionCompatible = false;

						break;
					}
				}

				lastItem = item;
			}

			Iterator<?> items2 = selection.iterator();

			List<String> autoCorrectContexts = new ArrayList<>();

			while (items2.hasNext()) {
				Object item = items2.next();

				if ((item instanceof Problem) && (((Problem)item).autoCorrectContext != null)) {
					autoCorrectContexts.add(((Problem)item).autoCorrectContext);
				}
			}

			setEnabled(selectionCompatible);

			List<String> allAutoCorrectContexts = new ArrayList<>();

			if (_provider instanceof TableViewer) {
				TableViewer viewer = (TableViewer)_provider;

				Object obj = viewer.getInput();

				if (obj instanceof Object[]) {
					Object[] problems = (Object[])obj;

					for (Object o : problems) {
						if (o instanceof Problem && ((Problem)o).autoCorrectContext != null) {
							allAutoCorrectContexts.add(((Problem)o).autoCorrectContext);
						}
					}
				}
				else if (obj instanceof List<?>) {
					List<?> list = (List<?>)obj;

					for (Object p : list) {
						if (p instanceof Problem) {
							Problem problem = (Problem)p;

							if (problem.autoCorrectContext != null) {
								allAutoCorrectContexts.add(problem.autoCorrectContext);
							}
						}
					}
				}
			}

			setText("Correct automatically");
		}
	}

	@Override
	protected IStatus runWithMarker(Problem problem, IMarker marker) {
		return null;
	}

	private String _upgradeVersion;
	private ISelectionProvider _provider;

}