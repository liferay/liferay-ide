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

package com.liferay.ide.project.core.facet;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action.Type;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetActionEvent;

/**
 * @author Gregory Amerson
 */
public class LiferayFacetedProjectListener implements IFacetedProjectListener {

	public static final String JSDT_FACET = "wst.jsdt.web";

	public void handleEvent(IFacetedProjectEvent event) {
		if (event.getType() != IFacetedProjectEvent.Type.POST_INSTALL) {
			return;
		}

		IProjectFacetActionEvent actionEvent = (IProjectFacetActionEvent)event;

		IProjectFacet projectFacet = actionEvent.getProjectFacet();

		if (!JSDT_FACET.equals(projectFacet.getId())) {
			return;
		}

		Job uninstall = new WorkspaceJob("uninstall jsdt facet") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {

				// first try to remove jsdt from fixed facet list

				try {
					IFacetedProject fProject = actionEvent.getProject();

					Set<IProjectFacet> fixedFacets = fProject.getFixedProjectFacets();

					Set<IProjectFacet> updatedFacets = new HashSet<>();

					for (IProjectFacet f : fixedFacets) {
						if (!JSDT_FACET.equals(f.getId())) {
							updatedFacets.add(f);
						}
					}

					fProject.setFixedProjectFacets(updatedFacets);
				}
				catch (Exception e) {
					ProjectCore.logError("Unable to removed fixed jsdt facet", e);
				}

				// next uninstall the jsdt facet

				try {
					Set<Action> actions = new HashSet<>();

					Type type = Type.valueOf("uninstall");

					Action uninstallJsdt = new Action(type, actionEvent.getProjectFacetVersion(), null);

					actions.add(uninstallJsdt);

					IFacetedProject facetedProject = actionEvent.getProject();

					facetedProject.modify(actions, monitor);

					// try to remove unneeded jsdt files

					IProject project = facetedProject.getProject();

					IFile jsdtscope = project.getFile(".settings/.jsdtscope");

					if (FileUtil.exists(jsdtscope)) {
						jsdtscope.delete(true, monitor);
					}

					IFile container = project.getFile(".settings/org.eclipse.wst.jsdt.ui.superType.container");

					if (FileUtil.exists(container)) {
						container.delete(true, monitor);
					}

					IFile name = project.getFile(".settings/org.eclipse.wst.jsdt.ui.superType.name");

					if (FileUtil.exists(name)) {
						name.delete(true, monitor);
					}
				}
				catch (CoreException ce) {
					ProjectCore.logError("Unable to uninstall jsdt facet", ce);
				}

				return Status.OK_STATUS;
			}

		};

		uninstall.schedule();
	}

}