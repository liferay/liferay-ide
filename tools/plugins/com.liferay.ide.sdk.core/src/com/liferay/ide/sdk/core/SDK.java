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

package com.liferay.ide.sdk.core;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.core.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Dirname;
import org.apache.tools.ant.taskdefs.Property;
import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.core.AntCorePreferences;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.internal.core.XMLMemento;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class SDK
{
    public final static List<String> APP_SERVER_PROPERTIES_KEYS =
        Collections.unmodifiableList( Arrays.asList( new String[] {
            "app.server.type",
            "app.server.dir",
            "app.server.deploy.dir",
            "app.server.lib.global.dir",
            "app.server.parent.dir",
            "app.server.portal.dir"
     }));

    public final static Map<String, Map<String, Object>> buildPropertiesCache = new HashMap<>();

    public final static List<String> SUPPORTED_SERVER_TYPES =
        Collections.unmodifiableList( Arrays.asList( new String[] {
            "tomcat",
            "jboss",
            "glassfish",
            "jetty",
            "wildfly"
    }));

    protected boolean contributed;

    protected boolean defaultSDK;

    protected IPath location;

    protected String name;

    protected String version;

    public SDK()
    {
    }

    public SDK( IPath location )
    {
        this.location = location;
    }

    public void addOrUpdateServerProperties( IPath newServerPath ) throws IOException
    {
        Project project = getSDKAntProject();

        String[] buildFileNames = { "build." + project.getProperty( "user.name" ) + ".properties",
            "build." + project.getProperty( "env.COMPUTERNAME" ) + ".properties",
            project.getProperty( "env.HOST" ) + ".properties", project.getProperty( "env.HOSTNAME" ) + ".properties" };

        File buildFile = null;

        for( String name : buildFileNames )
        {
            if( getLocation().append( name ).toFile().exists() )
            {
                buildFile = getLocation().append( name ).toFile();
                break;
            }
        }

        if( buildFile == null )
        {
            buildFile = new File(
                getLocation().append( "build." + project.getProperty( "user.name" ) + ".properties" ).toString() );
            buildFile.createNewFile();
        }

        Properties p = new Properties();

        try( InputStream in = new FileInputStream( buildFile ); OutputStream out = new FileOutputStream( buildFile ) )
        {
            p.load( in );

            if( p.containsKey( "app.server.parent.dir" ) )
            {
                p.put( "app.server.parent.dir", newServerPath.toPortableString() );
            }
            else
            {
                p.put( "app.server.parent.dir", newServerPath.toPortableString() );
            }

            p.store( out, "" );
        }
    }

    public IStatus buildLanguage(
        IProject project, IFile langFile, Map<String, String> overrideProperties,
        IProgressMonitor monitor )
    {
        SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
           Map<String, String> properties = new HashMap<String, String>();

            if( overrideProperties != null )
            {
                properties.putAll( overrideProperties );
            }

            String langDirLocation = langFile.getParent().getRawLocation().toOSString();

            String langFileName = langFile.getFullPath().removeFileExtension().lastSegment();

            properties.put( ISDKConstants.PROPERTY_LANG_DIR, langDirLocation );
            properties.put( ISDKConstants.PROPERTY_LANG_FILE, langFileName );

            final IPath buildFile = project.getFile( ISDKConstants.PROJECT_BUILD_XML ).getRawLocation();

            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.runTarget( buildFile, ISDKConstants.TARGET_BUILD_LANG_CMD, properties, true, workingDir );
        }
        catch( Exception e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;
    }

    public IStatus buildService(
        IProject project, IFile serviceXmlFile, Map<String, String> overrideProperties )
    {
        SDKHelper antHelper = new SDKHelper( this );

        try
        {
            Map<String, String> properties = new HashMap<String, String>();

            if( overrideProperties != null )
            {
                properties.putAll( overrideProperties );
            }

            String serviceFile = serviceXmlFile.getRawLocation().toOSString();

            properties.put( ISDKConstants.PROPERTY_SERVICE_FILE, serviceXmlFile.getRawLocation().toOSString() );
            properties.put( ISDKConstants.PROPERTY_SERVICE_INPUT_FILE, serviceFile );

            final IPath buildFile = project.getFile( ISDKConstants.PROJECT_BUILD_XML ).getRawLocation();
            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.runTarget( buildFile, ISDKConstants.TARGET_BUILD_SERVICE, properties, true, workingDir );
        }
        catch( Exception e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;
    }

    public IStatus buildWSDD(
        IProject project, IFile serviceXmlFile, Map<String, String> overrideProperties )
    {
        SDKHelper antHelper = new SDKHelper( this );

        try
        {
            Map<String, String> properties = new HashMap<String, String>();

            if( overrideProperties != null )
            {
                properties.putAll( overrideProperties );
            }

            String serviceFile = serviceXmlFile.getRawLocation().toOSString();

            properties.put( ISDKConstants.PROPERTY_SERVICE_FILE, serviceXmlFile.getRawLocation().toOSString() );
            properties.put( ISDKConstants.PROPERTY_SERVICE_INPUT_FILE, serviceFile );

            final IPath buildFile = project.getFile( ISDKConstants.PROJECT_BUILD_XML ).getRawLocation();
            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.runTarget( buildFile, ISDKConstants.TARGET_BUILD_WSDD, properties, true, workingDir );
        }
        catch( Exception e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;
    }

    public IStatus cleanAppServer(
        IProject project, String bundleZipLocation, String appServerDir,
        IProgressMonitor monitor )
    {
        try
        {
            Map<String, String> properties = new HashMap<String, String>();

            IPath workPath = new Path( appServerDir ).removeLastSegments( 2 );

            properties.put( ISDKConstants.PROPERTY_APP_ZIP_NAME, bundleZipLocation );
            properties.put( ISDKConstants.PROPERTY_EXT_WORK_DIR, workPath.toOSString() );

            IStatus status = runTarget( project, properties, "clean-app-server", true, monitor ); //$NON-NLS-1$

            if( !status.isOK() )
            {
                return status;
            }
        }
        catch( Exception ex )
        {
            return SDKCorePlugin.createErrorStatus( ex );
        }

        return Status.OK_STATUS;
    }

    public IStatus compileThemePlugin(
        IProject project, Map<String, String> overrideProperties )
    {
        SDKHelper antHelper = new SDKHelper( this );

        try
        {
            Map<String, String> properties = new HashMap<String, String>();

            if( overrideProperties != null )
            {
                properties.putAll( overrideProperties );
            }

            final IPath buildFile = project.getFile( ISDKConstants.PROJECT_BUILD_XML ).getRawLocation();
            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.runTarget( buildFile, ISDKConstants.TARGET_COMPILE, properties, true, workingDir );
        }
        catch( Exception e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;
    }

    public SDK copy()
    {
        SDK copy = new SDK( this.getLocation() );

        copy.setContributed( isContributed() );
        copy.setDefault( isDefault() );
        copy.setName( getName() );
        copy.setVersion( getVersion() );

        return copy;
    }

    public IPath createNewExtProject( String extName, String extDisplayName,
        boolean separateJRE, String workingDir, String baseDir, IProgressMonitor monitor )
    {
        try
        {
            SDKHelper antHelper = new SDKHelper( this, monitor );

            Map<String, String> properties = new HashMap<String, String>();

            properties.put( ISDKConstants.PROPERTY_EXT_NAME, extName );
            properties.put( ISDKConstants.PROPERTY_EXT_DISPLAY_NAME, extDisplayName );

            // create a space for new portlet template to get built
            IPath tempPath =
                SDKCorePlugin.getDefault().getStateLocation().append( ISDKConstants.TARGET_CREATE ).append(
                    String.valueOf( System.currentTimeMillis() ) );
            // if (!newPortletPath.toFile().mkdirs()) {
            // throw new
            // CoreException(SDKPlugin.createErrorStatus("Unable to create directory in state location"));
            // }

            properties.put( ISDKConstants.PROPERTY_EXT_PARENT_DIR, tempPath.toOSString() );

            if( baseDir != null )
            {
                properties.put( "plugin.type.dir", baseDir ); //$NON-NLS-1$
            }

            antHelper.runTarget( getLocation().append( ISDKConstants.EXT_PLUGIN_ANT_BUILD ),
                ISDKConstants.TARGET_CREATE, properties, separateJRE, workingDir );

            return tempPath;
        }
        catch( Exception e )
        {
            SDKCorePlugin.logError( e );
        }

        return null;
    }

    public IPath createNewHookProject(
        String hookName, String hookDisplayName, boolean separateJRE,
        String workingDir, String baseDir, IProgressMonitor monitor )
    {
        SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
            Map<String, String> properties = new HashMap<String, String>();
            properties.put( ISDKConstants.PROPERTY_HOOK_NAME, hookName );
            properties.put( ISDKConstants.PROPERTY_HOOK_DISPLAY_NAME, hookDisplayName );

            // create a space for new portlet template to get built
            IPath newHookPath =
                SDKCorePlugin.getDefault().getStateLocation().append( ISDKConstants.TARGET_CREATE ).append(
                    String.valueOf( System.currentTimeMillis() ) );

            properties.put( ISDKConstants.PROPERTY_HOOK_PARENT_DIR, newHookPath.toOSString() );

            final IPath buildLocation = getLocation().append( ISDKConstants.HOOK_PLUGIN_ANT_BUILD );

            if( baseDir != null )
            {
                properties.put( "plugin.type.dir", baseDir ); //$NON-NLS-1$
            }

            antHelper.runTarget( buildLocation, ISDKConstants.TARGET_CREATE, properties, separateJRE, workingDir );

            return newHookPath;
        }
        catch( Exception e )
        {
            SDKCorePlugin.logError( e );
        }

        return null;
    }

    public IPath createNewLayoutTplProject( String layoutTplName, String layoutTplDisplayName,
        boolean separateJRE, String workingDir, String baseDir, IProgressMonitor monitor )
    {
        SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
            Map<String, String> properties = new HashMap<String, String>();

            properties.put( ISDKConstants.PROPERTY_LAYOUTTPL_NAME, layoutTplName );
            properties.put( ISDKConstants.PROPERTY_LAYOUTTPL_DISPLAY_NAME, layoutTplDisplayName );

            // create a space for new layouttpm template to get built
            IPath newLayoutTplPath =
                SDKCorePlugin.getDefault().getStateLocation().append( ISDKConstants.TARGET_CREATE ).append(
                    String.valueOf( System.currentTimeMillis() ) );

            properties.put( ISDKConstants.PROPERTY_LAYOUTTPL_PARENT_DIR, newLayoutTplPath.toOSString() );

            if( baseDir != null )
            {
                properties.put( "plugin.type.dir", baseDir ); //$NON-NLS-1$
            }

            antHelper.runTarget(
                getLocation().append( ISDKConstants.LAYOUTTPL_PLUGIN_ANT_BUILD ), ISDKConstants.TARGET_CREATE,
                properties, separateJRE, workingDir );

            return newLayoutTplPath;
        }
        catch( Exception e )
        {
            SDKCorePlugin.logError( e );
        }

        return null;
    }

    public IPath createNewPortletProject( String portletName, String portletDisplayName, String portletFramework,
        boolean separateJRE, String workingDir, String baseDir, IProgressMonitor monitor )
    {
        SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
             Map<String, String> properties = new HashMap<String, String>();

            properties.put( ISDKConstants.PROPERTY_PORTLET_NAME, portletName );
            properties.put( ISDKConstants.PROPERTY_PORTLET_DISPLAY_NAME, portletDisplayName );
            properties.put( ISDKConstants.PROPERTY_PORTLET_FRAMEWORK, portletFramework );

            // create a space for new portlet template to get built
            IPath newPortletPath =
                SDKCorePlugin.getDefault().getStateLocation().append( ISDKConstants.TARGET_CREATE ).append(
                    String.valueOf( System.currentTimeMillis() ) );

            properties.put( ISDKConstants.PROPERTY_PORTLET_PARENT_DIR, newPortletPath.toOSString() );

            if( baseDir != null )
            {
                properties.put( "plugin.type.dir", baseDir ); //$NON-NLS-1$
            }

            antHelper.runTarget( getLocation().append( ISDKConstants.PORTLET_PLUGIN_ANT_BUILD ),
                ISDKConstants.TARGET_CREATE, properties, separateJRE, workingDir );

            return newPortletPath;
        }
        catch( Exception e )
        {
            SDKCorePlugin.logError( e );
        }

        return null;
    }

    public IPath createNewProject(
        String projectName, ArrayList<String> arguments, String type, String workingDir, IProgressMonitor monitor ) throws CoreException
    {
        CreateHelper createHelper = new CreateHelper( this, monitor );

        final IPath pluginFolder = getLocation().append( getPluginFolder( type ) );

        final IPath newPath = pluginFolder.append( projectName + getPluginSuffix( type ) );

        String createScript = ISDKConstants.CREATE_BAT;

        if( !CoreUtil.isWindows() )
        {
            createScript = ISDKConstants.CREATE_SH;
        }

        final IPath createFilePath = pluginFolder.append( createScript );

        final File createFile = createFilePath.toFile();

        String originalCreateConetent = "";

        if( !CoreUtil.isWindows() && createFile.exists() )
        {
            originalCreateConetent = FileUtil.readContents( createFile, true );

            if( originalCreateConetent.contains( "DisplayName=\\\"$2\\\"" ) )
            {
                String createContent = originalCreateConetent.replace( "DisplayName=\\\"$2\\\"", "DisplayName=\"$2\"" );

                try
                {
                    FileUtil.writeFile(
                        createFile, new ByteArrayInputStream( createContent.toString().getBytes( "UTF-8" ) ), null );
                }
                catch( Exception e )
                {
                    SDKCorePlugin.logError( e );
                }
            }
        }

        createHelper.runTarget( createFilePath, arguments, workingDir );

        if( !newPath.toFile().exists() )
        {
            throw new CoreException( SDKCorePlugin.createErrorStatus( "Create script did not complete successfully." ) );
        }

        if( !CoreUtil.isNullOrEmpty( originalCreateConetent ) )
        {
            try
            {
                FileUtil.writeFile(
                    createFile, new ByteArrayInputStream( originalCreateConetent.toString().getBytes( "UTF-8" ) ), null );
            }
            catch( Exception e )
            {
                SDKCorePlugin.logError( e );
            }
        }

        return newPath;
    }

    public IPath createNewThemeProject(
        String themeName, String themeDisplayName, boolean separateJRE, String workingDir, String baseDir,
        IProgressMonitor monitor )
    {
        SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
            Map<String, String> properties = new HashMap<String, String>();
            properties.put( ISDKConstants.PROPERTY_THEME_NAME, themeName );
            properties.put( ISDKConstants.PROPERTY_THEME_DISPLAY_NAME, themeDisplayName );

            // create a space for new portlet template to get built
            IPath tempPath =
                SDKCorePlugin.getDefault().getStateLocation().append( ISDKConstants.TARGET_CREATE ).append(
                    String.valueOf( System.currentTimeMillis() ) );

            properties.put( ISDKConstants.PROPERTY_THEME_PARENT_DIR, tempPath.toOSString() );

            if( baseDir != null )
            {
                properties.put( "plugin.type.dir", baseDir ); //$NON-NLS-1$
            }

            antHelper.runTarget( getLocation().append( ISDKConstants.THEME_PLUGIN_ANT_BUILD ),
                ISDKConstants.TARGET_CREATE, properties, separateJRE, workingDir );

            return tempPath;
        }
        catch( CoreException e )
        {
            SDKCorePlugin.logError( e );
        }

        return null;
    }

    public IPath createNewWebProject(
        String webName, String webDisplayName, boolean separateJRE,
        String workingDir, String baseDir, IProgressMonitor monitor )
    {
        SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
            Map<String, String> properties = new HashMap<String, String>();
            properties.put( ISDKConstants.PROPERTY_WEB_NAME, webName );
            properties.put( ISDKConstants.PROPERTY_WEB_DISPLAY_NAME, webDisplayName );

            // create a space for new web template to get built
            IPath newWebPath =
                SDKCorePlugin.getDefault().getStateLocation().append( ISDKConstants.TARGET_CREATE ).append(
                    String.valueOf( System.currentTimeMillis() ) );

            properties.put( ISDKConstants.PROPERTY_WEB_PARENT_DIR, newWebPath.toOSString() );

            final IPath buildLocation = getLocation().append( ISDKConstants.WEB_PLUGIN_ANT_BUILD );

            if( baseDir != null )
            {
                properties.put( "plugin.type.dir", baseDir ); //$NON-NLS-1$
            }

            antHelper.runTarget( buildLocation, ISDKConstants.TARGET_CREATE, properties, separateJRE, workingDir );

            return newWebPath;
        }
        catch( Exception e )
        {
            SDKCorePlugin.logError( e );
        }

        return null;
    }

//    This code is not used since IDE-810 and it may cause the new identical sdk checking state covers the old one's.
//    @Override
//    public boolean equals( Object obj )
//    {
//        return obj instanceof SDK && getName() != null && getName().equals( ( (SDK) obj ).getName() ) &&
//            getLocation() != null && getLocation().equals( ( (SDK) obj ).getLocation() );
//    }

    public String createXMLNameValuePair( String name, String value )
    {
        return name + "=\"" + value + "\" ";
    }

    public IStatus directDeploy(
        IProject project, Map<String, String> overrideProperties, boolean separateJRE,
        IProgressMonitor monitor )
    {
        try
        {
            SDKHelper antHelper = new SDKHelper( this, monitor );

            Map<String, String> properties = new HashMap<String, String>();

            if( overrideProperties != null )
            {
                properties.putAll( overrideProperties );
            }

            final IPath buildFile = project.getFile( ISDKConstants.PROJECT_BUILD_XML ).getRawLocation();
            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.runTarget( buildFile, ISDKConstants.TARGET_DIRECT_DEPLOY, properties, separateJRE, workingDir );
        }
        catch( Exception e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;

    }

    private String[] getAntHomeVMArg()
    {
        AntCorePreferences prefs = AntCorePlugin.getPlugin().getPreferences();

        String antHome = prefs.getAntHome();

        if( !CoreUtil.isNullOrEmpty( antHome ) )
        {
            return new String[] { "-Dant.home=\"" + antHome + "\"" };
        }

        return null;
    }

    public IPath[] getAntLibraries()
    {
        List<IPath> antLibs = new ArrayList<IPath>();

        for( String antLib : ISDKConstants.ANT_LIBRARIES )
        {
            final IPath antLibPath = getLocation().append( antLib );

            if ( antLibPath.toFile().exists() )
            {
                antLibs.add( antLibPath );
            }
        }

        return antLibs.toArray( new IPath[0] );
    }

    public Map<String, Object> getBuildProperties() throws CoreException
    {
        return getBuildProperties( false );
    }

    private Project getSDKAntProject()
    {
        final Project project = new Project();

        project.setBaseDir( new File( getLocation().toPortableString() ) );
        project.setSystemProperties();

        final Dirname dirname = new Dirname();
        dirname.setProject( project );
        dirname.setFile( new File( getLocation().append( "build-common.xml" ).toPortableString() ) );
        dirname.setProperty( "sdk.dir" );
        dirname.execute();

        final Property envTask = new Property();
        envTask.setProject( project );
        envTask.setEnvironment( "env" );
        envTask.execute();

        return project;
    }

    public Map<String, Object> getBuildProperties( final boolean reload ) throws CoreException
    {
        Project project = getSDKAntProject();

        Map<String, Object> sdkProperties = buildPropertiesCache.get( getLocation().toPortableString() );

        try
        {
            if ( sdkProperties == null || reload == true )
            {
                loadPropertiesFile( project, "build." + project.getProperty( "user.name" ) + ".properties" );
                loadPropertiesFile( project, "build." + project.getProperty( "env.COMPUTERNAME" ) + ".properties" );
                loadPropertiesFile( project, "build." + project.getProperty( "env.HOST" ) + ".properties" );
                loadPropertiesFile( project, "build." + project.getProperty( "env.HOSTNAME" ) + ".properties" );
                loadPropertiesFile( project, "build.properties" );

                if ( project.getProperty( "app.server.type" ) == null )
                {
                    throw new CoreException( SDKCorePlugin.createErrorStatus(
                        "Missing ${app.server.type} setting in build.properties file." ) );
                }

                final Map<String, String> propertyCopyList = new HashMap<String, String>();
                propertyCopyList.put(
                    "app.server." + project.getProperty( "app.server.type" ) + ".dir", "app.server.dir" );
                propertyCopyList.put(
                    "app.server." + project.getProperty( "app.server.type" ) + ".deploy.dir",
                    "app.server.deploy.dir" );
                propertyCopyList.put(
                    "app.server." + project.getProperty( "app.server.type" ) + ".lib.global.dir",
                    "app.server.lib.global.dir" );
                propertyCopyList.put(
                    "app.server." + project.getProperty( "app.server.type" ) + ".portal.dir",
                    "app.server.portal.dir" );

                for( String key : propertyCopyList.keySet() )
                {
                    AntPropertyCopy propertyCopyTask = new AntPropertyCopy();
                    propertyCopyTask.setOverride( true );
                    propertyCopyTask.setProject( project );
                    String from = key;
                    String to = propertyCopyList.get( from );
                    propertyCopyTask.setFrom( from );
                    propertyCopyTask.setName( to );
                    propertyCopyTask.execute();
                }

                sdkProperties = project.getProperties();

                for( String propertyKey : APP_SERVER_PROPERTIES_KEYS )
                {
                    if( !sdkProperties.keySet().contains( propertyKey ) )
                    {
                        throw new CoreException( SDKCorePlugin.createErrorStatus( "Missing ${" + propertyKey +
                            "} setting in build.properties file." ) );
                    }
                }

                buildPropertiesCache.put( getLocation().toPortableString(), sdkProperties );
            }
        }
        catch( Exception e )
        {
            throw new CoreException( SDKCorePlugin.createErrorStatus(e.getMessage()));
        }

        return sdkProperties;
    }

    private String getDefaultWorkingDir( final IPath buildFile )
    {
        return buildFile.removeLastSegments( 1 ).toOSString();
    }

    public IPath[] getDependencyJarPaths()
    {
        final List<IPath> retval = new ArrayList<IPath>();

        try
        {
            final IPath sdkLibPath =  getLocation().append( "dependencies" );

            final int compareVersions = CoreUtil.compareVersions( new Version( getVersion() ), ILiferayConstants.V700 );

            if ( sdkLibPath.toFile().exists() && compareVersions >= 0 )
            {
                final List<File> libFiles = FileListing.getFileListing( new File( sdkLibPath.toOSString() ) );

                for( File lib : libFiles )
                {
                    if( lib.exists() && lib.getName().endsWith( ".jar" ) )
                    {
                        retval.add( new Path( lib.getPath() ) );
                    }
                }
            }
        }
        catch( FileNotFoundException e )
        {
        }

        return retval.toArray( new IPath[0] );
    }

    public IPath getLocation()
    {
        return location;
    }

    public String getName()
    {
        return name;
    }

    public String getPluginFolder( String type )
    {
        if( "ext".equals( type ) )
        {
            return ISDKConstants.EXT_PLUGIN_PROJECT_FOLDER;
        }
        else if( "portlet".equals( type ) )
        {
            return ISDKConstants.PORTLET_PLUGIN_PROJECT_FOLDER;
        }
        else if( "hook".equals( type ) )
        {
            return ISDKConstants.HOOK_PLUGIN_PROJECT_FOLDER;
        }
        else if( "layouttpl".equals( type ) )
        {
            return ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_FOLDER;
        }
        else if( "theme".equals( type ) )
        {
            return ISDKConstants.THEME_PLUGIN_PROJECT_FOLDER;
        }
        else if( "web".equals( type ) )
        {
            return ISDKConstants.WEB_PLUGIN_PROJECT_FOLDER;
        }
        else
        {
            return "";
        }
    }

    private String getPluginSuffix( String type )
    {
        if( "ext".equals( type ) )
        {
            return ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX;
        }
        else if( "portlet".equals( type ) )
        {
            return ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;
        }
        else if( "hook".equals( type ) )
        {
            return ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX;
        }
        else if( "layouttpl".equals( type ) )
        {
            return ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;
        }
        else if( "theme".equals( type ) )
        {
            return ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX;
        }
        else if( "web".equals( type ) )
        {
            return ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX;
        }
        else
        {
            return "";
        }
    }

    public IPath getPortletTemplatePath()
    {
        return getLocation().append( ISDKConstants.PORTLET_PLUGIN_ZIP_PATH );
    }

    protected IEclipsePreferences getPrefStore()
    {
        return InstanceScope.INSTANCE.getNode( SDKCorePlugin.PREFERENCE_ID );
    }

    private Properties getProperties( File file )
    {
        Properties properties = new Properties();

        try
        {
            InputStream propertiesInput = new FileInputStream( file );
            properties.load( propertiesInput );
            propertiesInput.close();
        }
        catch( Exception e )
        {
            SDKCorePlugin.logError( e );
        }

        return properties;
    }

    public String getVersion()
    {
        if( version == null )
        {
            IPath sdkLocation = getLocation().makeAbsolute();

            if( !sdkLocation.isEmpty() )
            {
                try
                {
                    version = SDKUtil.readSDKVersion( sdkLocation.toOSString() );

                    if( version.equals( ILiferayConstants.V611.toString() ) )
                    {
                        Properties buildProperties = getProperties( sdkLocation.append( "build.properties" ).toFile() ); //$NON-NLS-1$

                        if( hasAppServerSpecificProps( buildProperties ) )
                        {
                            version = ILiferayConstants.V612.toString();
                        }
                    }

                    if( version.equals( ILiferayConstants.V6120.toString() ) )
                    {
                        Properties buildProperties = getProperties( sdkLocation.append( "build.properties" ).toFile() ); //$NON-NLS-1$

                        if( hasAppServerSpecificProps( buildProperties ) )
                        {
                            version = ILiferayConstants.V6130.toString();
                        }
                    }
                }
                catch( Exception e )
                {
                    SDKCorePlugin.logError( "Could not detect the sdk version.", e); //$NON-NLS-1$
                }
            }
        }

        return version;
    }

    private boolean hasAppServerSpecificProps( Properties props )
    {
        Enumeration<?> names = props.propertyNames();

        while( names.hasMoreElements() )
        {
            String name = names.nextElement().toString();

            if( name.matches( "app.server.tomcat.*" ) ) //$NON-NLS-1$
            {
                return true;
            }
        }

        return false;
    }

    public boolean hasProjectFile()
    {
        return this.location != null && this.location.append( ".project" ).toFile().exists(); //$NON-NLS-1$
    }

    public boolean isContributed()
    {
        return contributed;
    }

    public boolean isDefault()
    {
        return defaultSDK;
    }


    public boolean isValid()
    {
        IPath sdkLocation = getLocation();

        if( sdkLocation == null )
        {
            return false;
        }

        if( !SDKUtil.isSDKSupported( sdkLocation.toOSString() ) )
        {
            return false;
        }

        if( !SDKUtil.isValidSDKLocation( sdkLocation.toOSString() ) )
        {
            return false;
        }

        return true;
    }

    public void loadFromMemento( XMLMemento sdkElement )
    {
        setName( sdkElement.getString( "name" ) ); //$NON-NLS-1$
        setLocation( Path.fromPortableString( sdkElement.getString( "location" ) ) ); //$NON-NLS-1$
        // setRuntime(sdkElement.getString("runtime"));
    }

    private void loadPropertiesFile( Project project, final String fileName )
    {
        if( project != null && fileName != null )
        {
            File propertiesFile = new File( getLocation().append( fileName ).toPortableString() );

            if( propertiesFile.exists() )
            {
                final Property loadPropetiesTask = new Property();
                loadPropetiesTask.setProject( project );
                loadPropetiesTask.setFile( propertiesFile );
                loadPropetiesTask.execute();
            }
        }
    }

    public IStatus runCommand(  IProject project,
                                IFile buildXmlFile,
                                String command,
                                Map<String, String> overrideProperties,
                                IProgressMonitor monitor )
    {
        final SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
            Map<String, String> properties = new HashMap<String, String>();

            if( overrideProperties != null )
            {
                properties.putAll( overrideProperties );
            }

            final IPath buildFile = buildXmlFile.getRawLocation();
            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.setVMArgs( getAntHomeVMArg() );

            antHelper.runTarget( buildFile, command, properties, true, workingDir );
        }
        catch( Exception e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;
    }

    protected IStatus runTarget(
        IProject project, Map<String, String> properties, String target, boolean separateJRE, IProgressMonitor monitor )
    {
        SDKHelper antHelper = new SDKHelper( this, monitor );

        try
        {
            final IPath buildFile = project.getFile( ISDKConstants.PROJECT_BUILD_XML ).getRawLocation();
            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.runTarget( buildFile, target, properties, separateJRE, workingDir );
        }
        catch( CoreException e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;
    }

    public void saveToMemento( XMLMemento child )
    {
        child.putString( "name", getName() ); //$NON-NLS-1$
        child.putString( "location", getLocation().toPortableString() ); //$NON-NLS-1$
        child.putString( "version", getVersion() ); //$NON-NLS-1$
        // child.putString("runtime", getRuntime() != null ? getRuntime() : "");
    }

    public void setContributed( boolean contributed )
    {
        this.contributed = contributed;
    }

    public void setDefault( boolean defaultSDK )
    {
        this.defaultSDK = defaultSDK;
    }

    public void setLocation( IPath location )
    {
        this.location = location;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    @Override
    public String toString()
    {
        return this.getName() + ( isDefault() ? " [default]" : "");  //$NON-NLS-1$//$NON-NLS-2$
    }

    public String toXmlString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append( "<sdk " ); //$NON-NLS-1$
        builder.append( createXMLNameValuePair( "name", getName() ) ); //$NON-NLS-1$
        builder.append( createXMLNameValuePair( "location", getLocation().toPortableString() ) ); //$NON-NLS-1$
        // builder.append(createXMLNameValuePair("runtime", getRuntime() != null
        // ? getRuntime() : ""));
        builder.append( createXMLNameValuePair( "version", getVersion() ) ); //$NON-NLS-1$
        builder.append( "/>" ); //$NON-NLS-1$

        return builder.toString();
    }

    public IStatus validate()
    {
        return validate( false );
    }

    public IStatus validate( final boolean reload )
    {
        MultiStatus status = new MultiStatus( SDKCorePlugin.PLUGIN_ID, IStatus.OK, "", null );

        boolean validLocation = SDKUtil.isValidSDKLocation( getLocation().toOSString() );

        boolean buildXmlExists = getLocation().append( "build.xml" ).toFile().exists(); //$NON-NLS-1$

        final Project project = getSDKAntProject();

        if( !validLocation )
        {
            status.add( SDKCorePlugin.createErrorStatus( Msgs.SDKLocationInvalid ) );
            return status;
        }

        if( !buildXmlExists )
        {
            status.add( SDKCorePlugin.createErrorStatus( Msgs.buildXmlFileNotExist ) );
            return status;
        }

        Map<String,Object> sdkProperties = null;

        try
        {
            sdkProperties = getBuildProperties( reload );

            if ( sdkProperties == null )
            {
                status.add( SDKCorePlugin.createErrorStatus( "Could not find any sdk settings." ) );

                return status;
            }
        }
        catch( Exception e)
        {
            status.add( SDKCorePlugin.createErrorStatus( e.getMessage() ) );

            return status;
        }

        for( String propertyKey : APP_SERVER_PROPERTIES_KEYS )
        {
            final String propertyValue = (String) sdkProperties.get( propertyKey );

            if ( propertyValue == null )
            {
                status.add( SDKCorePlugin.createErrorStatus( propertyKey + " is null." ) );
            }
            else
            {
                switch (propertyKey)
                {
                    case "app.server.type":
                    {
                        if( !SUPPORTED_SERVER_TYPES.contains( propertyValue ) )
                        {
                            status.add( SDKCorePlugin.createErrorStatus( "The " + propertyKey + "(" +
                                            propertyValue + ") server is not supported by Liferay IDE." ) );
                        }

                        break;
                    }

                    case "app.server.dir":
                    case "app.server.deploy.dir":
                    case "app.server.lib.global.dir":
                    case "app.server.parent.dir":
                    case "app.server.portal.dir":
                    {
                        IPath propertyPath = new Path( propertyValue );

                        if( !propertyPath.toFile().exists() )
                        {
                            final String customerPropertyFile =
                                "build." + project.getProperty( "user.name" ) + ".properties";

                            status.add(
                                SDKCorePlugin.createErrorStatus(
                                    "Invalid SDK settings. Configure app.server.parent.dir property in " +
                                        customerPropertyFile + " to point to Liferay home" ) );
                        }

                        break;
                    }
                    default:
                }
            }
        }

        return status;
    }

    public IStatus war(
        IProject project, Map<String, String> overrideProperties, boolean separateJRE,
        IProgressMonitor monitor )
    {
        return war( project, overrideProperties, separateJRE, null, monitor );
    }

    public IStatus war(
        IProject project, Map<String, String> overrideProperties, boolean separateJRE,
        String[] vmargs, IProgressMonitor monitor )
    {
        try
        {
            SDKHelper antHelper = new SDKHelper( this, monitor );
            antHelper.setVMArgs( vmargs );

            Map<String, String> properties = new HashMap<String, String>();

            if( overrideProperties != null )
            {
                properties.putAll( overrideProperties );
            }

            final IPath buildFile = project.getFile( ISDKConstants.PROJECT_BUILD_XML ).getRawLocation();
            final String workingDir = getDefaultWorkingDir( buildFile );

            antHelper.runTarget( buildFile, ISDKConstants.TARGET_WAR, properties, separateJRE, workingDir );
        }
        catch( Exception e )
        {
            return SDKCorePlugin.createErrorStatus( e );
        }

        return Status.OK_STATUS;
    }

    private static class Msgs extends NLS
    {
        public static String buildXmlFileNotExist;
        public static String SDKLocationInvalid;

        static
        {
            initializeMessages( SDK.class.getName(), Msgs.class );
        }
    }
}
