/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.project.ui;

import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.ui.LiferayUIPlugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.internal.preferences.WorkbenchSettingsTransfer;

/**
 * @author Andy Wu
 */
@SuppressWarnings( "restriction" )
public class CreateLiferay7WorkspaceTransfer extends WorkbenchSettingsTransfer
{

    public CreateLiferay7WorkspaceTransfer()
    {
        super();
    }

    @Override
    public IStatus transferSettings( IPath newWorkspaceRoot )
    {
        IStatus status = Status.OK_STATUS;

        StringBuilder sb = new StringBuilder();

        sb.append( "-b " );
        sb.append( "\"" + newWorkspaceRoot.toFile().getAbsolutePath() + "\" " );
        sb.append( "init " );
        sb.append( "-f" );

        try
        {
            BladeCLI.execute( sb.toString() );
        }
        catch( BladeCLIException e )
        {
            status = new Status(IStatus.ERROR,LiferayUIPlugin.PLUGIN_ID,e.getMessage());
        }

        return status;
    }

    @Override
    public String getName()
    {
        return "Create Liferay 7 Workspace";
    }

}
