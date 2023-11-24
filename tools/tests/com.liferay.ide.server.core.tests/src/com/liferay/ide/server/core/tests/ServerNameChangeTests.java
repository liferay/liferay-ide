/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved./
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

package com.liferay.ide.server.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.util.ServerUtil;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class ServerNameChangeTests extends ServerCoreBase
{
    protected IPath getLiferayTomcatUnzipRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-ce-portal-7.0-ga5-tomcat" );
    }
    
    protected IPath getLiferayTomcatRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-ce-portal-7.0-ga5-tomcat/liferay-ce-portal-7.0-ga5" );
    }

    protected IPath getLiferayTomcatRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip" );
    }

    public String getTomcatRuntimeName()
    {
        return "Liferay CE GA5 Tomcat";
    }

    protected IPath getLiferayWildflyUnzipRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-ce-portal-7.0-ga5-wildfly" );
    }
    
    protected IPath getLiferayWildflyRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-ce-portal-7.0-ga5-wildfly/liferay-ce-portal-7.0-ga5" );
    }

    protected IPath getLiferayWildflyRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-ce-portal-wildfly-7.0-ga5-20171018150113838.zip" );
    }

    protected String getLiferayWildflyZipDir()
    {
        return "liferay-ce-portal-7.0-ga5-wildfly";
    }
    
    public String getWildflyRuntimeName()
    {
        return "Liferay CE GA5 Wildfly";
    }

    protected IPath getLiferayTomcat62UnzipRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-portal-tomcat-6.2-ce-ga6" );
    }
    
    protected IPath getLiferayTomcat62RuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-portal-tomcat-6.2-ce-ga6/liferay-portal-6.2-ce-ga6" );
    }

    protected IPath getLiferayTomcat62RuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.2-ce-ga6-20160112152609836.zip" );
    }

    public String getTomcat62RuntimeName()
    {
        return "Liferay CE Tomcat62";
    }

    protected IPath getLiferayTomcat62DumyUnzipRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-portal-tomcat-6.2-ce-ga6-dumy" );
    }

    public String getTomcat62DumyRuntimeName()
    {
        return "Liferay CE Tomcat62 Dumy";
    }
    
    public void extractLiferayRuntime( IPath zip , IPath dir ) throws Exception
    {
        final File liferayRuntimeDirFile = dir.toFile();

        if( !liferayRuntimeDirFile.exists() )
        {
            final File liferayRuntimeZipFile = zip.toFile();

            assertEquals(
                "Expected file to exist: " + liferayRuntimeZipFile.getAbsolutePath(), true,
                liferayRuntimeZipFile.exists() );

            ZipUtil.unzip( liferayRuntimeZipFile, dir.toFile() );
        }

        assertEquals( true, liferayRuntimeDirFile.exists() );
    }

    protected String getRuntimeId()
    {
        return "com.liferay.ide.server.portal.runtime";
    }

    protected String get62RuntimeId()
    {
        return "com.liferay.ide.server.62.tomcat.runtime.70";
    }

    
    private String setupRuntime( IPath runtimeZipPath, IPath runitmeDirPath, String runtimeName, IPath runtimUnzipDir, String runtimeId ) throws Exception
    {
        assertEquals( "Expected liferayBundlesPath to exist: " + runtimeZipPath.toOSString(), true,
            runtimeZipPath.toFile().exists() );

        extractLiferayRuntime( runtimeZipPath, runtimUnzipDir );

        final NullProgressMonitor npm = new NullProgressMonitor();

        IRuntime findRuntime = ServerCore.findRuntime( runtimeName );

        if( findRuntime == null )
        {
            final IRuntimeWorkingCopy runtimeWC =
                ServerCore.findRuntimeType( runtimeId ).createRuntime( runtimeName, npm );

            runtimeWC.setName( runtimeName );
            runtimeWC.setLocation( runitmeDirPath );

            findRuntime = runtimeWC.save( true, npm );
        }

        assertNotNull( findRuntime );
        final PortalRuntime liferayRuntime =
            (PortalRuntime) ServerCore.findRuntime( runtimeName ).loadAdapter( PortalRuntime.class, npm );

        assertNotNull( liferayRuntime );
        
        return findRuntime.getId();
    }
    
   
    @Before
    public void setupRuntime() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        setupRuntime( getLiferayTomcatRuntimeZip(), getLiferayTomcatRuntimeDir(), getTomcatRuntimeName(), getLiferayTomcatUnzipRuntimeDir(),getRuntimeId() );
        setupRuntime( getLiferayWildflyRuntimeZip(), getLiferayWildflyRuntimeDir(), getWildflyRuntimeName(), getLiferayWildflyUnzipRuntimeDir(), getRuntimeId() );
    }

    @Test
    public void testPortalServiceDelegateName() throws Exception 
    {
        if( shouldSkipBundleTests() ) return;

        IServerType portalServerType = ServerCore.findServerType( PortalServer.ID );

        assertNotNull( portalServerType );

        IProgressMonitor monitor = new NullProgressMonitor();

        IServerWorkingCopy newServer = portalServerType.createServer( null, null, monitor );

        assertNotNull( newServer );

        newServer.setRuntime( ServerUtil.getRuntime( getTomcatRuntimeName() ) );
        ((ServerWorkingCopy)newServer).setDefaults(null);
        assertEquals( "Liferay CE GA5 Tomcat at localhost", newServer.getName() );

        newServer.setRuntime( ServerUtil.getRuntime( getWildflyRuntimeName() ) );
        ((ServerWorkingCopy)newServer).setDefaults(null);
        assertEquals( "Liferay CE GA5 Wildfly at localhost", newServer.getName() );
    }
}