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

package com.liferay.mobile.sdk.core.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.mobile.sdk.core.MobileAPI;
import com.liferay.mobile.sdk.core.MobileSDKBuilder;
import com.liferay.mobile.sdk.core.MobileSDKCore;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
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

        MobileSDKBuilder.build(
            server, contextName, packageName, filter, newTempDir.getCanonicalPath(), new NullProgressMonitor() );

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

//        assertEquals( "Liferay core", apis[0].context );

        boolean foundCalendar = false;

        for( final MobileAPI api : apis )
        {
            if( api.context.equals( CONTEXT ) )
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
        final Map<String, String[]> buildSpec = new HashMap<String, String[]>();
        buildSpec.put( "calendar-portlet", new String[] { "calendar" } );

        final File[] customJars = MobileSDKBuilder.buildJars( SERVER, PACKAGE, buildSpec, new NullProgressMonitor() );

        checkJar( customJars[0], false );
        checkJar( customJars[1], true );
    }

    @Test
    public void mobileSDKBuilderBuildSingleAPI() throws Exception
    {
        final Map<String, String[]> buildSpec = new HashMap<String, String[]>();
        buildSpec.put( "calendar-portlet", new String[] { "calendar" } );

        final File[] customJars = MobileSDKBuilder.buildJars( SERVER, PACKAGE, buildSpec, new NullProgressMonitor() );


        checkJarPaths( customJars[0], new String[] { "foo/bar/v62/calendar" } );
        checkJarPaths( customJars[1], new String[] { "foo/bar/v62/calendar" } );
    }

    @Test
    public void mobileSDKBuilderBuildMultiAPI() throws Exception
    {
        final Map<String, String[]> buildSpec = new HashMap<String, String[]>();
        buildSpec.put( "calendar-portlet", new String[] { "calendar", "calendarbooking" } );

        final File[] customJars = MobileSDKBuilder.buildJars( SERVER, PACKAGE, buildSpec, new NullProgressMonitor() );

        checkJarPaths( customJars[0], new String[] { "foo/bar/v62/calendar", "foo/bar/v62/calendarbooking" } );
        checkJarPaths( customJars[1], new String[] { "foo/bar/v62/calendar", "foo/bar/v62/calendarbooking" } );
    }

    @Test
    public void mobileSDKBuilderMultiContextMultiAPI() throws Exception
    {
        final Map<String, String[]> buildSpec = new HashMap<String, String[]>();
        buildSpec.put( "calendar-portlet", new String[] { "calendar", "calendarbooking" } );
        buildSpec.put( "opensocial-portlet", new String[] { "gadget" }  );

        final File[] customJars = MobileSDKBuilder.buildJars( SERVER, PACKAGE, buildSpec, new NullProgressMonitor() );

        checkJarPaths( customJars[0], new String[] { "foo/bar/v62/calendar", "foo/bar/v62/calendarbooking", "foo/bar/v62/gadget" } );
        checkJarPaths( customJars[1], new String[] { "foo/bar/v62/calendar", "foo/bar/v62/calendarbooking", "foo/bar/v62/gadget" } );
    }

    private void checkJarPaths( File jar, String[] paths ) throws Exception
    {
        assertTrue( jar.exists() );

        final ZipFile jarFile = new ZipFile( jar );

        for( String path : paths )
        {
            ZipEntry entry = jarFile.getEntry( path );

            assertNotNull( entry );
        }

        jarFile.close();
    }
}
