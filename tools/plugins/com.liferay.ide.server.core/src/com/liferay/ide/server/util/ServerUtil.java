/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.server.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.launching.StandardVMType;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.osgi.framework.Version;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCore;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Tao Tao
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class ServerUtil
{

    private static final Version v6110 = ILiferayConstants.V6110;
//    private static final Version v6120 = new Version( 6, 1, 20 );

    private static final Version v612 = ILiferayConstants.V612;

    private static final Version v620 = ILiferayConstants.V620;

    private static void addRemoveProps(
        IPath deltaPath, IResource deltaResource, ZipOutputStream zip, Map<ZipEntry, String> deleteEntries,
        String deletePrefix ) throws IOException
    {
        String archive = removeArchive( deltaPath.toPortableString() );

        ZipEntry zipEntry = null;

        // check to see if we already have an entry for this archive
        for( ZipEntry entry : deleteEntries.keySet() )
        {
            if( entry.getName().startsWith( archive ) )
            {
                zipEntry = entry;
            }
        }

        if( zipEntry == null )
        {
            zipEntry = new ZipEntry( archive + "META-INF/" + deletePrefix + "-partialapp-delete.props" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        String existingFiles = deleteEntries.get( zipEntry );

        // String file = encodeRemovedPath(deltaPath.toPortableString().substring(archive.length()));
        String file = deltaPath.toPortableString().substring( archive.length() );

        if( deltaResource.getType() == IResource.FOLDER )
        {
            file += "/.*"; //$NON-NLS-1$
        }

        deleteEntries.put( zipEntry, ( existingFiles != null ? existingFiles : StringPool.EMPTY ) + ( file + "\n" ) ); //$NON-NLS-1$
    }

    private static void addToZip( IPath path, IResource resource, ZipOutputStream zip, boolean adjustGMTOffset )
        throws IOException, CoreException
    {
        switch( resource.getType() )
        {
            case IResource.FILE:
                ZipEntry zipEntry = new ZipEntry( path.toString() );

                zip.putNextEntry( zipEntry );

                InputStream contents = ( (IFile) resource ).getContents();

                if( adjustGMTOffset )
                {
                    TimeZone currentTimeZone = TimeZone.getDefault();
                    Calendar currentDt = new GregorianCalendar( currentTimeZone, Locale.getDefault() );

                    // Get the Offset from GMT taking current TZ into account
                    int gmtOffset =
                        currentTimeZone.getOffset(
                            currentDt.get( Calendar.ERA ), currentDt.get( Calendar.YEAR ),
                            currentDt.get( Calendar.MONTH ), currentDt.get( Calendar.DAY_OF_MONTH ),
                            currentDt.get( Calendar.DAY_OF_WEEK ), currentDt.get( Calendar.MILLISECOND ) );

                    zipEntry.setTime( System.currentTimeMillis() + ( gmtOffset * -1 ) );
                }

                try
                {
                    IOUtils.copy( contents, zip );
                }
                finally
                {
                    contents.close();
                }

                break;

            case IResource.FOLDER:
            case IResource.PROJECT:
                IContainer container = (IContainer) resource;

                IResource[] members = container.members();

                for( IResource res : members )
                {
                    addToZip( path.append( res.getName() ), res, zip, adjustGMTOffset );
                }
        }
    }

    public static Map<String, String> configureAppServerProperties( ILiferayRuntime liferayRuntime )
    {
        return getSDKRequiredProperties( liferayRuntime );
    }

    public static Map<String, String> configureAppServerProperties( IProject project ) throws CoreException
    {
        try
        {
            return getSDKRequiredProperties( ServerUtil.getLiferayRuntime( project ) );
        }
        catch( CoreException e1 )
        {
            throw new CoreException( LiferayServerCore.createErrorStatus( e1 ) );
        }
    }

    public static IStatus createErrorStatus( String msg )
    {
        return new Status( IStatus.ERROR, LiferayServerCore.PLUGIN_ID, msg );
    }

    public static File createPartialEAR(
        String archiveName, IModuleResourceDelta[] deltas, String deletePrefix, String deltaPrefix,
        boolean adjustGMTOffset )
    {
        IPath path = LiferayServerCore.getTempLocation( "partial-ear", archiveName ); //$NON-NLS-1$

        FileOutputStream outputStream = null;
        ZipOutputStream zip = null;
        File file = path.toFile();

        file.getParentFile().mkdirs();

        try
        {
            outputStream = new FileOutputStream( file );
            zip = new ZipOutputStream( outputStream );

            Map<ZipEntry, String> deleteEntries = new HashMap<ZipEntry, String>();

            processResourceDeltasZip( deltas, zip, deleteEntries, deletePrefix, deltaPrefix, adjustGMTOffset );

            for( ZipEntry entry : deleteEntries.keySet() )
            {
                zip.putNextEntry( entry );
                zip.write( deleteEntries.get( entry ).getBytes() );
            }

            // if ((removedResources != null) && (removedResources.size() > 0)) {
            // writeRemovedResources(removedResources, zip);
            // }
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }
        finally
        {
            if( zip != null )
            {
                try
                {
                    zip.close();
                }
                catch( IOException localIOException1 )
                {

                }
            }
        }

        return file;
    }

    public static File createPartialWAR(
        String archiveName, IModuleResourceDelta[] deltas, String deletePrefix, boolean adjustGMTOffset )
    {
        IPath path = LiferayServerCore.getTempLocation( "partial-war", archiveName ); //$NON-NLS-1$

        FileOutputStream outputStream = null;
        ZipOutputStream zip = null;
        File file = path.toFile();

        file.getParentFile().mkdirs();

        try
        {
            outputStream = new FileOutputStream( file );
            zip = new ZipOutputStream( outputStream );

            Map<ZipEntry, String> deleteEntries = new HashMap<ZipEntry, String>();

            processResourceDeltasZip( deltas, zip, deleteEntries, deletePrefix, StringPool.EMPTY, adjustGMTOffset );

            for( ZipEntry entry : deleteEntries.keySet() )
            {
                zip.putNextEntry( entry );
                zip.write( deleteEntries.get( entry ).getBytes() );
            }

            // if ((removedResources != null) && (removedResources.size() > 0)) {
            // writeRemovedResources(removedResources, zip);
            // }
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }
        finally
        {
            if( zip != null )
            {
                try
                {
                    zip.close();
                }
                catch( IOException localIOException1 )
                {

                }
            }
        }

        return file;
    }

    public static IServerWorkingCopy createServerForRuntime( IRuntime runtime )
    {
        for( IServerType serverType : ServerCore.getServerTypes() )
        {
            if( serverType.getRuntimeType().equals( runtime.getRuntimeType() ) )
            {
                try
                {
                    return serverType.createServer( "server", null, runtime, null ); //$NON-NLS-1$
                }
                catch( CoreException e )
                {
                }
            }
        }

        return null;
    }

    public static Properties getAllCategories( IPath portalDir )
    {
        Properties retval = null;

        File implJar = portalDir.append( "WEB-INF/lib/portal-impl.jar" ).toFile(); //$NON-NLS-1$

        if( implJar.exists() )
        {
            try
            {
                JarFile jar = new JarFile( implJar );
                Properties categories = new Properties();
                Properties props = new Properties();
                props.load( jar.getInputStream( jar.getEntry( "content/Language.properties" ) ) ); //$NON-NLS-1$
                Enumeration<?> names = props.propertyNames();

                while( names.hasMoreElements() )
                {
                    String name = names.nextElement().toString();

                    if( name.startsWith( "category." ) ) //$NON-NLS-1$
                    {
                        categories.put( name, props.getProperty( name ) );
                    }
                }

                retval = categories;

            }
            catch( IOException e )
            {
                LiferayServerCore.logError( e );
            }
        }

        return retval;
    }

    public static IPath getAppServerDir( org.eclipse.wst.common.project.facet.core.runtime.IRuntime serverRuntime )
    {
        ILiferayRuntime runtime = (ILiferayRuntime) getRuntimeAdapter( serverRuntime, ILiferayRuntime.class );

        return runtime != null ? runtime.getAppServerDir() : null;
    }

    public static String getAppServerPropertyKey( String propertyAppServerDeployDir, ILiferayRuntime runtime )
    {
        String retval = null;

        try
        {
            final Version version = new Version( runtime.getPortalVersion() );
            final String type = runtime.getAppServerType();

            if( ( CoreUtil.compareVersions( version, v620 ) >= 0 ) ||
                ( CoreUtil.compareVersions( version, v612 ) >= 0 && CoreUtil.compareVersions( version, v6110 ) < 0 ) )
            {
                retval = MessageFormat.format( propertyAppServerDeployDir, "." + type + "." ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        catch( Exception e )
        {
        }
        finally
        {
            if( retval == null )
            {
                retval = MessageFormat.format( propertyAppServerDeployDir, "." ); //$NON-NLS-1$
            }
        }

        return retval;
    }

    public static Properties getEntryCategories( IPath portalDir, String portalVersion )
    {
        Properties categories = getAllCategories( portalDir );

        Properties retval = new Properties();

        final String myKey = "category.my"; //$NON-NLS-1$
        final String categoryMy = categories.getProperty( myKey );

        if( ( portalVersion == null ) || ( CoreUtil.compareVersions( new Version( portalVersion ), v620 ) < 0 ) )
        {
            final String portalKey = "category.portal"; //$NON-NLS-1$
            final String serverKey = "category.server"; //$NON-NLS-1$
            final String keyContent = "category.content"; //$NON-NLS-1$

            retval.put( myKey, categoryMy + " Account Section" ); //$NON-NLS-1$
            retval.put( portalKey, categories.getProperty( portalKey ) + " Section" ); //$NON-NLS-1$
            retval.put( serverKey, categories.getProperty( serverKey ) + " Section" ); //$NON-NLS-1$
            retval.put( keyContent, categories.getProperty( keyContent ) + " Section" ); //$NON-NLS-1$
        }
        else
        {
            final String keyUsers = "category.users"; //$NON-NLS-1$
            final String keyApps = "category.apps"; //$NON-NLS-1$
            final String keyConfig = "category.configuration"; //$NON-NLS-1$
            final String keySites = "category.sites"; //$NON-NLS-1$
            final String keySiteConfig = "category.site_administration.configuration"; //$NON-NLS-1$
            final String keySiteContent = "category.site_administration.content"; //$NON-NLS-1$
            final String keySitePages = "category.site_administration.pages"; //$NON-NLS-1$
            final String keySiteUsers = "category.site_administration.users"; //$NON-NLS-1$

            retval.put( myKey, categoryMy + " Account Administration" ); //$NON-NLS-1$
            retval.put( keyUsers, "Control Panel - " + categories.getProperty( keyUsers ) ); //$NON-NLS-1$
            retval.put( keyApps, "Control Panel - " + categories.getProperty( keyApps ) ); //$NON-NLS-1$
            retval.put( keyConfig, "Control Panel - " + categories.getProperty( keyConfig ) ); //$NON-NLS-1$
            retval.put( keySites, "Control Panel - " + categories.getProperty( keySites ) ); //$NON-NLS-1$
            retval.put( keySiteConfig, "Site Administration - " + categories.getProperty( keySiteConfig ) ); //$NON-NLS-1$
            retval.put( keySiteContent, "Site Administration - " + categories.getProperty( keySiteContent ) ); //$NON-NLS-1$
            retval.put( keySitePages, "Site Administration - " + categories.getProperty( keySitePages ) ); //$NON-NLS-1$
            retval.put( keySiteUsers, "Site Administration - " + categories.getProperty( keySiteUsers ) ); //$NON-NLS-1$
        }

        return retval;
    }

    public static IFacetedProject getFacetedProject( IProject project )
    {
        try
        {
            return ProjectFacetsManager.create( project );
        }
        catch( CoreException e )
        {
            return null;
        }
    }

    public static IProjectFacet getLiferayFacet( IFacetedProject facetedProject )
    {
        for( IProjectFacetVersion projectFacet : facetedProject.getProjectFacets() )
        {
            if( isLiferayFacet( projectFacet.getProjectFacet() ) )
            {
                return projectFacet.getProjectFacet();
            }
        }
        return null;
    }

    public static ILiferayRuntime getLiferayRuntime( BridgedRuntime bridgedRuntime )
    {
        if( bridgedRuntime != null )
        {
            String id = bridgedRuntime.getProperty( "id" ); //$NON-NLS-1$

            if( id != null )
            {
                IRuntime runtime = ServerCore.findRuntime( id );

                if( isLiferayRuntime( runtime ) )
                {
                    return getLiferayRuntime( runtime );
                }
            }
        }

        return null;
    }

    public static ILiferayRuntime getLiferayRuntime( IProject project ) throws CoreException
    {
        if( project == null )
        {
            return null;
        }

        IFacetedProject facetedProject = ProjectFacetsManager.create( project );

        if( facetedProject != null )
        {
            return (ILiferayRuntime) getRuntimeAdapter( facetedProject.getPrimaryRuntime(), ILiferayRuntime.class );
        }
        else
        {
            return null;
        }
    }

    public static Set<IRuntime> getAvailableLiferayRuntimes()
    {
        Set<IRuntime> retval = new HashSet<IRuntime>();

        IRuntime[] runtimes = ServerCore.getRuntimes();

        for( IRuntime rt : runtimes )
        {
            if( isLiferayRuntime( rt ) )
            {
                retval.add( rt );
            }
        }

        return retval;
    }

    public static ILiferayRuntime getLiferayRuntime( IRuntime runtime )
    {
        if( runtime != null )
        {
            return (ILiferayRuntime) runtime.createWorkingCopy().loadAdapter( ILiferayRuntime.class, null );
        }

        return null;
    }

    public static ILiferayRuntime getLiferayRuntime( IServer server )
    {
        if( server != null )
        {
            return getLiferayRuntime( server.getRuntime() );
        }

        return null;
    }

    public static IPath getPortalDir( IJavaProject project )
    {
        return getPortalDir( project.getProject() );
    }

    public static IPath getPortalDir( IProject project )
    {
        IPath retval = null;

        final ILiferayProject liferayProject = LiferayCore.create( project );

        if( liferayProject != null )
        {
            retval = liferayProject.getAppServerPortalDir();
        }

        return retval;
    }

    public static Properties getPortletCategories( IPath portalDir )
    {
        Properties props = getAllCategories( portalDir );
        Properties categories = new Properties();
        Enumeration<?> names = props.propertyNames();

        String[] controlPanelCategories =
        {   "category.my", //$NON-NLS-1$
            "category.users", //$NON-NLS-1$
            "category.apps", //$NON-NLS-1$
            "category.configuration", //$NON-NLS-1$
            "category.sites", //$NON-NLS-1$
            "category.site_administration.configuration", //$NON-NLS-1$
            "category.site_administration.content", //$NON-NLS-1$
            "category.site_administration.pages", //$NON-NLS-1$
            "category.site_administration.users" //$NON-NLS-1$
        };

        while( names.hasMoreElements() )
        {
            boolean isControlPanelCategory = false;

            String name = names.nextElement().toString();

            for( String category : controlPanelCategories )
            {
                if( name.equals( category ) )
                {
                    isControlPanelCategory = true;
                    break;
                }
            }

            if( !isControlPanelCategory )
            {
                categories.put( name, props.getProperty( name ) );
            }
        }

        return categories;
    }

    public static IRuntime getRuntime( IProject project ) throws CoreException
    {
        return (IRuntime) getRuntimeAdapter( ProjectFacetsManager.create( project ).getPrimaryRuntime(), IRuntime.class );
    }

    public static IRuntime getRuntime( org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime )
    {
        return ServerCore.findRuntime( runtime.getProperty( "id" ) ); //$NON-NLS-1$
    }

    public static IRuntime getRuntime( String runtimeName )
    {
        IRuntime retval = null;

        if( ! CoreUtil.isNullOrEmpty( runtimeName ) )
        {
            final IRuntime[] runtimes = ServerCore.getRuntimes();

            if( runtimes != null )
            {
                for( final IRuntime runtime : runtimes )
                {
                    if( runtimeName.equals( runtime.getName() ) )
                    {
                        retval = runtime;
                        break;
                    }
                }
            }
        }

        return retval;
    }

    public static IRuntimeWorkingCopy getRuntime( String runtimeTypeId, IPath location )
    {
        IRuntimeType runtimeType = ServerCore.findRuntimeType( runtimeTypeId );

        try
        {
            IRuntime runtime = runtimeType.createRuntime( "runtime", null ); //$NON-NLS-1$

            IRuntimeWorkingCopy runtimeWC = runtime.createWorkingCopy();
            runtimeWC.setName( "Runtime" ); //$NON-NLS-1$
            runtimeWC.setLocation( location );

            return runtimeWC;
        }
        catch( CoreException e )
        {
            e.printStackTrace();
        }

        return null;
    }

    public static Object getRuntimeAdapter(
        org.eclipse.wst.common.project.facet.core.runtime.IRuntime facetRuntime, Class<?> adapterClass )
    {
        if( facetRuntime != null )
        {
            String runtimeId = facetRuntime.getProperty( "id" ); //$NON-NLS-1$

            for( org.eclipse.wst.server.core.IRuntime runtime : ServerCore.getRuntimes() )
            {
                if( runtime.getId().equals( runtimeId ) )
                {

                    if( IRuntime.class.equals( adapterClass ) )
                    {
                        return runtime;
                    }

                    IRuntimeWorkingCopy runtimeWC = null;

                    if( !runtime.isWorkingCopy() )
                    {
                        runtimeWC = runtime.createWorkingCopy();
                    }
                    else
                    {
                        runtimeWC = (IRuntimeWorkingCopy) runtime;
                    }

                    return (ILiferayRuntime) runtimeWC.loadAdapter( adapterClass, null );
                }
            }
        }

        return null;
    }

    public static Map<String, String> getSDKRequiredProperties( ILiferayRuntime appServer )
    {
        Map<String, String> properties = new HashMap<String, String>();

        String type = appServer.getAppServerType();

        String dir = appServer.getAppServerDir().toOSString();

        String deployDir = appServer.getAppServerDeployDir().toOSString();

        String libGlobalDir = appServer.getAppServerLibGlobalDir().toOSString();

        String portalDir = appServer.getAppServerPortalDir().toOSString();

        properties.put( ISDKConstants.PROPERTY_APP_SERVER_TYPE, type );

        final String appServerDirKey =
            getAppServerPropertyKey( ISDKConstants.PROPERTY_APP_SERVER_DIR, appServer );
        final String appServerDeployDirKey =
            getAppServerPropertyKey( ISDKConstants.PROPERTY_APP_SERVER_DEPLOY_DIR, appServer );
        final String appServerLibGlobalDirKey =
            getAppServerPropertyKey( ISDKConstants.PROPERTY_APP_SERVER_LIB_GLOBAL_DIR, appServer );
        final String appServerPortalDirKey =
            getAppServerPropertyKey( ISDKConstants.PROPERTY_APP_SERVER_PORTAL_DIR, appServer );

        properties.put( appServerDirKey, dir );
        properties.put( appServerDeployDirKey, deployDir );
        properties.put( appServerLibGlobalDirKey, libGlobalDir );
        properties.put( appServerPortalDirKey, portalDir );

        return properties;
    }

    public static IServer[] getServersForRuntime( IRuntime runtime )
    {
        List<IServer> serverList = new ArrayList<IServer>();

        if( runtime != null )
        {
            IServer[] servers = ServerCore.getServers();

            if( !CoreUtil.isNullOrEmpty( servers ) )
            {
                for( IServer server : servers )
                {
                    if( runtime.equals( server.getRuntime() ) )
                    {
                        serverList.add( server );
                    }
                }
            }
        }

        return serverList.toArray( new IServer[0] );
    }

    public static String[] getServletFilterNames( IPath portalDir ) throws Exception
    {
        List<String> retval = new ArrayList<String>();

        File filtersWebXmlFile = portalDir.append( "WEB-INF/liferay-web.xml" ).toFile(); //$NON-NLS-1$

        if( !filtersWebXmlFile.exists() )
        {
            filtersWebXmlFile = portalDir.append( "WEB-INF/web.xml" ).toFile(); //$NON-NLS-1$
        }

        if( filtersWebXmlFile.exists() )
        {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( filtersWebXmlFile );

            NodeList filterNameElements = document.getElementsByTagName( "filter-name" ); //$NON-NLS-1$

            for( int i = 0; i < filterNameElements.getLength(); i++ )
            {
                Node filterNameElement = filterNameElements.item( i );

                String content = filterNameElement.getTextContent();

                if( !CoreUtil.isNullOrEmpty( content ) )
                {
                    retval.add( content.trim() );
                }
            }
        }

        return retval.toArray( new String[0] );
    }

    public static boolean hasFacet( IProject project, IProjectFacet checkProjectFacet )
    {
        boolean retval = false;
        if( project == null || checkProjectFacet == null )
        {
            return retval;
        }

        try
        {
            IFacetedProject facetedProject = ProjectFacetsManager.create( project );
            if( facetedProject != null && checkProjectFacet != null )
            {
                for( IProjectFacetVersion facet : facetedProject.getProjectFacets() )
                {
                    IProjectFacet projectFacet = facet.getProjectFacet();
                    if( checkProjectFacet.equals( projectFacet ) )
                    {
                        retval = true;
                        break;
                    }
                }
            }
        }
        catch( CoreException e )
        {
        }
        return retval;
    }

    public static boolean isExistingVMName( String name )
    {
        for( IVMInstall vm : JavaRuntime.getVMInstallType( StandardVMType.ID_STANDARD_VM_TYPE ).getVMInstalls() )
        {
            if( vm.getName().equals( name ) )
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isExtProject( IProject project )
    {
        return hasFacet( project, ProjectFacetsManager.getProjectFacet( "liferay.ext" ) ); //$NON-NLS-1$
    }

    public static boolean isLiferayFacet( IProjectFacet projectFacet )
    {
        return projectFacet != null && projectFacet.getId().startsWith( "liferay" ); //$NON-NLS-1$
    }

    public static boolean isLiferayRuntime( BridgedRuntime bridgedRuntime )
    {
        if( bridgedRuntime != null )
        {
            String id = bridgedRuntime.getProperty( "id" ); //$NON-NLS-1$

            if( id != null )
            {
                IRuntime runtime = ServerCore.findRuntime( id );

                return isLiferayRuntime( runtime );
            }
        }

        return false;
    }

    public static boolean isLiferayRuntime( IRuntime runtime )
    {
        return getLiferayRuntime( runtime ) != null;
    }
    public static boolean isLiferayRuntime( IServer server )
    {
        return getLiferayRuntime( server ) != null;
    }
    public static boolean isValidPropertiesFile( File file )
    {
        if( file == null || !file.exists() )
        {
            return false;
        }

        try
        {
            new PropertiesConfiguration( file );
        }
        catch( Exception e )
        {
            return false;
        }

        return true;

    }

    private static void processResourceDeltasZip(
        IModuleResourceDelta[] deltas, ZipOutputStream zip, Map<ZipEntry, String> deleteEntries, String deletePrefix,
        String deltaPrefix, boolean adjustGMTOffset ) throws IOException, CoreException
    {
        for( IModuleResourceDelta delta : deltas )
        {
            int deltaKind = delta.getKind();

            IResource deltaResource = (IResource) delta.getModuleResource().getAdapter( IResource.class );

            IProject deltaProject = deltaResource.getProject();

            // IDE-110 IDE-648
            IVirtualFolder webappRoot = CoreUtil.getDocroot( deltaProject );

            IPath deltaPath = null;

            if( webappRoot != null )
            {
                for( IContainer container : webappRoot.getUnderlyingFolders() )
                {
                    if( container != null && container.exists() )
                    {
                        final IPath deltaFullPath = deltaResource.getFullPath();
                        final IPath containerFullPath = container.getFullPath();
                        deltaPath = new Path( deltaPrefix + deltaFullPath.makeRelativeTo( containerFullPath ) );

                        if( deltaPath != null && deltaPath.segmentCount() > 0 )
                        {
                            break;
                        }
                    }
                }
            }

            if( deltaKind == IModuleResourceDelta.ADDED || deltaKind == IModuleResourceDelta.CHANGED )
            {
                addToZip( deltaPath, deltaResource, zip, adjustGMTOffset );
            }
            else if( deltaKind == IModuleResourceDelta.REMOVED )
            {
                addRemoveProps( deltaPath, deltaResource, zip, deleteEntries, deletePrefix );
            }
            else if( deltaKind == IModuleResourceDelta.NO_CHANGE )
            {
                IModuleResourceDelta[] children = delta.getAffectedChildren();
                processResourceDeltasZip( children, zip, deleteEntries, deletePrefix, deltaPrefix, adjustGMTOffset );
            }
        }
    }

    private static String removeArchive( String archive )
    {
        int index = Math.max( archive.lastIndexOf( ".war" ), archive.lastIndexOf( ".jar" ) ); //$NON-NLS-1$ //$NON-NLS-2$

        if( index >= 0 )
        {
            return archive.substring( 0, index + 5 );
        }

        return StringPool.EMPTY;
    }

    public static void terminateLaunchesForConfig( ILaunchConfigurationWorkingCopy config ) throws DebugException
    {
        ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();

        for( ILaunch launch : launches )
        {
            if( launch.getLaunchConfiguration().equals( config ) )
            {
                launch.terminate();
            }
        }
    }
}
