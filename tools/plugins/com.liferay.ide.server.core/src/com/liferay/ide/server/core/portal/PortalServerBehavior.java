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

package com.liferay.ide.server.core.portal;

import aQute.remote.api.Agent;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.LiferayRuntimeClasspathEntry;
import com.liferay.ide.core.properties.PortalPropertiesConfiguration;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.ILiferayServerBehavior;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.PingThread;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
@SuppressWarnings( {"restriction","rawtypes"} )
public class PortalServerBehavior extends ServerBehaviourDelegate
    implements ILiferayServerBehavior, IJavaLaunchConfigurationConstants
{
    public static final String ATTR_STOP = "stop-server";

    private static final String[] JMX_EXCLUDE_ARGS = new String []
    {
        "-Dcom.sun.management.jmxremote",
        "-Dcom.sun.management.jmxremote.port=",
        "-Dcom.sun.management.jmxremote.ssl=",
        "-Dcom.sun.management.jmxremote.authenticate="
    };

    private IAdaptable info;
    private transient PingThread ping = null;
    private transient IDebugEventSetListener processListener;

    public PortalServerBehavior()
    {
        super();
    }

    public void addProcessListener( final IProcess newProcess )
    {
        if( processListener != null || newProcess == null )
        {
            return;
        }

        processListener = new IDebugEventSetListener()
        {
            public void handleDebugEvents( DebugEvent[] events )
            {
                if( events != null )
                {
                    for( DebugEvent event : events )
                    {
                        if( newProcess != null && newProcess.equals( event.getSource() ) &&
                            event.getKind() == DebugEvent.TERMINATE )
                        {
                            cleanup();
                        }
                    }
                }
            }
        };

        DebugPlugin.getDefault().addDebugEventListener( processListener );
    }

    @Override
    public boolean canRestartModule( IModule[] modules )
    {
        for( IModule module : modules )
        {
            IProject project = module.getProject();

            if( project == null )
            {
                return false;
            }

            final IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, project );

            if( bundleProject != null && !bundleProject.isFragmentBundle() )
            {
                return true;
            }
        }

        return false;
    }

    public void cleanup()
    {
        if( ping != null )
        {
            ping.stop();
            ping = null;
        }

        if( processListener != null )
        {
            DebugPlugin.getDefault().removeDebugEventListener( processListener );
            processListener = null;
        }

        setServerState( IServer.STATE_STOPPED );
    }

    public String getClassToLaunch()
    {
        return getPortalRuntime().getPortalBundle().getMainClass();
    }

    @Override
    public IPath getDeployedPath( IModule[] module )
    {
        return null;
    }

    public IAdaptable getInfo()
    {
        return this.info;
    }

    private int getNextToken( String s, int start )
    {
        int i = start;
        int length = s.length();
        char lookFor = ' ';

        while( i < length )
        {
            char c = s.charAt( i );

            if( lookFor == c )
            {
                if( lookFor == '"' )
                    return i + 1;
                return i;
            }

            if( c == '"' )
            {
                lookFor = '"';
            }

            i++;
        }
        return -1;
    }

    public PortalRuntime getPortalRuntime()
    {
        PortalRuntime retval = null;

        if( getServer().getRuntime() != null )
        {
            retval = (PortalRuntime) getServer().getRuntime().loadAdapter( PortalRuntime.class, null );
        }

        return retval;
    }

    private PortalServer getPortalServer()
    {
        PortalServer retval = null;

        if( getServer() != null )
        {
            retval = (PortalServer) getServer().loadAdapter( PortalServer.class, null );
        }

        return retval;
    }

    private String[] getRuntimeStartProgArgs()
    {
        return getPortalRuntime().getPortalBundle().getRuntimeStartProgArgs();
    }

    private String[] getRuntimeStartVMArguments()
    {
        if( !getPortalServer().getLaunchSettings() )
        {
            File portalext = getPortalRuntime().getLiferayHome().append( "portal-ext.properties" ).toFile();

            if( getPortalServer().getDeveloperMode() )
            {
                try
                {
                    if( !portalext.exists() )
                    {
                        portalext.createNewFile();
                    }

                    final PortalPropertiesConfiguration config = new PortalPropertiesConfiguration();

                    InputStream in = Files.newInputStream( portalext.toPath() );

                    config.load( in );

                    in.close();

                    String[] p = config.getStringArray( "include-and-override" );

                    boolean existing = false;

                    for( String prop : p )
                    {
                        if( prop.equals( "portal-developer.properties" ) )
                        {
                            existing = true;
                            break;
                        }
                    }

                    if( !existing )
                    {
                        config.addProperty( "include-and-override", "portal-developer.properties" );
                    }

                    config.save( portalext );
                }
                catch( Exception e )
                {
                    LiferayServerCore.logError( e );
                }
            }
            else if( portalext.exists() )
            {
                String contents = FileUtil.readContents( portalext, true );

                contents = contents.replace( "include-and-override=portal-developer.properties", "" );

                try
                {
                    FileUtils.write( portalext, contents );
                }
                catch( IOException e )
                {
                    LiferayServerCore.logError( e );
                }
            }
        }

        final List<String> retval = new ArrayList<>();

        Collections.addAll( retval, getPortalServer().getMemoryArgs() );

        Collections.addAll( retval, getPortalRuntime().getPortalBundle().getRuntimeStartVMArgs() );

        int agentPort = getServer().getAttribute( AGENT_PORT, Agent.DEFAULT_PORT );

        retval.add( "-D" + Agent.AGENT_SERVER_PORT_KEY + "=" + agentPort );

        return retval.toArray( new String[0] );
    }

    private String[] getRuntimeStopProgArgs()
    {
        return getPortalRuntime().getPortalBundle().getRuntimeStopProgArgs();
    }

    private String[] getRuntimeStopVMArguments()
    {
        final List<String> retval = new ArrayList<>();

        final String[] memoryArgs = getPortalServer().getMemoryArgs();
        
        if( memoryArgs != null )
        {
            Collections.addAll( retval, memoryArgs );
        }
        
        Collections.addAll( retval, getPortalRuntime().getPortalBundle().getRuntimeStopVMArgs() );

        return retval.toArray( new String[0] );
    }

    public void launchServer( ILaunch launch, String mode, IProgressMonitor monitor ) throws CoreException
    {
        if( "true".equals( launch.getLaunchConfiguration().getAttribute( ATTR_STOP, "false" ) ) )
        {
            return;
        }

        final IStatus status = getPortalRuntime().validate();

        if( status != null && status.getSeverity() == IStatus.ERROR )
            throw new CoreException( status );

        setServerRestartState( false );
        setServerState( IServer.STATE_STARTING );
        setMode( mode );

        try
        {
            String url = "http://" + getServer().getHost();

            final int port = Integer.parseInt( getPortalRuntime().getPortalBundle().getHttpPort() );

            if( port != 80 )
            {
                url += ":" + port;
            }

            ping = getPortalRuntime().getPortalBundle().createPingThread( getServer(), url, this );
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( "Can't ping for portal startup." );
        }
    }

    private String mergeArguments(
        final String orgArgsString, final String[] newArgs, final String[] excludeArgs, boolean keepActionLast )
    {
        String retval = null;

        if( CoreUtil.isNullOrEmpty( newArgs ) && CoreUtil.isNullOrEmpty( excludeArgs ) )
        {
            retval = orgArgsString;
        }
        else
        {
            retval = orgArgsString == null ? "" : orgArgsString;

            String xbootClasspath = "";

            // replace and null out all newArgs that already exist
            final int size = newArgs.length;

            for( int i = 0; i < size; i++ )
            {
                if( newArgs[i].startsWith( "-Xbootclasspath" ) )
                {
                    xbootClasspath = xbootClasspath + newArgs[i] + " ";

                    newArgs[i] = null;

                    continue;
                }

                final int ind = newArgs[i].indexOf( " " );
                final int ind2 = newArgs[i].indexOf( "=" );

                if( ind >= 0 && ( ind2 == -1 || ind < ind2 ) )
                { // -a bc style
                    int index = retval.indexOf( newArgs[i].substring( 0, ind + 1 ) );

                    if( index == 0 || ( index > 0 && Character.isWhitespace( retval.charAt( index - 1 ) ) ) )
                    {
                        newArgs[i] = null;
                    }
                }
                else if( ind2 >= 0 )
                { // a=b style
                    int index = retval.indexOf( newArgs[i].substring( 0, ind2 + 1 ) );

                    if( index == 0 || ( index > 0 && Character.isWhitespace( retval.charAt( index - 1 ) ) ) )
                    {
                        newArgs[i] = null;
                    }
                }
                else
                { // abc style
                    int index = retval.indexOf( newArgs[i] );

                    if( index == 0 || ( index > 0 && Character.isWhitespace( retval.charAt( index - 1 ) ) ) )
                    {
                        newArgs[i] = null;
                    }
                }
            }

            // remove excluded arguments
            if( excludeArgs != null && excludeArgs.length > 0 )
            {
                for( int i = 0; i < excludeArgs.length; i++ )
                {
                    int ind = excludeArgs[i].indexOf( " " );
                    int ind2 = excludeArgs[i].indexOf( "=" );

                    if( ind >= 0 && ( ind2 == -1 || ind < ind2 ) )
                    { // -a bc style
                        int index = retval.indexOf( excludeArgs[i].substring( 0, ind + 1 ) );

                        if( index == 0 || ( index > 0 && Character.isWhitespace( retval.charAt( index - 1 ) ) ) )
                        {
                            // remove
                            String s = retval.substring( 0, index );
                            int index2 = getNextToken( retval, index + ind + 1 );

                            if( index2 >= 0 )
                            {
                                // If remainder will become the first argument, remove leading blanks
                                while( index2 < retval.length() &&
                                    Character.isWhitespace( retval.charAt( index2 ) ) )
                                    index2 += 1;
                                retval = s + retval.substring( index2 );
                            }
                            else
                                retval = s;
                        }
                    }
                    else if( ind2 >= 0 )
                    { // a=b style
                        int index = retval.indexOf( excludeArgs[i].substring( 0, ind2 + 1 ) );

                        if( index == 0 || ( index > 0 && Character.isWhitespace( retval.charAt( index - 1 ) ) ) )
                        {
                            // remove
                            String s = retval.substring( 0, index );
                            int index2 = getNextToken( retval, index );

                            if( index2 >= 0 )
                            {
                                // If remainder will become the first argument, remove leading blanks
                                while( index2 < retval.length() &&
                                    Character.isWhitespace( retval.charAt( index2 ) ) )
                                    index2 += 1;
                                retval = s + retval.substring( index2 );
                            }
                            else
                                retval = s;
                        }
                    }
                    else
                    { // abc style
                        int index = retval.indexOf( excludeArgs[i] );

                        if( index == 0 || ( index > 0 && Character.isWhitespace( retval.charAt( index - 1 ) ) ) )
                        {
                            // remove
                            String s = retval.substring( 0, index );
                            int index2 = getNextToken( retval, index );
                            if( index2 >= 0 )
                            {
                                // Remove leading blanks
                                while( index2 < retval.length() &&
                                    Character.isWhitespace( retval.charAt( index2 ) ) )
                                    index2 += 1;
                                retval = s + retval.substring( index2 );
                            }
                            else
                                retval = s;
                        }
                    }
                }
            }

            // add remaining vmargs to the end
            for( int i = 0; i < size; i++ )
            {
                if( newArgs[i] != null )
                {
                    if( retval.length() > 0 && !retval.endsWith( " " ) )
                    {
                        retval += " ";
                    }

                    retval += newArgs[i];
                }
            }

            if( !CoreUtil.isNullOrEmpty( xbootClasspath ) )
            {
                // delete xbootclasspath
                int xbootIndex = retval.lastIndexOf( "-Xbootclasspath" );

                while( xbootIndex != -1 )
                {
                    String head = retval.substring( 0, xbootIndex );

                    int tailIndex = getNextToken( retval, xbootIndex );

                    String tail = retval.substring( tailIndex == retval.length() ? retval.length() : tailIndex + 1 );

                    retval = head + tail;

                    xbootIndex = retval.lastIndexOf( "-Xbootclasspath" );
                }

                retval = retval + " " + xbootClasspath;
            }
        }

        return retval;
    }

    private void mergeClasspath( List<IRuntimeClasspathEntry> oldCpEntries, IRuntimeClasspathEntry cpEntry )
    {
        for( IRuntimeClasspathEntry oldCpEntry : oldCpEntries )
        {
            if( oldCpEntry.getPath().equals( cpEntry.getPath() ) )
            {
                return;
            }
        }

        oldCpEntries.add( cpEntry );
    }

    @Override
    public synchronized void publish( int kind, List<IModule[]> modules, IProgressMonitor monitor, IAdaptable info )
        throws CoreException
    {
        this.info = info;// save info

        super.publish( kind, modules, monitor, info );

        this.info = null;
    }

    @Override
    protected void publishModule(
        final int kind, final int deltaKind, final IModule[] modules, final IProgressMonitor monitor )
        throws CoreException
    {
        // publishing is done by PortalPublishTask
        return;
    }


    @Override
    protected void publishServer( int kind, IProgressMonitor monitor ) throws CoreException
    {
        setServerPublishState(IServer.PUBLISH_STATE_UNKNOWN);
    }

    @Override
    public void redeployModule( final IModule[] module ) throws CoreException
    {
        setModulePublishState( module, IServer.PUBLISH_STATE_FULL );

        IAdaptable info = new IAdaptable()
        {
            @SuppressWarnings( "unchecked" )
            public Object getAdapter( Class adapter )
            {
                if( String.class.equals( adapter ) )
                {
                    return "user"; //$NON-NLS-1$
                }
                else if( IModule.class.equals( adapter ) )
                {
                    return module[0];
                }

                return null;
            }
        };

        final List<IModule[]> modules = new ArrayList<>();
        modules.add( module );

        publish( IServer.PUBLISH_FULL, modules, null, info );
    }

    private void replaceJREConatiner( List<IRuntimeClasspathEntry> oldCp, IRuntimeClasspathEntry newJRECp )
    {
        int size = oldCp.size();

        for( int i = 0; i < size; i++ )
        {
            final IRuntimeClasspathEntry entry2 = oldCp.get( i );

            if( entry2.getPath().uptoSegment( 2 ).isPrefixOf( newJRECp.getPath() ) )
            {
                oldCp.set( i, newJRECp );
                return;
            }
        }

        oldCp.add( 0, newJRECp );
    }

    public void setModulePublishState2( IModule[] module, int state  )
    {
        super.setModulePublishState( module, state );
    }

    public void setModuleState2( IModule[] modules, int state )
    {
        super.setModuleState( modules, state );
    }

    public void setServerStarted()
    {
        setServerState( IServer.STATE_STARTED );
    }

    @Override
    public void setupLaunchConfiguration( ILaunchConfigurationWorkingCopy launch, IProgressMonitor monitor )
        throws CoreException
    {
        final String existingProgArgs = launch.getAttribute( ATTR_PROGRAM_ARGUMENTS, (String) null );
        launch.setAttribute( ATTR_PROGRAM_ARGUMENTS, mergeArguments( existingProgArgs, getRuntimeStartProgArgs(), null, true ) );

        final String existingVMArgs = launch.getAttribute( ATTR_VM_ARGUMENTS, (String) null );

        final String[] configVMArgs = getRuntimeStartVMArguments();
        launch.setAttribute( ATTR_VM_ARGUMENTS, mergeArguments( existingVMArgs, configVMArgs, null, false ) );

        final PortalRuntime portalRuntime = getPortalRuntime();
        final IVMInstall vmInstall = portalRuntime.getVMInstall();

        if( vmInstall != null )
        {
            launch.setAttribute( ATTR_JRE_CONTAINER_PATH, JavaRuntime.newJREContainerPath( vmInstall ).toPortableString() );
        }

        final IRuntimeClasspathEntry[] orgClasspath = JavaRuntime.computeUnresolvedRuntimeClasspath( launch );
        final int orgClasspathSize = orgClasspath.length;

        final List<IRuntimeClasspathEntry> oldCp = new ArrayList<>( orgClasspathSize );
        Collections.addAll( oldCp, orgClasspath );

        final List<IRuntimeClasspathEntry> runCpEntries = portalRuntime.getRuntimeClasspathEntries();

        for( IRuntimeClasspathEntry cpEntry : runCpEntries )
        {
            mergeClasspath( oldCp, cpEntry );
        }

        if( vmInstall != null )
        {
            try
            {
                final String typeId = vmInstall.getVMInstallType().getId();
                final IRuntimeClasspathEntry newJRECp =
                    JavaRuntime.newRuntimeContainerClasspathEntry(
                        new Path( JavaRuntime.JRE_CONTAINER ).append( typeId ).append( vmInstall.getName() ),
                        IRuntimeClasspathEntry.BOOTSTRAP_CLASSES );
                replaceJREConatiner( oldCp, newJRECp );
            }
            catch( Exception e )
            {
                // ignore
            }

            final IPath jrePath = new Path( vmInstall.getInstallLocation().getAbsolutePath() );

            if( jrePath != null )
            {
                final IPath toolsPath = jrePath.append( "lib/tools.jar" );

                if( toolsPath.toFile().exists() )
                {
                    final IRuntimeClasspathEntry toolsJar = JavaRuntime.newArchiveRuntimeClasspathEntry( toolsPath );
                    // Search for index to any existing tools.jar entry
                    int toolsIndex;

                    for( toolsIndex = 0; toolsIndex < oldCp.size(); toolsIndex++ )
                    {
                        final IRuntimeClasspathEntry entry = oldCp.get( toolsIndex );

                        if( entry.getType() == IRuntimeClasspathEntry.ARCHIVE &&
                            entry.getPath().lastSegment().equals( "tools.jar" ) )
                        {
                            break;
                        }
                    }

                    // If existing tools.jar found, replace in case it's different. Otherwise add.
                    if( toolsIndex < oldCp.size() )
                    {
                        oldCp.set( toolsIndex, toolsJar );
                    }
                    else
                    {
                        mergeClasspath( oldCp, toolsJar );
                    }
                }
            }
        }

        final List<String> cp = new ArrayList<>();

        for( IRuntimeClasspathEntry entry : oldCp )
        {
            try
            {
                if ( entry.getClasspathEntry().getEntryKind() !=  IClasspathEntry.CPE_CONTAINER)
                {
                    entry = new LiferayRuntimeClasspathEntry(entry.getClasspathEntry());
                }
                cp.add( entry.getMemento() );
            }
            catch( Exception e )
            {
                LiferayServerCore.logError( "Could not resolve cp entry " + entry, e );
            }
        }

        launch.setAttribute( ATTR_CLASSPATH, cp );
        launch.setAttribute( ATTR_DEFAULT_CLASSPATH, false );

        setupAgent();

        setupAriesJmxBundles();
    }

    private void setupAgent()
    {
        // make sure that agent is either installed or will be installed in modules folder

        // delete legacy jar in static folder cause update the same jar in static will cause portal fail to start
        final IPath staticPath = getPortalRuntime().getPortalBundle().getLiferayHome().append( "osgi/static" );

        if( staticPath.append( "biz.aQute.remote.agent.jar" ).toFile().exists() )
        {
            try
            {
                Files.delete( Paths.get( staticPath.append( "biz.aQute.remote.agent.jar" ).toOSString() ) );
            }
            catch( IOException e )
            {
                LiferayServerCore.logError( "Unable to remove old remote agent bundle", e );
            }
        }

        // check current version of agent and delete old jar and copy latest
        final IPath modulesPath = getPortalRuntime().getPortalBundle().getLiferayHome().append( "osgi/modules" );
        final IPath agentInstalledPath = modulesPath.append( "biz.aQute.remote.agent.jar" );

        File modulesDir = modulesPath.toFile();

        if( !modulesDir.exists() )
        {
            modulesDir.mkdirs();
        }

        File agentFile = agentInstalledPath.toFile();

        File embeddedAgentFile = null;
        String embeddedAgentVersion = null;

        try
        {
            embeddedAgentFile = new File(
                FileLocator.toFileURL( LiferayServerCore.getDefault().getBundle().getEntry(
                    "bundles/biz.aQute.remote.agent.jar" ) ).getFile() );
        }
        catch( IOException e )
        {
            LiferayServerCore.logError( "Unable to get embedded biz.aQute.remote.agent.jar", e );
        }

        try(JarFile embededJarFile = new JarFile( embeddedAgentFile ))
        {
            embeddedAgentVersion = embededJarFile.getManifest().getMainAttributes().getValue( "Bundle-Version" );
        }
        catch( IOException e )
        {
        }

        if( agentFile.exists() )
        {
            boolean shouldDelete = true;

            try(JarFile jarFile = new JarFile( agentFile ))
            {
                String bundleVersion = jarFile.getManifest().getMainAttributes().getValue( "Bundle-Version" );

                if( !CoreUtil.empty( bundleVersion ) )
                {
                    Version atLeastVersion = new Version( embeddedAgentVersion );
                    Version version = new Version( bundleVersion );

                    if( version.compareTo( atLeastVersion ) >= 0 )
                    {
                        shouldDelete = false;
                    }
                }
            }
            catch( IOException e )
            {
            }

            if( shouldDelete )
            {
                try
                {
                    Files.delete( agentFile.toPath() );
                }
                catch( IOException e )
                {
                    LiferayServerCore.logError( "Unable to remove old remote agent bundle", e );
                }
            }
        }

        if( !agentFile.exists() )
        {
            FileUtil.copyFile( embeddedAgentFile, agentFile );
        }
    }

    private void setupAriesJmxBundles()
    {
        String[] ariesJmxBundleNames = new String[] { "org.apache.aries.jmx.api.jar", "org.apache.aries.jmx.core.jar",
            "org.apache.aries.util.jar" };

        String[] ariesJxmBundleFullNames = new String[] { "org.apache.aries.jmx.api-1.1.5.jar",
            "org.apache.aries.jmx.core-1.1.7.jar", "org.apache.aries.util-1.1.3.jar" };

        // delelte legacy jmx bundles in osgi/static
        final IPath staticPath = getPortalRuntime().getPortalBundle().getLiferayHome().append( "osgi/static" );

        for( String bundleName : ariesJmxBundleNames )
        {
            File bundleFile = staticPath.append( bundleName ).toFile();

            if( bundleFile.exists() )
            {
                try
                {
                    Files.delete( bundleFile.toPath() );
                }
                catch( IOException e )
                {
                    LiferayServerCore.logError( "Unable to remove " + bundleName + " in liferay home osgi/static", e );
                }
            }
        }

        if( !shouldSetUpAriesJmxBundles() )
        {
            return;
        }

        final IPath modulesPath = getPortalRuntime().getPortalBundle().getLiferayHome().append( "osgi/modules" );

        File modulesDir = modulesPath.toFile();

        if( !modulesDir.exists() )
        {
            modulesDir.mkdirs();
        }

        for( int i = 0; i < ariesJmxBundleNames.length; i++ )
        {
            final IPath agentInstalledPath = modulesPath.append( ariesJmxBundleNames[i] );

            File bundleFile = agentInstalledPath.toFile();

            if( !bundleFile.exists() )
            {
                try
                {
                    final File file = new File(
                        FileLocator.toFileURL( LiferayServerCore.getDefault().getBundle().getEntry(
                            "bundles/" + ariesJxmBundleFullNames[i] ) ).getFile() );

                    FileUtil.copyFile( file, bundleFile );
                }
                catch( IOException e )
                {
                }
            }
        }
    }

    private boolean shouldSetUpAriesJmxBundles()
    {
        File portalImplFile =
            getPortalRuntime().getAppServerPortalDir().append( "WEB-INF/lib/portal-impl.jar" ).toFile();

        if( !portalImplFile.exists() )
        {
            return true;
        }

        try(JarFile jarFile = new JarFile( portalImplFile ))
        {
            Attributes manifest = jarFile.getManifest().getMainAttributes();

            String buildNumber = manifest.getValue( "Liferay-Portal-Build-Number" ).trim();

            String patchAttr = manifest.getValue( "Liferay-Portal-Installed-Patches" );

            // dxp version
            if( buildNumber.equals( "7010" ) )
            {
                if( CoreUtil.empty( patchAttr ) )
                {
                    // dxp-ga1
                    return false;
                }
                else
                {
                    String[] result = patchAttr.trim().split( "-" );

                    if( result == null || result.length < 3 )
                    {
                        return false;
                    }

                    int patchVersion = Integer.parseInt( result[1] );

                    // from dxp fix pack 9 (de-9-7010), the aries jmx bundles are removed
                    return patchVersion >= 9;
                }
            }
            else if( buildNumber.startsWith( "700" ) )
            {
                // ce version
                Version lp70ga3 = new Version( "7002" );
                Version currentCEVersion = new Version( buildNumber );

                // from ce-ga4, the aries jmx bundles are removed
                return currentCEVersion.compareTo( lp70ga3 ) > 0;
            }
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( "get portal version and patch info error", e );
        }

        return true;
    }

    @Override
    public void startModule( IModule[] modules, IProgressMonitor monitor ) throws CoreException
    {
        startOrStopModules( modules, "start", monitor );
    }

    @Override
    public void stopModule( IModule[] modules, IProgressMonitor monitor ) throws CoreException
    {
        startOrStopModules( modules, "stop", monitor );
    }

    private void startOrStopModules( IModule[] modules, String action, IProgressMonitor monitor )
    {
        for( IModule module : modules )
        {
            IProject project = module.getProject();

            if( project == null )
            {
                continue;
            }

            final IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, project );

            if( bundleProject != null )
            {
                BundleSupervisor supervisor = null;

                try
                {
                    final String symbolicName = bundleProject.getSymbolicName();

                    supervisor = createBundleSupervisor();

                    long bundleId = supervisor.getBundleId( symbolicName );

                    if( bundleId > 0 )
                    {
                        if( action.equals( "start" ) )
                        {
                            String error = supervisor.getAgent().start( bundleId );

                            if( error == null )
                            {
                                setModuleState( new IModule[] { module }, IServer.STATE_STARTED );
                            }
                            else
                            {
                                LiferayServerCore.logError( "Unable to start this bundle" );
                            }
                        }
                        else if( action.equals( "stop" ) )
                        {
                            String error = supervisor.getAgent().stop( bundleId );

                            if( error == null )
                            {
                                setModuleState( new IModule[] { module }, IServer.STATE_STOPPED );
                            }
                            else
                            {
                                LiferayServerCore.logError( "Unable to stop this bundle" );
                            }
                        }
                    }
                }
                catch( Exception e )
                {
                    LiferayServerCore.logError( "Unable to " + action + " module", e );
                }
                finally
                {
                    if( supervisor != null )
                    {
                        try
                        {
                            supervisor.close();
                        }
                        catch( IOException e )
                        {
                        }
                    }
                }
            }
        }
    }

    @Override
    public void stop( boolean force )
    {
        if( force )
        {
            terminate();
            return;
        }

        int state = getServer().getServerState();

        // If stopped or stopping, no need to run stop command again
        if (state == IServer.STATE_STOPPED || state == IServer.STATE_STOPPING)
        {
            return;
        }
        else if (state == IServer.STATE_STARTING)
        {
            terminate();
            return;
        }

        try
        {
            if( state != IServer.STATE_STOPPED )
            {
                setServerState( IServer.STATE_STOPPING );
            }

            final ILaunchConfiguration launchConfig = ( (Server) getServer() ).getLaunchConfiguration( false, null );
            final ILaunchConfigurationWorkingCopy wc = launchConfig.getWorkingCopy();

            final String args = renderCommandLine( getRuntimeStopProgArgs(), " " );
            // Remove JMX arguments if present
            final String existingVMArgs =
                wc.getAttribute( IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, (String) null );

            if( existingVMArgs.indexOf( JMX_EXCLUDE_ARGS[0] ) >= 0 )
            {
                wc.setAttribute(
                    IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
                    mergeArguments( existingVMArgs, getRuntimeStopVMArguments(), JMX_EXCLUDE_ARGS, false ) );
            }
            else
            {
                wc.setAttribute(
                    IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
                    mergeArguments( existingVMArgs, getRuntimeStopVMArguments(), null, true ) );
            }

            wc.setAttribute( IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, args );
            wc.setAttribute( "org.eclipse.debug.ui.private", true );
            wc.setAttribute( ATTR_STOP, "true" );

            wc.launch( ILaunchManager.RUN_MODE, new NullProgressMonitor() );
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( "Error stopping portal", e );
        }
    }

    protected void terminate()
    {
        if( getServer().getServerState() == IServer.STATE_STOPPED )
        {
            return;
        }

        try
        {
            setServerState( IServer.STATE_STOPPING );

            ILaunch launch = getServer().getLaunch();

            if( launch != null )
            {
                launch.terminate();
                cleanup();
            }
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( "Error killing the process", e );
        }
    }

    protected static String renderCommandLine( String[] commandLine, String separator )
    {
        if( commandLine == null || commandLine.length < 1 )
        {
            return "";
        }

        StringBuffer buf = new StringBuffer( commandLine[0] );

        for( int i = 1; i < commandLine.length; i++ )
        {
            buf.append( separator );
            buf.append( commandLine[i] );
        }

        return buf.toString();
    }

    public BundleSupervisor createBundleSupervisor() throws Exception
    {
        return ServerUtil.createBundleSupervisor( getPortalRuntime(), getServer() );
    }

}
