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

package com.liferay.ide.server.tomcat.core.util;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.IPluginPublisher;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatConstants;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatRuntime;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatServer;
import com.liferay.ide.server.tomcat.core.LiferayTomcatPlugin;
import com.liferay.ide.server.tomcat.core.LiferayTomcatRuntime70;
import com.liferay.ide.server.tomcat.core.LiferayTomcatServerBehavior;
import com.liferay.ide.server.util.LiferayPortalValueLoader;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jst.server.tomcat.core.internal.TomcatVersionHelper;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerEvent;
import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class LiferayTomcatUtil
{

    private static String CONFIG_DIR = "conf"; //$NON-NLS-1$

    public static final String CONFIG_TYPE_SERVER = "server"; //$NON-NLS-1$
    public static final String CONFIG_TYPE_VERSION = "version"; //$NON-NLS-1$
    private static String DEFAULT_PORTAL_CONTEXT_FILE = "ROOT.xml"; //$NON-NLS-1$

    private static String DEFAULT_PORTAL_DIR = "/webapps/ROOT"; //$NON-NLS-1$
    private static String HOST_NAME = "localhost"; //$NON-NLS-1$

    // to read liferay info from manifest need at least version 6.2.0
    private static final Version MANIFEST_VERSION_REQUIRED = ILiferayConstants.V620;
    private static String SERVICE_NAME = "Catalina"; //$NON-NLS-1$

    public static void addRuntimeVMArgments(
        List<String> runtimeVMArgs, IPath installPath, IPath configPath, IPath deployPath, boolean isTestEnv,
        IServer currentServer, ILiferayTomcatServer liferayTomcatServer )
    {
        runtimeVMArgs.add( "-Dfile.encoding=UTF8" ); //$NON-NLS-1$
        runtimeVMArgs.add( "-Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false" ); //$NON-NLS-1$
        runtimeVMArgs.add( "-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager" ); //$NON-NLS-1$

        ILiferayRuntime runtime = ServerUtil.getLiferayRuntime( currentServer );
        Version portalVersion = new Version( getVersion( runtime ) );

        if( CoreUtil.compareVersions( portalVersion, LiferayTomcatRuntime70.leastSupportedVersion ) < 0 )
        {
            runtimeVMArgs.add( "-Djava.security.auth.login.config=\"" + configPath.toOSString() + "/conf/jaas.config\"" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else
        {
            runtimeVMArgs.add( "-Djava.net.preferIPv4Stack=true" ); //$NON-NLS-1$
        }

        runtimeVMArgs.add( "-Djava.util.logging.config.file=\"" + installPath.toOSString() + //$NON-NLS-1$
            "/conf/logging.properties\"" ); //$NON-NLS-1$
        runtimeVMArgs.add( "-Djava.io.tmpdir=\"" + installPath.toOSString() + "/temp\"" ); //$NON-NLS-1$ //$NON-NLS-2$

        final boolean useDefaultPortalServerSettings = liferayTomcatServer.getUseDefaultPortalServerSettings();

        if ( useDefaultPortalServerSettings )
        {
            addUserDefaultVMArgs( runtimeVMArgs );
        }
        else
        {
            addUserVMArgs( runtimeVMArgs, currentServer, liferayTomcatServer );

            File externalPropertiesFile =
                getExternalPropertiesFile( installPath, configPath, currentServer, liferayTomcatServer );

            runtimeVMArgs.add( "-Dexternal-properties=\"" + externalPropertiesFile.getAbsolutePath() + "\"" );
        }
    }

    private static void addUserDefaultVMArgs( List<String> runtimeVMArgs )
    {
        String[] memoryArgs = ILiferayTomcatConstants.DEFAULT_MEMORY_ARGS.split( StringPool.SPACE );

        if( memoryArgs != null )
        {
            for( String arg : memoryArgs )
            {
                runtimeVMArgs.add( arg );
            }
        }
    }

    private static void addUserVMArgs(
        List<String> runtimeVMArgs, IServer currentServer, ILiferayTomcatServer portalTomcatServer )
    {
        String[] memoryArgs = ILiferayTomcatConstants.DEFAULT_MEMORY_ARGS.split( StringPool.SPACE );
        String userTimezone = ILiferayTomcatConstants.DEFAULT_USER_TIMEZONE;

        if( currentServer != null && portalTomcatServer != null )
        {
            memoryArgs = DebugPlugin.parseArguments( portalTomcatServer.getMemoryArgs() );

            userTimezone = portalTomcatServer.getUserTimezone();
        }

        if( memoryArgs != null )
        {
            for( String arg : memoryArgs )
            {
                runtimeVMArgs.add( arg );
            }
        }

        runtimeVMArgs.add( "-Duser.timezone=" + userTimezone ); //$NON-NLS-1$
    }

    public static IStatus canAddModule( IModule module, IServer currentServer )
    {
        IProject project = module.getProject();

        if( project != null )
        {
            IFacetedProject facetedProject = ProjectUtil.getFacetedProject( project );

            if( facetedProject != null )
            {
                IProjectFacet liferayFacet = ProjectUtil.getLiferayFacet( facetedProject );

                if( liferayFacet != null )
                {
                    String facetId = liferayFacet.getId();

                    IRuntime runtime = null;

                    try
                    {
                        runtime = ServerUtil.getRuntime( project );
                    }
                    catch( CoreException e )
                    {
                    }

                    if( runtime != null )
                    {
                        IPluginPublisher pluginPublisher =
                            LiferayServerCore.getPluginPublisher( facetId, runtime.getRuntimeType().getId() );

                        if( pluginPublisher != null )
                        {
                            IStatus status = pluginPublisher.canPublishModule( currentServer, module );

                            if( !status.isOK() )
                            {
                                return status;
                            }
                        }
                    }
                }
            }
        }

        return Status.OK_STATUS;
    }

    /*
     * Added for IDE-646
     */
    protected static IPath checkAndReturnCustomPortalDir( IPath appServerDir )
    {
        IPath retval = null;

        if( appServerDir != null )
        {
            File contextFile =
                appServerDir.append( CONFIG_DIR ).append( SERVICE_NAME ).append( HOST_NAME ).append(
                    DEFAULT_PORTAL_CONTEXT_FILE ).toFile();

            if( contextFile.exists() )
            {
                Context tcPortalContext = loadContextFile( contextFile );

                if( tcPortalContext != null )
                {
                    String docBase = tcPortalContext.getDocBase();

                    if( docBase != null )
                    {
                        return new Path( docBase );
                    }
                }
            }

            if( retval == null )
            {
                retval = appServerDir.append( DEFAULT_PORTAL_DIR );
            }
        }

        return retval;
    }

    public static void displayToggleMessage( String msg, String key )
    {
//        UIUtil.postInfoWithToggle(
//            Msgs.liferayTomcatServer, msg, Msgs.notShowMessage, false, LiferayTomcatPlugin.getPreferenceStore(), key );
    }

    private static File ensurePortalIDEPropertiesExists(
        IPath installPath, IPath configPath, IServer currentServer, ILiferayTomcatServer portalServer )
    {

        IPath idePropertiesPath = installPath.append( "../portal-ide.properties" ); //$NON-NLS-1$

        String hostName = "localhost"; //$NON-NLS-1$

        try
        {
            ServerInstance server =
                TomcatVersionHelper.getCatalinaServerInstance( configPath.append( "conf/server.xml" ), null, null ); //$NON-NLS-1$

            hostName = server.getHost().getName();
        }
        catch( Exception e )
        {
            LiferayTomcatPlugin.logError( e );
        }

        // read portal-developer.properties
        // Properties devProps = new Properties();
        // IPath devPropertiesPath =
        // installPath.append("webapps/ROOT/WEB-INF/classes/portal-developer.properties");
        // if (devPropertiesPath.toFile().exists()) {
        // devProps.load(new FileReader(devPropertiesPath.toFile()));
        // }

        // if (idePropertiesPath.toFile().exists()) {
        // String value =
        // CoreUtil.readPropertyFileValue(idePropertiesPath.toFile(),
        // "auto.deploy.tomcat.conf.dir");
        // if (configPath.append("conf/Catalina/"+hostName).toFile().equals(new
        // File(value))) {
        // return;
        // }
        // }

        Properties props = new Properties();

        if( portalServer != null && portalServer.getServerMode() == ILiferayTomcatConstants.DEVELOPMENT_SERVER_MODE )
        {
            props.put( "include-and-override", "portal-developer.properties" );
        }

        props.put( "com.liferay.portal.servlet.filters.etag.ETagFilter", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
        props.put( "com.liferay.portal.servlet.filters.header.HeaderFilter", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
        props.put( "json.service.auth.token.enabled", "false" ); //$NON-NLS-1$ //$NON-NLS-2$

        props.put( "auto.deploy.tomcat.conf.dir", configPath.append( "conf/Catalina/" + hostName ).toOSString() ); //$NON-NLS-1$ //$NON-NLS-2$

        if( currentServer != null && portalServer != null )
        {
            IPath runtimLocation = currentServer.getRuntime().getLocation();

            String autoDeployDir = portalServer.getAutoDeployDirectory();

            if( !ILiferayTomcatConstants.DEFAULT_AUTO_DEPLOYDIR.equals( autoDeployDir ) )
            {
                IPath autoDeployDirPath = new Path( autoDeployDir );

                if( autoDeployDirPath.isAbsolute() && autoDeployDirPath.toFile().exists() )
                {
                    props.put( "auto.deploy.deploy.dir", portalServer.getAutoDeployDirectory() ); //$NON-NLS-1$
                }
                else
                {
                    File autoDeployDirFile = new File( runtimLocation.toFile(), autoDeployDir );

                    if( autoDeployDirFile.exists() )
                    {
                        props.put( "auto.deploy.deploy.dir", autoDeployDirFile.getPath() ); //$NON-NLS-1$
                    }
                }
            }

            props.put( "auto.deploy.interval", portalServer.getAutoDeployInterval() ); //$NON-NLS-1$
        }

        // props.put( "json.service.public.methods", "*" );
        props.put( "jsonws.web.service.public.methods", "*" ); //$NON-NLS-1$ //$NON-NLS-2$

        File file = idePropertiesPath.toFile();

        try
        {
            props.store( Files.newOutputStream( file.toPath() ), null );
        }
        catch( Exception e )
        {
            LiferayTomcatPlugin.logError( e );
        }

        return file;
    }

    public static IPath[] getAllUserClasspathLibraries( IPath runtimeLocation, IPath portalDir )
    {
        List<IPath> libs = new ArrayList<IPath>();
        IPath libFolder = runtimeLocation.append( "lib" ); //$NON-NLS-1$
        IPath extLibFolder = runtimeLocation.append( "lib/ext" ); //$NON-NLS-1$
        IPath webinfLibFolder = portalDir.append( "WEB-INF/lib" ); //$NON-NLS-1$

        try
        {
            List<File> libFiles = FileListing.getFileListing( new File( libFolder.toOSString() ) );

            for( File lib : libFiles )
            {
                if( lib.exists() && lib.getName().endsWith( ".jar" ) ) //$NON-NLS-1$
                {
                    libs.add( new Path( lib.getPath() ) );
                }
            }

            List<File> extLibFiles = FileListing.getFileListing( new File( extLibFolder.toOSString() ) );

            for( File lib : extLibFiles )
            {
                if( lib.exists() && lib.getName().endsWith( ".jar" ) ) //$NON-NLS-1$
                {
                    libs.add( new Path( lib.getPath() ) );
                }
            }

            libFiles = FileListing.getFileListing( new File( webinfLibFolder.toOSString() ) );

            for( File lib : libFiles )
            {
                if( lib.exists() && lib.getName().endsWith( ".jar" ) ) //$NON-NLS-1$
                {
                    libs.add( new Path( lib.getPath() ) );
                }
            }
        }
        catch( FileNotFoundException e )
        {
            LiferayTomcatPlugin.logError( e );
        }

        return libs.toArray( new IPath[0] );
    }

    public static String getConfigInfoFromCache( String configType, IPath portalDir )
    {
        IPath configInfoPath = null;

        if( configType.equals( CONFIG_TYPE_VERSION ) )
        {
            configInfoPath = LiferayTomcatPlugin.getDefault().getStateLocation().append( "version.properties" ); //$NON-NLS-1$
        }

        else if( configType.equals( CONFIG_TYPE_SERVER ) )
        {
            configInfoPath = LiferayTomcatPlugin.getDefault().getStateLocation().append( "serverInfos.properties" ); //$NON-NLS-1$
        }

        else
        {
            return null;
        }

        File configInfoFile = configInfoPath.toFile();

        String portalDirKey = CoreUtil.createStringDigest( portalDir.toPortableString() );

        Properties properties = new Properties();

        if( configInfoFile.exists() )
        {
            try(InputStream fileInput = Files.newInputStream( configInfoFile.toPath() ))
            {
                properties.load( fileInput );

                String configInfo = (String) properties.get( portalDirKey );

                if( !CoreUtil.isNullOrEmpty( configInfo ) )
                {
                    return configInfo;
                }
            }
            catch( Exception e )
            {
            }
        }

        return null;
    }

    public static String getConfigInfoFromManifest( String configType, IPath portalDir )
    {
        File implJar = portalDir.append( "WEB-INF/lib/portal-impl.jar" ).toFile(); //$NON-NLS-1$

        String version = null;
        String serverInfo = null;

        if( implJar.exists() )
        {
            try
            {
                @SuppressWarnings( "resource" )
                JarFile jar = new JarFile( implJar );

                Manifest manifest = jar.getManifest();

                Attributes attributes = manifest.getMainAttributes();

                version = attributes.getValue( "Liferay-Portal-Version" ); //$NON-NLS-1$
                serverInfo = attributes.getValue( "Liferay-Portal-Server-Info" ); //$NON-NLS-1$

                if( CoreUtil.compareVersions( Version.parseVersion( version ), MANIFEST_VERSION_REQUIRED ) < 0 )
                {
                    version = null;
                    serverInfo = null;
                }
            }
            catch( IOException e )
            {
                LiferayTomcatPlugin.logError( e );
            }
        }

        if( configType.equals( CONFIG_TYPE_VERSION ) )
        {
            return version;
        }

        if( configType.equals( CONFIG_TYPE_SERVER ) )
        {
            return serverInfo;
        }

        return null;
    }

    private static File getExternalPropertiesFile(
        IPath installPath, IPath configPath, IServer currentServer, ILiferayTomcatServer portalServer )
    {
        File retval = null;

        if( portalServer != null )
        {
            File portalIdePropFile =
                ensurePortalIDEPropertiesExists( installPath, configPath, currentServer, portalServer );

            retval = portalIdePropFile;

            String externalProperties = portalServer.getExternalProperties();

            if( !CoreUtil.isNullOrEmpty( externalProperties ) )
            {
                File externalPropertiesFile = setupExternalPropertiesFile( portalIdePropFile, externalProperties );

                if( externalPropertiesFile != null )
                {
                    retval = externalPropertiesFile;
                }
            }
        }

        return retval;
    }

    public static ILiferayTomcatRuntime getLiferayTomcatRuntime( IRuntime runtime )
    {
        if( runtime != null )
        {
            return (ILiferayTomcatRuntime) runtime.createWorkingCopy().loadAdapter( ILiferayTomcatRuntime.class, null );
        }

        return null;
    }

    public static IPath getPortalDir( IPath appServerDir )
    {
        return checkAndReturnCustomPortalDir( appServerDir );
    }

    public static String getVersion( ILiferayRuntime runtime )
    {
        String version = getConfigInfoFromCache( CONFIG_TYPE_VERSION, runtime.getAppServerPortalDir() );

        if( version == null )
        {
            version = getConfigInfoFromManifest( CONFIG_TYPE_VERSION, runtime.getAppServerPortalDir() );

            if( version == null )
            {
                final LiferayPortalValueLoader loader = new LiferayPortalValueLoader( runtime.getUserLibs() );

                final Version loadedVersion = loader.loadVersionFromClass();

                if( loadedVersion != null )
                {
                    version = loadedVersion.toString();
                }
            }

            if( version != null )
            {
                saveConfigInfoIntoCache( CONFIG_TYPE_VERSION, version, runtime.getAppServerPortalDir() );
            }
        }

        return version;
    }

    public static boolean isExtProjectContext( Context context )
    {
        return false;
    }

    public static boolean isLiferayModule( IModule module )
    {
        boolean retval = false;

        if( module != null )
        {
            IProject project = module.getProject();

            retval = ProjectUtil.isLiferayFacetedProject( project );
        }

        return retval;
    }

    public static Context loadContextFile( File contextFile )
    {
        Context context = null;

        if( contextFile != null && contextFile.exists() )
        {
            try(InputStream fis = Files.newInputStream( contextFile.toPath() ))
            {
                Factory factory = new Factory();
                factory.setPackageName( "org.eclipse.jst.server.tomcat.core.internal.xml.server40" ); //$NON-NLS-1$
                context = (Context) factory.loadDocument( fis );
                if( context != null )
                {
                    String path = context.getPath();
                    // If path attribute is not set, derive from file name
                    if( path == null )
                    {
                        String fileName = contextFile.getName();
                        path = fileName.substring( 0, fileName.length() - ".xml".length() ); //$NON-NLS-1$
                        if( "ROOT".equals( path ) ) //$NON-NLS-1$
                            path = StringPool.EMPTY;
                        context.setPath( StringPool.FORWARD_SLASH + path );
                    }
                }
            }
            catch( Exception e )
            {
                // may be a spurious xml file in the host dir?
            }
        }
        return context;
    }

    public static IPath modifyLocationForBundle( IPath currentLocation )
    {
        IPath modifiedLocation = null;

        if( currentLocation == null || CoreUtil.isNullOrEmpty( currentLocation.toOSString() ) )
        {
            return null;
        }

        File location = currentLocation.toFile();

        if( location.exists() && location.isDirectory() )
        {
            // check to see if this location contains tomcat dir *tomcat*
            File[] files = location.listFiles();
            boolean matches = false;

            String pattern = ".*tomcat.*"; //$NON-NLS-1$

            File tomcatDir = null;

            for( File file : files )
            {
                if( file.isDirectory() && file.getName().matches( pattern ) )
                {
                    matches = true;

                    tomcatDir = file;

                    break;
                }
            }

            if( matches && tomcatDir != null )
            {
                modifiedLocation = new Path( tomcatDir.getPath() );
            }
        }

        return modifiedLocation;
    }

    public static void saveConfigInfoIntoCache( String configType, String configInfo, IPath portalDir )
    {
        IPath versionsInfoPath = null;

        if( configType.equals( CONFIG_TYPE_VERSION ) )
        {
            versionsInfoPath = LiferayTomcatPlugin.getDefault().getStateLocation().append( "version.properties" ); //$NON-NLS-1$
        }
        else if( configType.equals( CONFIG_TYPE_SERVER ) )
        {
            versionsInfoPath = LiferayTomcatPlugin.getDefault().getStateLocation().append( "serverInfos.properties" ); //$NON-NLS-1$
        }

        if( versionsInfoPath != null )
        {
            File versionInfoFile = versionsInfoPath.toFile();

            if( configInfo != null )
            {
                String portalDirKey = CoreUtil.createStringDigest( portalDir.toPortableString() );
                Properties properties = new Properties();

                try(InputStream fileInput = Files.newInputStream( versionInfoFile.toPath() ))
                {
                    properties.load( fileInput );
                }
                catch( NoSuchFileException e )
                {
                    // ignore filenotfound we likely just haven't had a file written yet.
                }
                catch( IOException e )
                {
                    LiferayTomcatPlugin.logError( e );
                }

                properties.put( portalDirKey, configInfo );

                try(OutputStream fileOutput = Files.newOutputStream( versionInfoFile.toPath() ))
                {
                    properties.store( fileOutput, StringPool.EMPTY );
                }
                catch( Exception e )
                {
                    LiferayTomcatPlugin.logError( e );
                }
            }
        }
    }

    private static File setupExternalPropertiesFile( File portalIdePropFile, String externalPropertiesPath )
    {
        File retval = null;
        // first check to see if there is an external properties file
        File externalPropertiesFile = new File( externalPropertiesPath );

        if( externalPropertiesFile.exists() )
        {
            ExternalPropertiesConfiguration props = new ExternalPropertiesConfiguration();

            try( InputStream newInputStream = Files.newInputStream( externalPropertiesFile.toPath() ))
            {
                props.load( newInputStream );
                props.setProperty( "include-and-override", portalIdePropFile.getAbsolutePath() ); //$NON-NLS-1$
                props.setHeader( "# Last modified by Liferay IDE " + new Date() ); //$NON-NLS-1$
                props.save( Files.newOutputStream( externalPropertiesFile.toPath() ) );

                retval = externalPropertiesFile;
            }
            catch( Exception e )
            {
                retval = null;
            }
        }
        else
        {
            retval = null; // don't setup an external properties file
        }

        return retval;
    }

    public static void syncStopServer( final IServer server )
    {
        if( server.getServerState() != IServer.STATE_STARTED )
        {
            return;
        }

        final LiferayTomcatServerBehavior serverBehavior =
            (LiferayTomcatServerBehavior) server.loadAdapter( LiferayTomcatServerBehavior.class, null );

        Thread shutdownThread = new Thread()
        {
            @Override
            public void run()
            {
                serverBehavior.stop( true );

                synchronized( server )
                {
                    try
                    {
                        server.wait( 5000 );
                    }
                    catch( InterruptedException e )
                    {
                    }
                }
            }

        };

        IServerListener shutdownListener = new IServerListener()
        {
            @Override
            public void serverChanged( ServerEvent event )
            {
                if( event.getState() == IServer.STATE_STOPPED )
                {
                    synchronized( server )
                    {
                        server.notifyAll();
                    }
                }
            }
        };

        server.addServerListener( shutdownListener );

        try
        {
            shutdownThread.start();
            shutdownThread.join();
        }
        catch( InterruptedException e )
        {
        }

        server.removeServerListener( shutdownListener );
    }

    public static IStatus validateRuntimeStubLocation( String runtimeTypeId, IPath runtimeStubLocation )
    {
        try
        {
            IRuntimeWorkingCopy runtimeStub = ServerCore.findRuntimeType( runtimeTypeId ).createRuntime( null, null );
            runtimeStub.setLocation( runtimeStubLocation );
            runtimeStub.setStub( true );
            return runtimeStub.validate( null );
        }
        catch( Exception e )
        {
            return LiferayTomcatPlugin.createErrorStatus( e );
        }
    }

}
