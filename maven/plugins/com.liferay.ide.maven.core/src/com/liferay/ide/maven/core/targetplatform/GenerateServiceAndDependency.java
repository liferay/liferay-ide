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

package com.liferay.ide.maven.core.targetplatform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.list.SetUniqueList;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IServer;
import org.osgi.framework.Version;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.maven.core.LiferayMavenCore;
import com.liferay.ide.maven.core.aether.AetherUtil;
import com.liferay.ide.server.core.portal.BundleSupervisor;
import com.liferay.ide.server.core.portal.GogoTelnetClient;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.core.portal.PortalServerBehavior;

import aQute.remote.api.Agent;

/**
 * @author Lovett Li
 * @author Simon Jiang
 * @author Terry Jia
 */
public class GenerateServiceAndDependency
{

    private static final String[] _portalImplExpPackage = new String[] {
        "com.liferay.portal.bean",
        "com.liferay.portal.cache.thread.local",
        "com.liferay.portal.cluster",
        "com.liferay.portal.dao.jdbc.aop",
        "com.liferay.portal.dao.orm.hibernate",
        "com.liferay.portal.deploy.hot",
        "com.liferay.portal.events",
        "com.liferay.portal.freemarker",
        "com.liferay.portal.increment",
        "com.liferay.portal.messaging.async",
        "com.liferay.portal.monitoring.statistics.service",
        "com.liferay.portal.plugin",
        "com.liferay.portal.resiliency.service",
        "com.liferay.portal.search",
        "com.liferay.portal.security.access.control",
        "com.liferay.portal.security.auth",
        "com.liferay.portal.security.lang",
        "com.liferay.portal.service.http",
        "com.liferay.portal.service.permission",
        "com.liferay.portal.service.persistence.constants",
        "com.liferay.portal.servlet",
        "com.liferay.portal.servlet.filters.authverifier",
        "com.liferay.portal.spring.aop",
        "com.liferay.portal.spring.bean",
        "com.liferay.portal.spring.context",
        "com.liferay.portal.spring.hibernate",
        "com.liferay.portal.spring.transaction",
        "com.liferay.portal.systemevent",
        "com.liferay.portal.template",
        "com.liferay.portal.tools",
        "com.liferay.portal.upgrade.util",
        "com.liferay.portal.upgrade.util.classname",
        "com.liferay.portal.upgrade.util.classname.dependency",
        "com.liferay.portal.upgrade.v7_0_0",
        "com.liferay.portal.upload",
        "com.liferay.portal.util",
        "com.liferay.portal.xml",
        "com.liferay.portlet",
        "com.liferay.portlet.asset",
        "com.liferay.portlet.asset.model",
        "com.liferay.portlet.asset.model.impl",
        "com.liferay.portlet.asset.service",
        "com.liferay.portlet.asset.service.permission",
        "com.liferay.portlet.asset.service.persistence",
        "com.liferay.portlet.asset.util",
        "com.liferay.portlet.documentlibrary",
        "com.liferay.portlet.documentlibrary.action",
        "com.liferay.portlet.documentlibrary.antivirus",
        "com.liferay.portlet.documentlibrary.convert",
        "com.liferay.portlet.documentlibrary.lar",
        "com.liferay.portlet.documentlibrary.model",
        "com.liferay.portlet.documentlibrary.service",
        "com.liferay.portlet.documentlibrary.service.permission",
        "com.liferay.portlet.documentlibrary.store",
        "com.liferay.portlet.documentlibrary.util",
        "com.liferay.portlet.expando.model",
        "com.liferay.portlet.expando.service",
        "com.liferay.portlet.expando.service.persistence",
        "com.liferay.portlet.expando.util",
        "com.liferay.portlet.layoutsadmin.display.context",
        "com.liferay.portlet.messageboards.model",
        "com.liferay.portlet.messageboards.service",
        "com.liferay.portlet.messageboards.service.persistence",
        "com.liferay.portlet.social.model",
        "com.liferay.portlet.social.service",
        "com.liferay.portlet.social.service.persistence",
        "com.liferay.portlet.trash",
        "com.liferay.portlet.trash.model",
        "com.liferay.portlet.trash.service",
        "com.liferay.portlet.trash.service.persistence",
        "com.liferay.portlet.trash.util",
        "com.liferay.portlet.usersadmin.search"
    };

    private final IServer _server;

    Map<String, String> bundleExportMaps = new HashMap<String, String>();

    public GenerateServiceAndDependency( IServer server )
    {
        _server = server;
    }

    private boolean checkPackageName( final String serviceName )
    {
        for( String packageName : _portalImplExpPackage )
        {
            if( serviceName.startsWith( packageName ) )
            {
                return false;
            }
        }

        return true;
    }

    public void execute() throws Exception
    {
        BundleSupervisor supervisor = null;

        if( _server == null )
        {
            return;
        }
        try
        {
            PortalServerBehavior serverBehavior =
                (PortalServerBehavior) _server.loadAdapter( PortalServerBehavior.class, null );
            supervisor = serverBehavior.createBundleSupervisor();

            if( supervisor == null )
            {
                return;
            }

            if( supervisor.getAgent() != null && supervisor.isOpen() &&
                !supervisor.getAgent().redirect( Agent.COMMAND_SESSION ) )
            {
                return;
            }

            String[] services = getServices( supervisor );
            updateServicesStaticFile( services, supervisor );

            Map<String, String[]> dynamicServiceWrapper = getDynamicServiceWrapper();
            updateServiceWrapperStaticFile( dynamicServiceWrapper );
        }
        finally
        {
            supervisor.getAgent().redirect( Agent.NONE );
            supervisor.close();
        }
    }

    private Map<String, String[]> getDynamicServiceWrapper() throws IOException
    {
        final IPath bundleLibPath = ( (PortalRuntime) _server.getRuntime().loadAdapter(
            PortalRuntime.class, null ) ).getAppServerLibGlobalDir();
        final IPath bundleServerPath =
            ( (PortalRuntime) _server.getRuntime().loadAdapter( PortalRuntime.class, null ) ).getAppServerDir();
        final Map<String, String[]> map = new LinkedHashMap<>();
        List<File> libFiles;
        File portalkernelJar = null;

        try
        {
            libFiles = FileListing.getFileListing( new File( bundleLibPath.toOSString() ) );

            for( File lib : libFiles )
            {
                if( lib.exists() && lib.getName().endsWith( "portal-kernel.jar" ) )
                {
                    portalkernelJar = lib;
                    break;
                }
            }

            libFiles = FileListing.getFileListing( new File( bundleServerPath.append( "../osgi" ).toOSString() ) );
            libFiles.add( portalkernelJar );

            if( !libFiles.isEmpty() )
            {
                for( File lib : libFiles )
                {
                    if( lib.getName().endsWith( ".lpkg" ) )
                    {
                        try(JarFile jar = new JarFile( lib ))
                        {
                            Enumeration<JarEntry> enu = jar.entries();

                            while( enu.hasMoreElements() )
                            {
                                JarInputStream jarInputStream = null;

                                try
                                {
                                    JarEntry entry = enu.nextElement();

                                    String name = entry.getName();

                                    if( name.contains( ".api-" ) )
                                    {
                                        JarEntry jarentry = jar.getJarEntry( name );
                                        InputStream inputStream = jar.getInputStream( jarentry );

                                        jarInputStream = new JarInputStream( inputStream );
                                        JarEntry nextJarEntry;

                                        while( ( nextJarEntry = jarInputStream.getNextJarEntry() ) != null )
                                        {
                                            String entryName = nextJarEntry.getName();

                                            getServiceWrapperList( map, entryName, jarInputStream );
                                        }

                                    }
                                }
                                catch( Exception e )
                                {
                                }
                                finally
                                {
                                    if( jarInputStream != null )
                                    {
                                        jarInputStream.close();
                                    }
                                }
                            }
                        }
                    }
                    else if( lib.getName().endsWith( "api.jar" ) || lib.getName().equals( "portal-kernel.jar" ) )
                    {
                        JarInputStream jarinput = null;

                        try(JarFile jar = new JarFile( lib ))
                        {
                            jarinput = new JarInputStream( new FileInputStream( lib ) );

                            Enumeration<JarEntry> enu = jar.entries();

                            while( enu.hasMoreElements() )
                            {
                                JarEntry entry = enu.nextElement();
                                String name = entry.getName();

                                getServiceWrapperList( map, name, jarinput );
                            }
                        }
                        catch( IOException e )
                        {
                        }
                        finally
                        {

                            if( jarinput != null )
                            {
                                jarinput.close();
                            }
                        }
                    }
                }
            }
        }
        catch( FileNotFoundException e )
        {
        }

        return map;
    }

    private String[] getServiceBundle( String serviceName ) throws Exception
    {
        String[] serviceBundleInfo;
        String bundleGroup = "";
        String bundleName;
        String bundleVersion;
        try(GogoTelnetClient gogoClient = new GogoTelnetClient())
        {
            String packagesResponse =
                gogoClient.send( "packages " + serviceName.substring( 0, serviceName.lastIndexOf( "." ) ), true );

            if( packagesResponse.startsWith( "No exported packages" ) )
            {
                String servicesResponse = gogoClient.send(
                    "services " + "(objectClass=" + serviceName + ")" + " | grep \"Registered by bundle:\" ", true );
                serviceBundleInfo = parseRegisteredBundle( servicesResponse );
            }
            else
            {
                serviceBundleInfo = parseSymbolicName( packagesResponse );
            }

            if( serviceBundleInfo != null )
            {
                bundleName = serviceBundleInfo[0];
                bundleVersion = serviceBundleInfo[1];

                if( bundleName.equals( "org.eclipse.osgi,system.bundle" ) )
                {
                    bundleGroup = "com.liferay.portal";
                }
                else if( bundleName.startsWith( "com.liferay" ) )
                {
                    bundleGroup = "com.liferay";
                }
                else
                {
                    int ordinalIndexOf = StringUtils.ordinalIndexOf( bundleName, ".", 3 );

                    if( ordinalIndexOf != -1 )
                    {
                        bundleGroup = bundleName.substring( 0, ordinalIndexOf );
                    }
                    else
                    {
                        ordinalIndexOf = StringUtils.ordinalIndexOf( bundleName, ".", 2 );

                        if( ordinalIndexOf != -1 )
                        {
                            bundleGroup = bundleName.substring( 0, ordinalIndexOf );
                        }
                    }
                }
                return new String[] { bundleGroup, bundleName, bundleVersion };
            }
        }
        catch( Exception e )
        {
            LiferayMavenCore.logError( e );
        }
        return null;
    }

    private String[] getServices( BundleSupervisor supervisor ) throws Exception
    {
        supervisor.getAgent().stdin( "services" );
        return parseService( supervisor.getOutInfo() );
    }

    private void getServiceWrapperList(
        final Map<String, String[]> wrapperMap, String name, JarInputStream jarInputStream )
    {
        if( name.endsWith( "ServiceWrapper.class" ) && !( name.contains( "$" ) ) )
        {
            name = name.replaceAll( "\\\\", "." ).replaceAll( "/", "." );
            name = name.substring( 0, name.lastIndexOf( "." ) );
            Attributes mainAttributes = jarInputStream.getManifest().getMainAttributes();
            String bundleName = mainAttributes.getValue( "Bundle-SymbolicName" );
            String version = mainAttributes.getValue( "Bundle-Version" );
            String group = "";

            if( bundleName.equals( "com.liferay.portal.kernel" ) )
            {
                group = "com.liferay.portal";
            }
            else
            {
                int ordinalIndexOf = StringUtils.ordinalIndexOf( bundleName, ".", 2 );

                if( ordinalIndexOf != -1 )
                {
                    group = bundleName.substring( 0, ordinalIndexOf );
                }
            }

            wrapperMap.put( name, new String[] { group, bundleName, version } );
        }
    }

    private String[] parseRegisteredBundle( String serviceName )
    {
        if( serviceName.startsWith( "false" ) )
        {
            return null;
        }

        String str = serviceName.substring( 0, serviceName.indexOf( "[" ) );
        str = str.replaceAll( "\"Registered by bundle:\"", "" ).trim();
        String[] result = str.split( "_" );

        if( result.length == 2 )
        {
            return result;
        }

        return null;
    }

    @SuppressWarnings( "unchecked" )
    private String[] parseService( String outinfo ) throws Exception
    {
        final IPath serviceFile = LiferayMavenCore.getDefault().getStateLocation().append( "services-result" );
        final ObjectMapper mapperService = new ObjectMapper();
        mapperService.writeValue( serviceFile.toFile(), outinfo );
        final Pattern pattern = Pattern.compile( "(?<=\\{)(.+?)(?=\\})" );
        final Matcher matcher = pattern.matcher( outinfo );
        final List<String> serviceList = SetUniqueList.decorate( new ArrayList<String>() );

        while( matcher.find() )
        {
            for( int i = 0; i < matcher.groupCount(); i++ )
            {
                String serviceName = matcher.group( i );

                if( serviceName.contains( "bundle.id=" ) || serviceName.contains( "service.id=" ) ||
                    serviceName.contains( "=" ) )
                {
                    continue;
                }
                else
                {
                    if( serviceName.split( "," ).length > 1 )
                    {
                        for( String bbs : serviceName.split( "," ) )
                        {
                            if( checkPackageName( bbs ) )
                            {
                                serviceList.add( bbs.trim() );
                            }
                        }
                    }
                    else
                    {
                        if( checkPackageName( serviceName ) )
                        {
                            serviceList.add( serviceName );
                        }
                    }
                }
            }
        }

        return serviceList.stream().sorted().toArray( String[]::new );
    }

    private String[] parseSymbolicName( String info )
    {
        final int symbolicIndex = info.indexOf( "bundle-symbolic-name" );
        final int versionIndex = info.indexOf( "version:Version" );
        String symbolicName;
        String version;

        if( symbolicIndex != -1 && versionIndex != -1 )
        {
            symbolicName = info.substring( symbolicIndex, info.indexOf( ";", symbolicIndex ) );
            version = info.substring( versionIndex, info.indexOf( ";", versionIndex ) );

            final Pattern p = Pattern.compile( "\"([^\"]*)\"" );
            Matcher m = p.matcher( symbolicName );

            while( m.find() )
            {
                symbolicName = m.group( 1 );
            }

            m = p.matcher( version );

            while( m.find() )
            {
                version = m.group( 1 );
            }

            return new String[] { symbolicName, version };
        }

        return null;
    }

    private void updateServicesStaticFile( final String[] servicesList, final BundleSupervisor supervisor )
        throws Exception
    {
        final List<String> services = new ArrayList<String>();
        final ObjectMapper mapperService = new ObjectMapper();
        final ObjectMapper mapperNotFindDependency = new ObjectMapper();
        final ObjectMapper mapperFindDependency = new ObjectMapper();
        final ObjectMapper mapperSystemDependency = new ObjectMapper();
        final Map<String, String[]> mapNotFindDependency = new LinkedHashMap<>();
        final Map<String, String[]> mapmFindDependency = new LinkedHashMap<>();
        final Map<String, String[]> mapSystemDependency = new LinkedHashMap<>();

        final IPath serviceFile = LiferayMavenCore.getDefault().getStateLocation().append( "services-static.json" );
        final IPath serviceNoFindDependency =
            LiferayMavenCore.getDefault().getStateLocation().append( "services-no-dependency.json" );
        final IPath serviceFindDependency =
            LiferayMavenCore.getDefault().getStateLocation().append( "services-find-dependency.json" );
        final IPath serviceSystemDependency =
            LiferayMavenCore.getDefault().getStateLocation().append( "services-system-dependency.json" );
        final Set<String> serviceBundleSet = new HashSet<String>();

        final Job job = new WorkspaceJob( "Update services static file...")
        {

            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor )
            {
                try
                {
                    for( String serviceName : servicesList )
                    {
                        try
                        {
                            if( monitor.isCanceled() )
                            {
                                return Status.CANCEL_STATUS;
                            }
                            String[] serviceBundle = getServiceBundle( serviceName );

                            if( serviceBundle != null )
                            {
                                boolean packageExported = verfifyBundleExportService( serviceName, serviceBundle );

                                if( !packageExported )
                                {
                                    continue;
                                }

                                System.out.println( "------- ServiceName is " + serviceName + ", packagesResponse is " +
                                    serviceBundle[0] + ":" + serviceBundle[1] + " --------" );

                                if( serviceBundle[1].equals( "org.eclipse.osgi,system.bundle" ) )
                                {
                                    mapSystemDependency.put( serviceName, serviceBundle );
                                    continue;
                                }
                                else
                                {
                                    String artifactValue = new String(
                                        serviceBundle[0] + ":" + serviceBundle[1] + ":" + serviceBundle[2] );

                                    if( !serviceBundleSet.contains( artifactValue ) )
                                    {
                                        try
                                        {
                                            Artifact fetchArtifact = AetherUtil.fetchArtifact( artifactValue );

                                            if( fetchArtifact != null )
                                            {
                                                mapmFindDependency.put( serviceName, serviceBundle );
                                            }
                                            else
                                            {
                                                Version initialVersion = new Version( serviceBundle[2] );

                                                Version newVersion;

                                                if( !CoreUtil.isNullOrEmpty( initialVersion.getQualifier() ) )
                                                {
                                                    newVersion = new Version(
                                                        initialVersion.getMajor(), initialVersion.getMinor(),
                                                        initialVersion.getMicro() );
                                                }
                                                else
                                                {
                                                    newVersion = new Version(
                                                        initialVersion.getMajor(), initialVersion.getMinor(), 0 );
                                                }

                                                fetchArtifact = AetherUtil.fetchArtifact(
                                                    serviceBundle[0] + ":" + serviceBundle[1] + ":" +
                                                        newVersion.toString() );

                                                if( fetchArtifact != null )
                                                {
                                                    mapmFindDependency.put( serviceName, serviceBundle );
                                                }
                                                else
                                                {
                                                    mapNotFindDependency.put( serviceName, serviceBundle );
                                                }
                                                mapNotFindDependency.put( serviceName, serviceBundle );
                                            }
                                            serviceBundleSet.add( artifactValue );
                                        }
                                        catch( Exception e )
                                        {
                                            LiferayMavenCore.log(
                                                LiferayMavenCore.createErrorStatus(
                                                    serviceName + " download dependency job failed.", e ) );
                                        }
                                    }
                                    else
                                    {
                                        mapmFindDependency.put( serviceName, serviceBundle );
                                    }
                                }
                                services.add( serviceName );
                            }
                        }
                        catch( Exception e )
                        {
                            LiferayMavenCore.log(
                                LiferayMavenCore.createErrorStatus(
                                    serviceName + " can't find its dependency jar", e ) );
                        }
                    }
                    mapperService.writeValue( serviceFile.toFile(), services );
                    mapperNotFindDependency.writeValue( serviceNoFindDependency.toFile(), mapNotFindDependency );
                    mapperFindDependency.writeValue( serviceFindDependency.toFile(), mapmFindDependency );
                    mapperSystemDependency.writeValue( serviceSystemDependency.toFile(), mapSystemDependency );
                }
                catch( Exception e )
                {
                    LiferayMavenCore.log( LiferayMavenCore.createErrorStatus( "can't save dependency log", e ) );
                }
                return Status.OK_STATUS;
            }
        };

        job.schedule();
    }

    private boolean verfifyBundleExportService( String serviceName, String[] serviceBundle )
    {
        try(GogoTelnetClient gogoClient = new GogoTelnetClient())
        {
            String bundle = null;

            if( serviceBundle[1].equals( "org.eclipse.osgi,system.bundle" ) )
            {
                bundle = "org.eclipse.osgi";
            }
            else
            {
                bundle = serviceBundle[1];
            }
            String bundleExportedResponse = bundleExportMaps.get( bundle );

            if( bundleExportedResponse == null )
            {
                bundleExportedResponse = gogoClient.send( "bundle " + bundle + " | grep exported", true );
                bundleExportMaps.put( bundle, bundleExportedResponse );
            }

            if( !bundleExportedResponse.equals( "No exported packages" ) )
            {
                String[] exportedPackages = bundleExportedResponse.split( System.getProperty( "line.separator" ) );

                for( String exportedPakcage : exportedPackages )
                {
                    String[] packageResult = exportedPakcage.trim().split( ";" );

                    if( packageResult != null )
                    {
                        String servicePackage = serviceName.substring( 0, serviceName.lastIndexOf( "." ) );

                        if( packageResult[0].equals( servicePackage ) )
                        {
                            return true;
                        }
                    }
                }
            }
        }
        catch( Exception e )
        {
            LiferayMavenCore.logError( e );
            return false;
        }
        return false;
    }

    private void updateServiceWrapperStaticFile( final Map<String, String[]> wrappers ) throws Exception
    {
        final IPath servicewrapperFile =
            LiferayMavenCore.getDefault().getStateLocation().append( "servicewrapper-static.json" );
        final IPath servicewrapperNoFindDependency =
            LiferayMavenCore.getDefault().getStateLocation().append( "servicewrapper-no-dependency.json" );
        final IPath servicewrapperFindDependency =
            LiferayMavenCore.getDefault().getStateLocation().append( "servicewrapper-find-dependency.json" );

        final Map<String, String[]> mapNotFindDependency = new LinkedHashMap<>();
        final Map<String, String[]> mapmFindDependency = new LinkedHashMap<>();

        final ObjectMapper mapper = new ObjectMapper();

        final Job job = new WorkspaceJob( "Update ServiceWrapper static file...")
        {

            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor )
            {
                try
                {
                    String[] serviceWrapperList = wrappers.keySet().toArray( new String[wrappers.keySet().size()] );

                    Iterator<String> wrapperIterator = wrappers.keySet().iterator();
                    final Set<String> serviceWrapperBundleSet = new HashSet<String>();
                    while( wrapperIterator.hasNext() )
                    {
                        String wrapperName = wrapperIterator.next();

                        String[] wrapperBundle = wrappers.get( wrapperName );

                        String artifactValue =
                            new String( wrapperBundle[0] + ":" + wrapperBundle[1] + ":" + wrapperBundle[2] );

                        if( !serviceWrapperBundleSet.contains( artifactValue ) )
                        {
                            try
                            {
                                Artifact fetchArtifact = AetherUtil.fetchArtifact( artifactValue );

                                if( fetchArtifact != null )
                                {
                                    mapmFindDependency.put( wrapperName, wrapperBundle );
                                }
                                else
                                {
                                    Version initialVersion = new Version( wrapperBundle[2] );

                                    Version newVersion;

                                    if( !CoreUtil.isNullOrEmpty( initialVersion.getQualifier() ) )
                                    {
                                        newVersion = new Version(
                                            initialVersion.getMajor(), initialVersion.getMinor(),
                                            initialVersion.getMicro() );
                                    }
                                    else
                                    {
                                        newVersion =
                                            new Version( initialVersion.getMajor(), initialVersion.getMinor(), 0 );
                                    }

                                    fetchArtifact = AetherUtil.fetchArtifact(
                                        wrapperBundle[0] + ":" + wrapperBundle[1] + ":" + newVersion.toString() );

                                    if( fetchArtifact != null )
                                    {
                                        mapmFindDependency.put( wrapperName, wrapperBundle );
                                    }
                                    else
                                    {
                                        mapNotFindDependency.put( wrapperName, wrapperBundle );
                                    }
                                }
                                serviceWrapperBundleSet.add( artifactValue );
                            }
                            catch( Exception e )
                            {
                                LiferayMavenCore.log(
                                    LiferayMavenCore.createErrorStatus(
                                        wrapperName + " download dependency job failed.", e ) );
                            }
                        }
                        else
                        {
                            mapmFindDependency.put( wrapperName, wrapperBundle );
                        }

                    }
                    mapper.writeValue( servicewrapperNoFindDependency.toFile(), mapNotFindDependency );
                    mapper.writeValue( servicewrapperFindDependency.toFile(), mapmFindDependency );
                    mapper.writeValue( servicewrapperFile.toFile(), serviceWrapperList );
                }
                catch( IOException e )
                {
                    return Status.CANCEL_STATUS;
                }

                return Status.OK_STATUS;
            }
        };

        job.schedule();

    }
}

