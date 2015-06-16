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

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;
import org.osgi.framework.Version;


/**
 * @author Gregory Amerson
 */
public class PortletFrameworkValidationService extends ValidationService
{
    private FilteredListener<PropertyContentEvent> listener;

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

        op().property( NewLiferayPluginProjectOp.PROP_PROJECT_PROVIDER ).attach( this.listener );
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final ILiferayProjectProvider projectProvider = op().getProjectProvider().content();
        final IPortletFramework portletFramework = op().getPortletFramework().content();

        if( ! portletFramework.supports( projectProvider ) )
        {
            retval =
                Status.createErrorStatus( "Selected portlet framework is not supported with " +
                    projectProvider.getDisplayName() );
        }
        else
        {
            try
            {
                IProject sdkParentProject = SDKUtil.getWorkspaceSDKProject();

                if( "ant".equals( projectProvider.getShortName() ) && portletFramework != null && sdkParentProject != null )
                {
                    SDK sdk = SDKUtil.createSDKFromLocation( sdkParentProject.getLocation());
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
            catch( CoreException e )
            {
            }
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        op().property( NewLiferayPluginProjectOp.PROP_PROJECT_PROVIDER ).detach( this.listener );

        super.dispose();
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }
}
