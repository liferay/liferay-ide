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
package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;


/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 * @author Terry Jia
 * @author Simon Jiang
 */
public class ProjectNameValidationService extends ValidationService
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
                if( ! event.property().definition().equals( NewLiferayPluginProjectOp.PROP_FINAL_PROJECT_NAME )
                                && ! event.property().definition().equals( NewLiferayPluginProjectOp.PROP_PROJECT_NAMES )
                                && ! event.property().definition().equals( NewLiferayPluginProjectOp.PROP_PROJECT_NAME ) )
                {
                    refresh();
                }
            }
        };

        op().attach( listener, "*" ); //$NON-NLS-1$
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final NewLiferayPluginProjectOp op = op();

        if( op().getProjectProvider().content().getShortName().equals( "ant" ) )
        {
            SDK sdk = null;
            try
            {
                sdk = SDKUtil.getWorkspaceSDK();

                if( sdk != null )
                {
                    IStatus sdkStatus = sdk.validate();

                    if( !sdkStatus.isOK() )
                    {
                        retval = Status.createErrorStatus( sdkStatus.getChildren()[0].getMessage() );
                    }
                }
            }
            catch( CoreException e )
            {
                retval = Status.createErrorStatus( e );
            }
        }

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
            else if( isAntProject( op ) && isSuffixOnly( currentProjectName ) )
            {
                retval = Status.createErrorStatus( "A project name cannot only be a type suffix." );
            }
            else if( ! hasValidDisplayName( currentProjectName) )
            {
                retval = Status.createErrorStatus( "The project name is invalid." );
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

        op.getSdkLocation().refresh();

        return retval;
    }

    @Override
    public void dispose()
    {
        super.dispose();

        op().detach( listener, "*" );
    }

    private boolean hasValidDisplayName( String currentProjectName )
    {
        final String currentDisplayName = ProjectUtil.convertToDisplayName( currentProjectName );

        return ! CoreUtil.isNullOrEmpty( currentDisplayName );
    }

    private boolean isAntProject( NewLiferayPluginProjectOp op )
    {
        return "ant".equals( op.getProjectProvider().content().getShortName() );
    }

    private boolean isInvalidProjectName( NewLiferayPluginProjectOp op )
    {
        final String projectName = op.getProjectName().content();

        if( CoreUtil.getProject( projectName ).exists() )
        {
            return true;
        }

        if( "ant".equals( op.getProjectProvider().content().getShortName() ) )
        {
            String pluginTypeValue;

            switch( op.getPluginType().content() )
            {
            case servicebuilder:
            case portlet:
                pluginTypeValue = ISDKConstants._PORTLET_PLUGIN_PROJECT_SUFFIX;
                break;

            case hook:
                pluginTypeValue = ISDKConstants._HOOK_PLUGIN_PROJECT_SUFFIX;
                break;

            case ext:
                pluginTypeValue = ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX;
                break;

            case layouttpl:
                pluginTypeValue = ISDKConstants._LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;
                break;

            case theme:
                pluginTypeValue = ISDKConstants._THEME_PLUGIN_PROJECT_SUFFIX;
                break;

            case web:
                pluginTypeValue = ISDKConstants._WEB_PLUGIN_PROJECT_SUFFIX;
                break;

            default:
                pluginTypeValue = ISDKConstants._PORTLET_PLUGIN_PROJECT_SUFFIX;
            }

            if( !projectName.endsWith( pluginTypeValue ) )
            {
                return CoreUtil.getProject( projectName + pluginTypeValue ).exists();
            }
        }

        return false;
    }

    private boolean isMavenProject( NewLiferayPluginProjectOp op )
    {
        return "maven".equals( op.getProjectProvider().content().getShortName() );
    }

    private boolean isSuffixOnly( String currentProjectName )
    {
        for( PluginType type : PluginType.values() )
        {
            if( ( ( ! type.equals( PluginType.servicebuilder ) ) && ( "-" + type.name() ).equals( currentProjectName ) ) )
            {
                return true;
            }
        }

        return false;
    }

    private boolean isValidMavenProjectName( String currentProjectName )
    {
        // IDE-1349, use the same logic as maven uses to validate artifactId to validate maven project name.
        // See org.apache.maven.model.validation.DefaultModelValidator.validateId();
        return currentProjectName.matches( MAVEN_PROJECT_NAME_REGEX );
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }

}
