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

import org.eclipse.core.runtime.IStatus;

import aQute.remote.api.Agent;

import org.junit.Test;

import com.liferay.ide.project.core.modules.ServiceCommand;
import com.liferay.ide.server.util.SocketUtil;

/**
 * @author Lovett Li
 */
public class ServiceCommandTests
{

    @Test
    public void GetIntegrationPoints() throws Exception
    {
        if( canConnect() )
        {
            String[] IntegrationPoints = new ServiceCommand().execute();

            assertNotNull( IntegrationPoints );

            assertTrue( IntegrationPoints.length > 0 );
        }
    }

    @Test
    public void GetServiceBundle() throws Exception
    {
        if( canConnect() )
        {
            String[] serviceBundle =
                new ServiceCommand( "com.liferay.bookmarks.service.BookmarksEntryLocalService" ).execute();
            String[] serviceBundleNoExportPackage =
                new ServiceCommand( "com.liferay.announcements.web.messaging.CheckEntryMessageListener" ).execute();
            String[] serviceBundleNotExit = 
                new ServiceCommand( "com.liferay.test.TestServiceNotExit" ).execute();

            assertEquals( "com.liferay.bookmarks.api", serviceBundle[0] );
            assertEquals( "1.0.0", serviceBundle[1] );

            assertEquals( "com.liferay.announcements.web", serviceBundleNoExportPackage[0] );
            assertEquals( "1.0.0", serviceBundleNoExportPackage[1] );

            assertNull( serviceBundleNotExit );
        }
    }

    public boolean canConnect()
    {
        IStatus status = SocketUtil.canConnect( "localhost", String.valueOf( Agent.DEFAULT_PORT ) );

        if( status != null && status.isOK() )
        {
            return true;
        }

        return false;
    }
}
