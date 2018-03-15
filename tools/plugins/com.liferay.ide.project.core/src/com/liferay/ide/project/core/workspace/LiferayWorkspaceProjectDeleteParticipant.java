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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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
public class LiferayWorkspaceProjectDeleteParticipant extends DeleteParticipant {

	public LiferayWorkspaceProjectDeleteParticipant() {
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context)
		throws OperationCanceledException {

		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		CompositeChange change = new CompositeChange(getName());

		if (FileUtil.notExists(_workspaceProject)) {
			return change;
		}

		IPath bundlesLocation = LiferayWorkspaceUtil.getHomeLocation(_workspaceProject);

		if (FileUtil.notExists(bundlesLocation)) {
			return change;
		}

		IServer[] servers = ServerCore.getServers();

		Stream<IServer> serversStream = Stream.of(servers);

		serversStream.filter(
			server -> server != null
		).filter(
			server -> {
				IRuntime runtime = server.getRuntime();

				return bundlesLocation.equals(runtime.getLocation());
			}
		).forEach(
			server -> {
				RemoveServerRuntimeChange removeChange = new RemoveServerRuntimeChange(server);

				change.add(removeChange);
			}
		);

		return change;
	}

	@Override
	public String getName() {
		return _delete_server_and_runtime_from_workspace_project;
	}

	@Override
	protected boolean initialize(Object element) {
		if (!(element instanceof IProject)) {
			return false;
		}

		_workspaceProject = (IProject)element;

		return true;
	}

	private static String _actions_delete_for_workspace_project = "Delete runtime and server.";
	private static String _delete_server_and_runtime_from_workspace_project =
		"Liferay Workpsace Project Bundle's Runtime Cleanup";

	private IProject _workspaceProject;

	private class RemoveServerRuntimeChange extends Change {

		public RemoveServerRuntimeChange(IServer server) {
			_server = server;
		}

		@Override
		public Object getModifiedElement() {
			return _server;
		}

		@Override
		public String getName() {
			return _actions_delete_for_workspace_project;
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
				IRuntime runtime = _server.getRuntime();
				_server.delete();

				if (runtime != null) {
					runtime.delete();
				}
			}
			catch (Exception e) {
				ProjectCore.logError("Failed to delete server " + _server.getName(), e);
			}

			return null;
		}

		private IServer _server;

	}

}