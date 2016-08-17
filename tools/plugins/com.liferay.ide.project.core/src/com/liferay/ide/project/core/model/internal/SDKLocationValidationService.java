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

import static com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods.supportsExtOrWebTypePlugin;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;
import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 */
public class SDKLocationValidationService extends ValidationService
{
    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        this.listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( NewLiferayPluginProjectOp.PROP_PROJECT_PROVIDER ).attach( this.listener );
        op().property( NewLiferayPluginProjectOp.PROP_PROJECT_NAME ).attach( this.listener );
        op().property( NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK ).attach( this.listener );
        op().property( NewLiferayPluginProjectOp.PROP_PLUGIN_TYPE ).attach( this.listener );
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        if ( op().getProjectProvider().content().getShortName().equals( "ant" ))
        {

            int countPossibleWorkspaceSDKProjects = SDKUtil.countPossibleWorkspaceSDKProjects();

            if( countPossibleWorkspaceSDKProjects > 1 )
            {
                return StatusBridge.create( ProjectCore.createErrorStatus( "This workspace has more than one SDK. " ) );
            }

            final Path sdkLocation = op().getSdkLocation().content( true );

            if( sdkLocation == null || sdkLocation.isEmpty() )
            {
                return StatusBridge.create( ProjectCore.createErrorStatus( "This sdk location is empty " ) );
            }

            SDK sdk = SDKUtil.createSDKFromLocation( PathBridge.create( sdkLocation ) );

            if( sdk != null )
            {
                IStatus status = sdk.validate(true);

                if( !status.isOK() )
                {
                    return StatusBridge.create( status );
                }
            }
            else
            {
                return StatusBridge.create( ProjectCore.createErrorStatus( "This sdk location is not correct" ) );
            }

            final Path projectLocation = op().getLocation().content();

            final String projectName = op().getProjectName().content();

            IPath projectPath = PathBridge.create( projectLocation );

            if( projectPath != null && projectPath.toFile().exists() )
            {
                return StatusBridge.create(
                    ProjectCore.createErrorStatus(
                        "Project(" + projectName + ") is existed in sdk folder, please set new project name" ) );
            }

            if( op().getPluginType().content().equals( PluginType.web ) )
            {
                if( !supportsExtOrWebTypePlugin( op(), "web" ) )
                {
                    retval = Status.createErrorStatus(
                        "The selected Plugins SDK does not support creating new web type plugins.  " +
                            "Please configure version 7.0 or greater." );
                }
            }
            else if( op().getPluginType().content().equals( PluginType.ext ) )
            {
                if( !supportsExtOrWebTypePlugin( op(), "ext" ) )
                {
                    retval = Status.createErrorStatus(
                        "The selected Plugins SDK does not support creating ext type plugins.  " +
                            "Please configure version 6.2 or less." );
                }
            }
            else if (op().getPluginType().content().equals( PluginType.portlet ))
            {
                final IPortletFramework portletFramework = op().getPortletFramework().content();
                final Version requiredVersion = new Version( portletFramework.getRequiredSDKVersion() );
                final Version sdkVersion = new Version( sdk.getVersion() );

                if( CoreUtil.compareVersions( requiredVersion, sdkVersion ) > 0 )
                {
                    retval =
                        Status.createErrorStatus( "Selected portlet framework requires SDK version at least " +
                            requiredVersion );
                }
            }
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        op().property( NewLiferayPluginProjectOp.PROP_PROJECT_NAME ).detach( this.listener );
        op().property( NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK ).detach( this.listener );
        op().property( NewLiferayPluginProjectOp.PROP_PLUGIN_TYPE ).detach( this.listener );
        op().property( NewLiferayPluginProjectOp.PROP_PROJECT_PROVIDER ).detach( this.listener );

        super.dispose();
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }
}