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

import com.liferay.ide.sdk.core.ISDKListener;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;

import org.eclipse.sapphire.DefaultValueService;


/**
 * @author Gregory Amerson
 */
public class PluginsSDKNameDefaultValueService extends DefaultValueService implements ISDKListener
{

   static final String NONE = "<None>";

    @Override
    protected void initDefaultValueService()
    {
        super.initDefaultValueService();

        SDKManager.getInstance().addSDKListener( this );
    }

    @Override
    public void dispose()
    {
        SDKManager.getInstance().removeSDKListener( this );

        super.dispose();
    }

    @Override
    protected String compute()
    {
        String value = null;

        final SDK defaultSDK = SDKManager.getInstance().getDefaultSDK();

        if( defaultSDK != null )
        {
            value = defaultSDK.getName();
        }
        else
        {
            value = NONE;
        }

        return value;
    }

    public void sdksAdded( SDK[] sdk )
    {
        refresh();
    }

    public void sdksChanged( SDK[] sdk )
    {
        refresh();
    }

    public void sdksRemoved( SDK[] sdk )
    {
        refresh();
    }

}
