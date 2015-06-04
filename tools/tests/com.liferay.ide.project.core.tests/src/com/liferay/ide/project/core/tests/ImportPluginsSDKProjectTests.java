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

import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.PluginClasspathContainerInitializer;
import com.liferay.ide.project.core.PluginClasspathDependencyContainerInitializer;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Simon Jiang
 */


public class ImportPluginsSDKProjectTests extends ProjectCoreBase
{
    private Properties initProps = new Properties();

    private boolean isLiferayRuntimePluginClassPath(List<IClasspathEntry> entries, final String entryPath)
    {
        boolean retval = false;
        for( Iterator<IClasspathEntry> iterator = entries.iterator(); iterator.hasNext(); )
        {
            IClasspathEntry entry = (IClasspathEntry) iterator.next();

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
        return ProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.2.0" );
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-6.2.0-ce-ga1-20131101192857659.zip" );
    }

    @Override
    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-6.2.0/";
    }

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2.0-ce-ga1/tomcat-7.0.42" );
    }

    @Override
    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.2.0-ce-ga1-20131101192857659.zip" );
    }

    private void persistAppServerProperties() throws FileNotFoundException, IOException, ConfigurationException
    {

        initProps.put( "app.server.type", "tomcat" );
        initProps.put( "app.server.tomcat.dir", getLiferayRuntimeDir().toPortableString() );
        initProps.put( "app.server.tomcat.deploy.dir", getLiferayRuntimeDir().append( "webapps" ).toPortableString() );
        initProps.put( "app.server.tomcat.lib.global.dir", getLiferayRuntimeDir().append( "lib/ext" ).toPortableString() );
        initProps.put( "app.server.parent.dir", getLiferayRuntimeDir().removeLastSegments( 1 ).toPortableString() );
        initProps.put( "app.server.tomcat.portal.dir", getLiferayRuntimeDir().append( "webapps/ROOT" ).toPortableString() );

        IPath loc = getLiferayPluginsSdkDir();

        String userName = System.getProperty( "user.name" ); //$NON-NLS-1$

        File userBuildFile = loc.append( "build." + userName + ".properties" ).toFile(); //$NON-NLS-1$ //$NON-NLS-2$

        if( userBuildFile.exists() )
        {
            PropertiesConfiguration propsConfig = new PropertiesConfiguration( userBuildFile );

            for( Object key : initProps.keySet() )
            {
                propsConfig.setProperty( (String) key, initProps.get( key ) );
            }

            propsConfig.setHeader( "" );
            propsConfig.save( userBuildFile );

        }
        else
        {
            Properties props = new Properties();

            props.putAll( initProps );

            props.store( new FileOutputStream( userBuildFile ), "" );
        }
    }

    private IPath importProject( String pluginType, String name ) throws Exception
    {
        SDK sdk = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );
        final IPath pluginTypeFolder = sdk.getLocation().append( pluginType );

        final URL projectZipUrl =
            Platform.getBundle( "com.liferay.ide.project.core.tests" ).getEntry( "projects/" + name + ".zip" );

        final File projectZipFile = new File( FileLocator.toFileURL( projectZipUrl ).getFile() );

        ZipUtil.unzip( projectZipFile, pluginTypeFolder.toFile() );

        final IPath projectFolder = pluginTypeFolder.append( name );

        assertEquals( true, projectFolder.toFile().exists() );

        return projectFolder;
    }

    @Before
    @Override
    public void setupRuntime() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        assertEquals(
            "Expected liferayBundlesPath to exist: " + getLiferayBundlesPath().toOSString(), true,
            getLiferayBundlesPath().toFile().exists() );

        extractRuntime( getLiferayRuntimeZip(), getLiferayRuntimeDir() );
    }

    @Override
    @Before
    public void setupPluginsSDK() throws Exception
    {
        super.setupPluginsSDK();
        persistAppServerProperties();
    }

    @Test
    public void testSDKSetting() throws Exception
    {
        final IPath hookPath = importProject( "portlets", "Import-IDE3.0-hook" );
        SDK sdk = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );
        Map<String, String> sdkProperties = sdk.getProperties();

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
        IProject hookProjectForIDE3 = ProjectUtil.importProject( projectPath, new NullProgressMonitor() );

        assertNotNull( hookProjectForIDE3 );
        assertEquals( LiferayNature.hasNature( hookProjectForIDE3 ), true );

        IJavaProject javaProject = JavaCore.create( hookProjectForIDE3 );
        IClasspathEntry[] rawClasspath = javaProject.getRawClasspath();
        List<IClasspathEntry> rawClasspaths = Arrays.asList( rawClasspath );
        final boolean hasPluginClasspathDependencyContainer = 
                        isLiferayRuntimePluginClassPath(rawClasspaths, PluginClasspathDependencyContainerInitializer.ID);

        assertEquals( hasPluginClasspathDependencyContainer, true );
    }

    @Test
    public void testImportConfiguredPortletProject() throws Exception
    {
        final IPath projectPath = importProject( "portlets", "Import-Old-Configured-portlet" );
        IProject portletProjectForIDE3 = ProjectUtil.importProject( projectPath, new NullProgressMonitor() );

        assertNotNull( portletProjectForIDE3 );
        assertEquals( LiferayNature.hasNature( portletProjectForIDE3 ), true );

        IJavaProject javaProject = JavaCore.create( portletProjectForIDE3 );
        IClasspathEntry[] rawClasspath = javaProject.getRawClasspath();
        List<IClasspathEntry> rawClasspaths = Arrays.asList( rawClasspath );

        final boolean hasOldPluginClasspathContainer = 
                        isLiferayRuntimePluginClassPath(rawClasspaths, PluginClasspathContainerInitializer.ID);
        final boolean hasPluginClasspathDependencyContainer = 
                        isLiferayRuntimePluginClassPath(rawClasspaths, PluginClasspathDependencyContainerInitializer.ID);
        final boolean hasOldRuntimeClasspathContainer = 
                        isLiferayRuntimePluginClassPath(rawClasspaths, "com.liferay.studio.server.tomcat.runtimeClasspathProvider");

        assertEquals( hasOldPluginClasspathContainer, false );
        assertEquals( hasOldRuntimeClasspathContainer, false );
        assertEquals( hasPluginClasspathDependencyContainer, true );
    }
}
