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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.modules.BaseModuleOp;
import com.liferay.ide.project.core.util.ValidationUtil;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 * @author Andy Wu
 */
public class FragmentProjectNameValidationService extends ValidationService
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
                if( !event.property().definition().equals( BaseModuleOp.PROP_FINAL_PROJECT_NAME ) &&
                    !event.property().definition().equals( BaseModuleOp.PROP_PROJECT_NAMES ) &&
                    !event.property().definition().equals( ProjectName.PROP_PROJECT_NAME ) )
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

        final NewModuleFragmentOp op = op();
        final String currentProjectName = op.getProjectName().content();

        if( !CoreUtil.empty( currentProjectName ) )
        {
            final IStatus nameStatus = CoreUtil.getWorkspace().validateName( currentProjectName, IResource.PROJECT );

            if( !nameStatus.isOK() )
            {
                return StatusBridge.create( nameStatus );
            }

            if( ValidationUtil.isExistingProjectName( currentProjectName ) )
            {
                return Status.createErrorStatus( "A project with that name(ignore case) already exists." );
            }

            if( !isValidProjectName( currentProjectName ) )
            {
                return Status.createErrorStatus( "The project name is invalid." );
            }

            final Path currentProjectLocation = op.getLocation().content( true );

            // double check to make sure this project wont overlap with existing dir
            if( currentProjectLocation != null )
            {
                final String currentPath = currentProjectLocation.toOSString();
                final IPath osPath = org.eclipse.core.runtime.Path.fromOSString( currentPath );

                final IStatus projectStatus =
                    op.getProjectProvider().content().validateProjectLocation( currentProjectName, osPath );

                if( !projectStatus.isOK() )
                {
                    return StatusBridge.create( projectStatus );
                }

                File projectFodler = osPath.append( currentProjectName ).toFile();

                if( projectFodler.exists() && projectFodler.list().length > 0 )
                {
                    return StatusBridge.create(
                        ProjectCore.createErrorStatus( "Target project folder is not empty." ) );
                }
            }
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        if( this.listener != null && op() != null && !op().disposed() )
        {
            op().detach( listener, "*" );

            this.listener = null;
        }

        super.dispose();
    }

    private boolean isValidProjectName( String currentProjectName )
    {
        return currentProjectName.matches( PROJECT_NAME_REGEX );
    }

    private NewModuleFragmentOp op()
    {
        return context( NewModuleFragmentOp.class );
    }

}
