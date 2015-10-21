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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.PluginClasspathContainerInitializer;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.SDKClasspathContainer;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author Simon Jiang
 */


public class ImportPluginsSDKProjectTests extends ProjectCoreBase
{
    @AfterClass
    public static void removePluginsSDK() throws Exception
    {
        deleteAllWorkspaceProjects();
    }

    private boolean isLiferayRuntimePluginClassPath(List<IClasspathEntry> entries, final String entryPath)
    {
        boolean retval = false;
        for( Iterator<IClasspathEntry> iterator = entries.iterator(); iterator.hasNext(); )
        {
            IClasspathEntry entry = iterator.next();

            if( entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER )
            {
                for( String path : entry.getPath().segments() )
                {
                    if ( path.equals( entryPath ))
                    {
                        retval = true;
                        break;
                    }
                }
            }
        }
        return retval;
    }

    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-7.0" );
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-7.0-ce-m5-20150515112305685.zip" );
    }

    @Override
    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-7.0/";
    }


    private IPath importProject( String pluginType, String name ) throws Exception
    {
        SDK sdk = SDKUtil.getWorkspaceSDK();
        final IPath pluginTypeFolder = sdk.getLocation().append( pluginType );

        final URL projectZipUrl =
            Platform.getBundle( "com.liferay.ide.project.core.tests" ).getEntry( "projects/" + name + ".zip" );

        final File projectZipFile = new File( FileLocator.toFileURL( projectZipUrl ).getFile() );

        ZipUtil.unzip( projectZipFile, pluginTypeFolder.toFile() );

        final IPath projectFolder = pluginTypeFolder.append( name );

        assertEquals( true, projectFolder.toFile().exists() );

        return projectFolder;
    }

    @Test
    public void testSDKSetting() throws Exception
    {
        SDK sdk = SDKUtil.getWorkspaceSDK();
        Map<String, Object> sdkProperties = sdk.getBuildProperties(true);

        assertNotNull( sdkProperties.get( "app.server.type" ) );
        assertNotNull( sdkProperties.get( "app.server.dir" ) );
        assertNotNull( sdkProperties.get( "app.server.deploy.dir" ) );
        assertNotNull( sdkProperties.get( "app.server.lib.global.dir" ) );
        assertNotNull( sdkProperties.get( "app.server.parent.dir" ) );
        assertNotNull( sdkProperties.get( "app.server.portal.dir" ) );

        assertEquals( sdkProperties.get( "app.server.type" ), "tomcat" );
        assertEquals( sdkProperties.get( "app.server.dir" ), getLiferayRuntimeDir().toPortableString() );
        assertEquals( sdkProperties.get( "app.server.deploy.dir" ), getLiferayRuntimeDir().append( "webapps" ).toPortableString() );
        assertEquals( sdkProperties.get( "app.server.lib.global.dir" ), getLiferayRuntimeDir().append( "lib/ext" ).toPortableString() );
        assertEquals( sdkProperties.get( "app.server.parent.dir" ), getLiferayRuntimeDir().removeLastSegments( 1 ).toPortableString() );
        assertEquals( sdkProperties.get( "app.server.portal.dir" ), getLiferayRuntimeDir().append( "webapps/ROOT" ).toPortableString() );

    }

    @Test
    public void testImportBasicHookProject() throws Exception
    {
        final IPath projectPath = importProject( "hooks", "Import-IDE3.0-hook" );
        IProject hookProjectForIDE3 = ProjectImportUtil.importProject( projectPath, new NullProgressMonitor(), null );

        assertNotNull( hookProjectForIDE3 );

        IJavaProject javaProject = JavaCore.create( hookProjectForIDE3 );
        IClasspathEntry[] rawClasspath = javaProject.getRawClasspath();
        List<IClasspathEntry> rawClasspaths = Arrays.asList( rawClasspath );
        final boolean hasPluginClasspathDependencyContainer =
                        isLiferayRuntimePluginClassPath(rawClasspaths, SDKClasspathContainer.ID);

        assertEquals( hasPluginClasspathDependencyContainer, true );
    }

    @Test
    public void testImportConfiguredPortletProject() throws Exception
    {
        final IPath projectPath = importProject( "portlets", "Import-Old-Configured-portlet" );
        IProject portletProjectForIDE3 = ProjectImportUtil.importProject( projectPath, new NullProgressMonitor(), null );

        assertNotNull( portletProjectForIDE3 );

        IJavaProject javaProject = JavaCore.create( portletProjectForIDE3 );
        IClasspathEntry[] rawClasspath = javaProject.getRawClasspath();
        List<IClasspathEntry> rawClasspaths = Arrays.asList( rawClasspath );

        final boolean hasOldPluginClasspathContainer =
                        isLiferayRuntimePluginClassPath(rawClasspaths, PluginClasspathContainerInitializer.ID);
        final boolean hasPluginClasspathDependencyContainer =
                        isLiferayRuntimePluginClassPath(rawClasspaths, SDKClasspathContainer.ID);
        final boolean hasOldRuntimeClasspathContainer =
                        isLiferayRuntimePluginClassPath(rawClasspaths, "com.liferay.studio.server.tomcat.runtimeClasspathProvider");

        assertEquals( hasOldPluginClasspathContainer, false );
        assertEquals( hasOldRuntimeClasspathContainer, false );
        assertEquals( hasPluginClasspathDependencyContainer, true );
    }
}
