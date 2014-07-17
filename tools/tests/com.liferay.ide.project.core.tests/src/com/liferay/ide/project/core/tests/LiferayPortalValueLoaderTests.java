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

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.server.util.LiferayPortalValueLoader;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.ServerCore;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
public class LiferayPortalValueLoaderTests extends ProjectCoreBase
{

    private LiferayPortalValueLoader loader( final IPath runtimeLocation )
    {
        return new LiferayPortalValueLoader( runtimeLocation, runtimeLocation.append( "webapps/ROOT" ) );
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

        final IPath runtimeLocation = ServerCore.getRuntimes()[0].getLocation();

        final String[] props = loader( runtimeLocation ).loadHookPropertiesFromClass();

        assertNotNull( props );

        assertEquals( 141, props.length );
    }

    @Test
    public void loadServerInfoFromClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        setupPluginsSDKAndRuntime();

        final IPath runtimeLocation = ServerCore.getRuntimes()[0].getLocation();

        final String info = loader( runtimeLocation ).loadServerInfoFromClass();

        assertNotNull( info );

        assertEquals( "Liferay Portal Community Edition / 6.2.0", info );
    }

    @Test
    public void loadVersionFromClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        setupPluginsSDKAndRuntime();

        final IPath runtimeLocation = ServerCore.getRuntimes()[0].getLocation();

        final Version version = loader( runtimeLocation ).loadVersionFromClass();

        assertNotNull( version );

        assertEquals( ILiferayConstants.V620, version );
    }

}
