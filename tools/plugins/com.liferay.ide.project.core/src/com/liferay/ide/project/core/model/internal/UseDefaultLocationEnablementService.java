/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.services.EnablementService;
import org.eclipse.sapphire.services.EnablementServiceData;
import org.osgi.framework.Version;


/**
 * @author Gregory Amerson
 */
public class UseDefaultLocationEnablementService extends EnablementService
{

    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initEnablementService()
    {
        super.initEnablementService();

        this.listener = new FilteredListener<PropertyContentEvent>()
        {
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( NewLiferayPluginProjectOp.PROP_PLUGINS_SDK_NAME ).attach( this.listener );
        op().property( NewLiferayPluginProjectOp.PROP_PROJECT_PROVIDER ).attach( this.listener );
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }

    @Override
    public void dispose()
    {
        op().property( NewLiferayPluginProjectOp.PROP_PLUGINS_SDK_NAME ).detach( this.listener );
        op().property( NewLiferayPluginProjectOp.PROP_PROJECT_PROVIDER ).detach( this.listener );

        super.dispose();
    }

    @Override
    protected EnablementServiceData compute()
    {
        final String provider = op().getProjectProvider().content( true ).getShortName();
        final String sdkName = op().getPluginsSDKName().content( true );

        final SDK sdk = SDKManager.getInstance().getSDK( sdkName );

        boolean enablement = true;

        if( "ant".equals( provider ) && sdk != null && CoreUtil.compareVersions( new Version( sdk.getVersion() ), ILiferayConstants.V620 ) < 0 ) //$NON-NLS-1$
        {
            enablement = false;
        }

        return new EnablementServiceData( enablement );
    }

}
