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

import org.junit.Test;

import com.liferay.ide.project.core.modules.ServiceCommand;

/**
 * @author Lovett Li
 */
public class ServiceCommandStaticTests
{

    @Test
    public void testGetStaticService() throws Exception
    {
        String[] IntegrationPoints = new ServiceCommand( null ).execute();

        assertNotNull( IntegrationPoints );

        assertTrue( IntegrationPoints.length > 0 );
    }

    @Test
    public void testGetStaticServiceBundle() throws Exception
    {
        String[] serviceBundle =
            new ServiceCommand( null, "com.liferay.bookmarks.service.BookmarksEntryLocalService" ).execute();
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
