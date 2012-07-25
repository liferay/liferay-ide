/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package com.liferay.ide.server.ui.cmd;

import com.liferay.ide.server.remote.IRemoteServerWorkingCopy;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * A command on a Remote server.
 */
public abstract class RemoteServerCommand extends AbstractOperation
{

    protected IRemoteServerWorkingCopy server;

    /**
     * ServerCommand constructor comment.
     * 
     * @param server
     *            a Tomcat server
     * @param label
     *            a label
     */
    public RemoteServerCommand( IRemoteServerWorkingCopy server, String label )
    {
        super( label );
        this.server = server;
    }

    public IStatus redo( IProgressMonitor monitor, IAdaptable info ) throws ExecutionException
    {
        return execute( monitor, info );
    }

    public abstract void execute();

    public IStatus execute( IProgressMonitor monitor, IAdaptable info ) throws ExecutionException
    {
        execute();
        return null;
    }

    public abstract void undo();

    public IStatus undo( IProgressMonitor monitor, IAdaptable info ) throws ExecutionException
    {
        undo();
        return null;
    }
}
