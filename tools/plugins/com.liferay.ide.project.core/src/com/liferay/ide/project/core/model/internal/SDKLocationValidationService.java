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

import static com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods.supportsWebTypePlugin;

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

        op().property( NewLiferayPluginProjectOp.PROP_PROJECT_NAME ).attach( this.listener );

    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final Path sdkLocation = op().getSdkLocation().content( true );

        if( sdkLocation == null || sdkLocation.isEmpty() )
        {
            return StatusBridge.create( ProjectCore.createErrorStatus( "This sdk location is empty " ) );
        }

        SDK sdk = SDKUtil.createSDKFromLocation( PathBridge.create( sdkLocation ) );

        if( sdk != null )
        {
            IStatus status = sdk.validate();

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

        if ( projectPath != null && projectPath.toFile().exists() )
        {
            return StatusBridge.create( ProjectCore.createErrorStatus( "Project(" + projectName + ") is existed in sdk folder, please set new project name" ) );
        }

        if( op().getPluginType().content().equals( PluginType.web ) && ! supportsWebTypePlugin( op() ) )
        {
            retval =
                Status.createErrorStatus( "The selected Plugins SDK does not support creating new web type plugins.  " +
                    "Please configure version 7.0.0 or greater." );
        }



        return retval;
    }

    @Override
    public void dispose()
    {
        super.dispose();

        op().property( NewLiferayPluginProjectOp.PROP_PROJECT_NAME ).detach( this.listener );
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }
}