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
package com.liferay.ide.gradle.core.workspace;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Andy Wu
 */
public class WorkspaceNameValidationService extends ValidationService
{
    private static final String PROJECT_NAME_REGEX = "[A-Za-z0-9_\\-.]+";

    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                if( ! event.property().definition().equals( NewLiferayWorkspaceOp.PROP_WORKSPACE_NAME ) )
                {
                    refresh();
                }
            }
        };

        op().attach( listener, "*" );
    }

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
            retval = StatusBridge.create( e.getStatus() );

            return retval;
        }

        final NewLiferayWorkspaceOp op = op();
        final String currentWorkspaceName = op.getWorkspaceName().content();

        if( CoreUtil.isNullOrEmpty( currentWorkspaceName ) )
        {
            retval = Status.createErrorStatus( "Liferay Workspace name could not be null" );

            return retval;
        }

        final IStatus nameStatus = CoreUtil.getWorkspace().validateName( currentWorkspaceName, IResource.PROJECT );

        if( !nameStatus.isOK() )
        {
            retval = StatusBridge.create( nameStatus );

            return retval;
        }

        if( !isValidProjectName( currentWorkspaceName ) )
        {
            retval = Status.createErrorStatus( "The name is invalid for a project" );

            return retval;
        }

        if( isExistingFolder( op ) )
        {
            retval = Status.createErrorStatus(
                "There is already a folder at the location \"" + op.getLocation().content().toString() + "\"" );
            return retval;
        }

        if( isInvalidProjectName( op ) )
        {
            retval = Status.createErrorStatus( "A project with that name already exists." );

            return retval;
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        super.dispose();

        op().detach( listener, "*" );
    }

    private boolean isInvalidProjectName( NewLiferayWorkspaceOp op )
    {
        final String workspaceName = op.getWorkspaceName().content();

        if( CoreUtil.getProject( workspaceName ).exists() )
        {
            return true;
        }

        return false;
    }

    private boolean isExistingFolder( NewLiferayWorkspaceOp op )
    {
        final boolean useDefaultLocation = op.getUseDefaultLocation().content( true );

        if( useDefaultLocation )
        {
            Path newLocationBase = null;

            newLocationBase = PathBridge.create( CoreUtil.getWorkspaceRoot().getLocation() );

            if( newLocationBase != null )
            {
                NewLiferayWorkspaceOpMethods.updateLocation( op, newLocationBase );
            }

            if ( op.getLocation().content().toFile().exists() )
            {
                return true ;
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
