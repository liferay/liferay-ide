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

package com.liferay.ide.gradle.core;

import com.liferay.blade.gradle.tooling.ProjectInfo;
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.core.portal.docker.PortalDockerRuntime;
import com.liferay.ide.server.core.portal.docker.PortalDockerServer;

import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteParticipant;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Simon Jiang
 */
public class LiferayGradleWorkspaceProjectDeleteParticipant extends DeleteParticipant {

	public LiferayGradleWorkspaceProjectDeleteParticipant() {
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context)
		throws OperationCanceledException {

		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		IWorkspaceProject workspaceProject = LiferayCore.create(IWorkspaceProject.class, _workspaceProject);

		if (workspaceProject == null) {
			return null;
		}

		CompositeChange change = new CompositeChange(getName());

		if (GradleUtil.isGradleProject(workspaceProject)) {
			return change;
		}

		ProjectInfo projectInfo = LiferayGradleCore.getToolingModel(ProjectInfo.class, _workspaceProject);

		if (Objects.isNull(projectInfo)) {
			return change;
		}

		Stream.of(
			ServerCore.getServers()
		).filter(
			server -> server != null
		).filter(
			server -> {
				PortalDockerServer dockerServer = (PortalDockerServer)server.loadAdapter(PortalDockerServer.class, pm);

				return Objects.nonNull(dockerServer) &&
					   Objects.equals(dockerServer.getContainerName(), projectInfo.getDockerContainerId());
			}
		).forEach(
			server -> {
				RemoveDockerServerChange removeChange = new RemoveDockerServerChange(server);

				change.add(removeChange);
			}
		);

		Stream.of(
			ServerCore.getRuntimes()
		).filter(
			runtime -> runtime != null
		).filter(
			runtime -> {
				PortalDockerRuntime dockerRuntime = (PortalDockerRuntime)runtime.loadAdapter(
					PortalDockerRuntime.class, pm);

				return Objects.nonNull(dockerRuntime) &&
					   Objects.equals(
						   String.join(":", dockerRuntime.getImageRepo(), dockerRuntime.getImageTag()),
						   projectInfo.getDockerImageId());
			}
		).forEach(
			runtime -> {
				RemoveDockerRuntimeChange removeChange = new RemoveDockerRuntimeChange(runtime);

				change.add(removeChange);
			}
		);

		return change;
	}

	@Override
	public String getName() {
		return "Liferay Workpsace Project Bundle's Runtime Cleanup";
	}

	@Override
	protected boolean initialize(Object element) {
		if (!(element instanceof IProject) && !(element instanceof IFolder)) {
			return false;
		}

		if (element instanceof IFolder) {
			IFolder bundlesFolder = (IFolder)element;

			_workspaceProject = bundlesFolder.getProject();
		}
		else if (element instanceof IProject) {
			_workspaceProject = (IProject)element;
		}

		return true;
	}

	private IProject _workspaceProject;

	private class RemoveDockerRuntimeChange extends Change {

		public RemoveDockerRuntimeChange(IRuntime runtime) {
			_runtime = runtime;
		}

		@Override
		public Object getModifiedElement() {
			return _runtime;
		}

		@Override
		public String getName() {
			return "Delete docker runtime.";
		}

		@Override
		public void initializeValidationData(IProgressMonitor pm) {
		}

		@Override
		public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
			return new RefactoringStatus();
		}

		@Override
		public Change perform(IProgressMonitor pm) throws CoreException {
			try {
				_runtime.delete();
			}
			catch (Exception e) {
				ProjectCore.logError("Failed to delete server " + _runtime.getName(), e);
			}

			return null;
		}

		private IRuntime _runtime;

	}

	private class RemoveDockerServerChange extends Change {

		public RemoveDockerServerChange(IServer server) {
			_server = server;
		}

		@Override
		public Object getModifiedElement() {
			return _server;
		}

		@Override
		public String getName() {
			return "Delete docker server.";
		}

		@Override
		public void initializeValidationData(IProgressMonitor pm) {
		}

		@Override
		public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
			return new RefactoringStatus();
		}

		@Override
		public Change perform(IProgressMonitor pm) throws CoreException {
			try {
				_server.delete();
			}
			catch (Exception e) {
				ProjectCore.logError("Failed to delete server " + _server.getName(), e);
			}

			return null;
		}

		private IServer _server;

	}

}