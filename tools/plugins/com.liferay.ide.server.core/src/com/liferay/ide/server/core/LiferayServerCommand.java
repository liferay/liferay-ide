/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.server.core;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Terry Jia
 */
public abstract class LiferayServerCommand extends AbstractOperation
{

    protected ILiferayServerWorkingCopy server;

    public LiferayServerCommand( ILiferayServerWorkingCopy server, String label )
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
