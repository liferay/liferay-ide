/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;

import com.liferay.ide.project.core.model.LiferayPluginSDKOp;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.PossibleValuesService;
import org.junit.Test;


/**
 * @author Simon Jiang
 */
public class LiferayPluginSDKOpTests extends ProjectCoreBase
{



    @Test
    public void pluginsSDKNameDefaultValueService() throws Exception
    {
        LiferayPluginSDKOp op1 = LiferayPluginSDKOp.TYPE.instantiate();

        final SDK originalSDK = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );
        originalSDK.setDefault( true );

        final SDK newSDK = createNewSDK();
        newSDK.setDefault( false );

        SDKManager.getInstance().setSDKs( new SDK[] { originalSDK, newSDK } );

        DefaultValueService dvs = op1.getPluginsSDKName().service( DefaultValueService.class );

        assertEquals( originalSDK.getName(), dvs.value() );
        assertEquals( originalSDK.getName(), op1.getPluginsSDKName().content() );

        op1.dispose();


        LiferayPluginSDKOp op2 = LiferayPluginSDKOp.TYPE.instantiate();
        originalSDK.setDefault( false );
        newSDK.setDefault( true );

        dvs = op2.getPluginsSDKName().service( DefaultValueService.class );

        assertEquals( newSDK.getName(), dvs.value() );
        assertEquals( newSDK.getName(), op2.getPluginsSDKName().content() );

        SDKManager.getInstance().clearSDKs();

        assertEquals( "<None>", dvs.value() );
        assertEquals( "<None>", op2.getPluginsSDKName().content() );
    }

    @Test
    public void pluginsSDKNamePossibleValueService() throws Exception
    {
        LiferayPluginSDKOp op = LiferayPluginSDKOp.TYPE.instantiate();

        final SDK originSDK = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );
        final SDK newSDK = createNewSDK();

        Set<String> exceptedSDKNames = new HashSet<String>();
        exceptedSDKNames.add( originSDK.getName() );
        exceptedSDKNames.add( newSDK.getName() );

        final Set<String> acturalSDKNames = op.getPluginsSDKName().service( PossibleValuesService.class ).values();

        assertEquals( true, exceptedSDKNames.containsAll( acturalSDKNames ) );
        assertEquals( true, acturalSDKNames.containsAll( exceptedSDKNames ) );
    }
}
