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

package com.liferay.ide.project.core.tests.modules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.ServiceCommand;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.core.tests.ServerCoreBase;
import com.liferay.ide.server.core.tests.ServerManagerTests;

/**
 * @author Lovett Li
 */
public class ServiceCommandDynamicTests extends ServerCoreBase
{
    private PortalRuntime portalRuntime = null;

    private ServerManagerTests serverManagerTests = new ServerManagerTests()
    {

        @Override
        protected IPath getLiferayRuntimeDir()
        {
            return ProjectCore.getDefault().getStateLocation().append( "liferay-portal-7.0-ce-b8/tomcat-8.0.30" );
        }

        @Override
        protected IPath getLiferayRuntimeZip()
        {
            return getLiferayBundlesPath().append( "liferay-portal-tomcat-7.0-ce-b8-20160223094645600.zip" );
        }

        @Override
        protected String getRuntimeId()
        {
            return "com.liferay.ide.server.portal.runtime";
        }

        @Override
        public void setupRuntime() throws Exception
        {
            if( shouldSkipServerTests() )
                return;

            extractRuntime( getLiferayRuntimeZip(), getLiferayRuntimeDir() );

            IProgressMonitor npm = new NullProgressMonitor();

            final String name = "ServicesTest";
            final IRuntimeWorkingCopy runtimeWC =
                ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( name, npm );

            assertNotNull( runtimeWC );

            runtimeWC.setName( name );
            runtimeWC.setLocation( getLiferayRuntimeDir() );
            runtime = runtimeWC.save( true, npm );

            portalRuntime = (PortalRuntime) ServerCore.findRuntime( name ).loadAdapter( PortalRuntime.class, npm );
        }

        protected void setupServer() throws Exception
        {
            final NullProgressMonitor npm = new NullProgressMonitor();

            final IServerWorkingCopy serverWC = createServerForRuntime( "7.0.0", runtime );

            ServerDelegate delegate = (ServerDelegate) serverWC.loadAdapter( ServerDelegate.class, null );
            delegate.importRuntimeConfiguration( serverWC.getRuntime(), null );
            serverWC.setAttribute( "AGENT_PORT", "29998" );
            server = serverWC.save( true, npm );

            assertNotNull( server );
        }
    };

    private void setupAgent()
    {
        final IPath modulesPath = portalRuntime.getPortalBundle().getLiferayHome().append( "osgi/modules" );
        final IPath agentInstalledPath = modulesPath.append( "biz.aQute.remote.agent.jar" );

        if( !agentInstalledPath.toFile().exists() )
        {
            try
            {
                final File file = new File(
                    FileLocator.toFileURL(
                        LiferayServerCore.getDefault().getBundle().getEntry(
                            "bundles/biz.aQute.remote.agent-3.1.0.jar" ) ).getFile() );

                FileUtil.copyFile( file, modulesPath.append( "biz.aQute.remote.agent.jar" ).toFile() );
            }
            catch( IOException e )
            {
            }
        }
    }

    @Override
    public void setupRuntime() throws Exception
    {
       serverManagerTests.setupRuntime();
    }

    @Before
    public void startServer() throws Exception
    {
        setupAgent();
        serverManagerTests.startServer();
    }

    @After
    public void stopServer() throws Exception
    {
        serverManagerTests.stopServer();
    }

    @Test
    public void testGetService() throws Exception
    {
        IServer server = getServer();

        String[] IntegrationPoints = new ServiceCommand( server ).execute();

        assertNotNull( IntegrationPoints );

        assertTrue( IntegrationPoints.length > 0 );
    }

    @Test
    public void testGetServiceBundle() throws Exception
    {
        IServer server = getServer();

        String[] serviceBundle =
            new ServiceCommand( server, "com.liferay.bookmarks.service.BookmarksEntryLocalService" ).execute();
        String[] serviceBundleNoExportPackage =
            new ServiceCommand( null, "com.liferay.announcements.web.messaging.CheckEntryMessageListener" ).execute();
        String[] serviceBundleNotExit = new ServiceCommand( null, "com.liferay.test.TestServiceNotExit" ).execute();

        assertEquals( "com.liferay.bookmarks.api", serviceBundle[0] );
        assertEquals( "1.0.0", serviceBundle[1] );

        assertEquals( "com.liferay.announcements.web", serviceBundleNoExportPackage[0] );
        assertEquals( "1.0.0", serviceBundleNoExportPackage[1] );

        assertNull( serviceBundleNotExit );
    }
}