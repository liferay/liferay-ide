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

package com.liferay.ide.server.ui.cmd;

import com.liferay.ide.server.remote.IRemoteServerWorkingCopy;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Gregory Amerson
 */
public abstract class RemoteServerCommand extends AbstractOperation {

	public RemoteServerCommand(IRemoteServerWorkingCopy server, String label) {
		super(label);

		this.server = server;
	}

	public abstract void execute();

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		execute();

		return null;
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	public abstract void undo();

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		undo();

		return null;
	}

	protected IRemoteServerWorkingCopy server;

}