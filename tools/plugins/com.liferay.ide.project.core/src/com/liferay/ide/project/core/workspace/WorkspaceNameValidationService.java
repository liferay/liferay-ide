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
package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.ValidationUtil;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Andy Wu
 */
public class WorkspaceNameValidationService extends ValidationService
{
    private static final String PROJECT_NAME_REGEX = "[A-Za-z0-9_\\-.]+";

    private Listener listener;

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        try
        {
            if( LiferayWorkspaceUtil.hasLiferayWorkspace() )
            {
                retval = Status.createErrorStatus( LiferayWorkspaceUtil.hasLiferayWorkspaceMsg );

                return retval;
            }
        }
        catch( CoreException e )
        {
            return StatusBridge.create( e.getStatus() );
        }

        final NewLiferayWorkspaceOp op = op();
        final String currentWorkspaceName = op.getWorkspaceName().content();

        if( CoreUtil.isNullOrEmpty( currentWorkspaceName ) )
        {
            return Status.createErrorStatus( "Liferay Workspace name could not be empty." );
        }

        final IStatus nameStatus = CoreUtil.getWorkspace().validateName( currentWorkspaceName, IResource.PROJECT );

        if( !nameStatus.isOK() )
        {
            return StatusBridge.create( nameStatus );
        }

        if( !isValidProjectName( currentWorkspaceName ) )
        {
            return Status.createErrorStatus( "The name is invalid for a project." );
        }

        if( ValidationUtil.isExistingProjectName( currentWorkspaceName ) )
        {
            return Status.createErrorStatus( "A project with that name(ignore case) already exists." );
        }

        if( isExistingFolder( op, currentWorkspaceName ) )
        {
            return Status.createErrorStatus( "Target project folder is not empty." );
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        super.dispose();

        if( this.listener != null )
        {
            op().getLocation().detach( this.listener );

            this.listener = null;
        }
    }

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        this.listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().getLocation().attach( this.listener );
    }

    private boolean isExistingFolder( NewLiferayWorkspaceOp op, String projectName )
    {
        Path location = op.getLocation().content();

        if( location != null )
        {
            File targetDir = location.append( projectName ).toFile();

            if( targetDir.exists() && targetDir.list().length > 0 )
            {
                return true;
            }
        }

        return false;
    }

    private boolean isValidProjectName( String currentProjectName )
    {
        return currentProjectName.matches( PROJECT_NAME_REGEX );
    }

    private NewLiferayWorkspaceOp op()
    {
        return context( NewLiferayWorkspaceOp.class );
    }
}
