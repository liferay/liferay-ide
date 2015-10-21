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

package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.LiferayPortalValueLoader;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
public class LiferayPortalValueLoaderTests extends ProjectCoreBase
{

    @AfterClass
    public static void removePluginsSDK() throws Exception
    {
        deleteAllWorkspaceProjects();
    }

    private LiferayPortalValueLoader loader( final IRuntime runtime )
    {
        ILiferayRuntime liferayRutime = ServerUtil.getLiferayRuntime( runtime );
        return new LiferayPortalValueLoader( liferayRutime.getUserLibs() );
    }

    @Before
    public void removeRuntimes() throws Exception
    {
        super.removeAllRuntimes();
    }

    @Test
    public void loadHookPropertiesFromClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        setupPluginsSDKAndRuntime();

        final IRuntime runtime = ServerCore.getRuntimes()[0];

        final String[] props = loader( runtime ).loadHookPropertiesFromClass();

        assertNotNull( props );

        assertEquals( 141, props.length );
    }

    @Test
    public void loadServerInfoFromClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        setupPluginsSDKAndRuntime();

        final IRuntime runtime = ServerCore.getRuntimes()[0];

        final String info = loader( runtime ).loadServerInfoFromClass();

        assertNotNull( info );

        assertEquals( "Liferay Portal Community Edition / 6.2.3", info );
    }

    @Test
    public void loadVersionFromClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        setupPluginsSDKAndRuntime();

        final IRuntime runtime = ServerCore.getRuntimes()[0];

        final Version version = loader( runtime ).loadVersionFromClass();

        assertNotNull( version );

        assertEquals( "6.2.3", version.toString() );
    }

}
