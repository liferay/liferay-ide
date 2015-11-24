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
package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.ProjectName;

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
 */
public class ModuleProjectNameValidationService extends ValidationService
{
    private static final String MAVEN_PROJECT_NAME_REGEX = "[A-Za-z0-9_\\-.]+";

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
                if( ! event.property().definition().equals( NewLiferayModuleProjectOp.PROP_FINAL_PROJECT_NAME )
                                && ! event.property().definition().equals( NewLiferayModuleProjectOp.PROP_PROJECT_NAMES )
                                && ! event.property().definition().equals( ProjectName.PROP_PROJECT_NAME ) )
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

        final NewLiferayModuleProjectOp op = op();
        final String currentProjectName = op.getProjectName().content();

        if( currentProjectName != null )
        {
            final IStatus nameStatus = CoreUtil.getWorkspace().validateName( currentProjectName, IResource.PROJECT );

            if( ! nameStatus.isOK() )
            {
                retval = StatusBridge.create( nameStatus );
            }
            else if( isInvalidProjectName( op ) )
            {
                retval = Status.createErrorStatus( "A project with that name already exists." );
            }
            else if( isMavenProject( op ) && ! isValidMavenProjectName( currentProjectName ) )
            {
                retval = Status.createErrorStatus( "The project name is invalid for a maven project" );
            }
            else
            {
                final Path currentProjectLocation = op.getLocation().content( true );

                // double check to make sure this project wont overlap with existing dir
                if( currentProjectName != null && currentProjectLocation != null )
                {
                    final String currentPath = currentProjectLocation.toOSString();
                    final IPath osPath = org.eclipse.core.runtime.Path.fromOSString( currentPath );

                    final IStatus projectStatus =
                        op.getProjectProvider().content().validateProjectLocation( currentProjectName, osPath );

                    if( ! projectStatus.isOK() )
                    {
                        retval = StatusBridge.create( projectStatus );
                    }
                }
            }
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        super.dispose();

        op().detach( listener, "*" );
    }

    private boolean isInvalidProjectName( NewLiferayModuleProjectOp op )
    {
        final String projectName = op.getProjectName().content();

        if( CoreUtil.getProject( projectName ).exists() )
        {
            return true;
        }

        return false;
    }

    private boolean isMavenProject( NewLiferayModuleProjectOp op )
    {
        return "maven-module".equals( op.getProjectProvider().content().getShortName() );
    }

    private boolean isValidMavenProjectName( String currentProjectName )
    {
        // IDE-1349, use the same logic as maven uses to validate artifactId to validate maven project name.
        // See org.apache.maven.model.validation.DefaultModelValidator.validateId();
        return currentProjectName.matches( MAVEN_PROJECT_NAME_REGEX );
    }

    private NewLiferayModuleProjectOp op()
    {
        return context( NewLiferayModuleProjectOp.class );
    }

}
