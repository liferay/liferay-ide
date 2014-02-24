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

package com.liferay.mobile.sdk.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.mobile.sdk.core.MobileSDKCore;
import com.liferay.mobile.sdk.core.MobileSDKCore.MobileAPI;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class MobileSDKCoreTests extends BaseTests
{

    private static final String CONTEXT = "calendar-portlet";
    private static final String PACKAGE = "foo.bar";
    private static final String PASSWORD = "test";
    private static final String SERVER = "http://localhost:8080/";
    private static final String USERNAME = "test@liferay.com";

    private Collection<File> build( String server, String contextName, String packageName ) throws IOException
    {
        return build( server, contextName, null, packageName );
    }

    private Collection<File> build( String server, String contextName, String filter, String packageName )
        throws IOException
    {
        final File newTempDir = MobileSDKCore.newTempDir();

        MobileSDKCore.newSDKBuilder().build( server, contextName, packageName, filter, newTempDir.getCanonicalPath() );

        return FileUtils.listFiles( newTempDir, null, true );
    }

    private void checkJar( File jar, boolean src ) throws Exception
    {
        assertTrue( jar.exists() );

        final ZipFile zipFile = new ZipFile( jar );
        final ZipEntry manifest = zipFile.getEntry( "META-INF/MANIFEST.MF" );

        assertNotNull( manifest );

        final String manifestContents = CoreUtil.readStreamToString( zipFile.getInputStream( manifest ) );

        assertTrue( manifestContents.startsWith( "Manifest-Version: 1.0" ) );

        boolean valid = false;

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while( entries.hasMoreElements() )
        {
            final String entryName = entries.nextElement().getName();

            if( entryName.startsWith( PACKAGE.split( "\\." )[0] ) &&
                entryName.endsWith( src ? ".java" : ".class" ) )
            {
                valid = true;
                break;
            }
        }

        zipFile.close();

        assertTrue( valid );
    }

    @Test
    public void testMobileSDKDiscovery() throws Exception
    {
        final MobileAPI[] apis = MobileSDKCore.discoverAPIs( SERVER, USERNAME, PASSWORD );

        assertNotNull( apis );

        assertTrue( apis.length > 1 );

        assertEquals( "Liferay core", apis[0].name );

        boolean foundCalendar = false;

        for( final MobileAPI api : apis )
        {
            if( api.name.equals( CONTEXT ) )
            {
                foundCalendar = true;
            }
        }

        assertTrue( foundCalendar );
    }

    @Test
    public void testMobileSDKBuilderBuild() throws Exception
    {
        final Collection<File> files = build( SERVER, CONTEXT, PACKAGE );

        assertNotNull( files );

        assertTrue( files.size() == 4 );
    }

    @Test
    public void testMobileSDKBuilderBuildFilter() throws Exception
    {
        final Collection<File> files = build( SERVER, CONTEXT, "calendarbooking", PACKAGE );

        assertTrue( files.size() == 1 );
    }

    @Test
    public void testMobileSDKBuilderBuildJars() throws Exception
    {
        final File[] customJars = MobileSDKCore.newSDKBuilder().buildJars( SERVER, CONTEXT, PACKAGE );

        checkJar( customJars[0], false );
        checkJar( customJars[1], true );
    }

}
