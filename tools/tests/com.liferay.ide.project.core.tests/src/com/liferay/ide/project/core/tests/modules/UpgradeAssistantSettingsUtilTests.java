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
import static org.junit.Assert.assertTrue;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.upgrade.Liferay7UpgradeAssistantSettings;
import com.liferay.ide.project.core.upgrade.PortalSettings;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lovett Li
 */
public class UpgradeAssistantSettingsUtilTests
{
    private static final IPath stateLocation = ProjectCore.getDefault().getStateLocation();

    @Before
    public void deleteJsonfiles()
    {
        File jsonFile =
            stateLocation.append( "Liferay7UpgradeAssistantSettings.json" ).toFile();

        if( jsonFile.exists() )
        {
            assertTrue( jsonFile.delete() );
        }
    }

    @Test
    public void writeTest() throws Exception
    {
        UpgradeAssistantSettingsUtil.setObjectToStore(
            Liferay7UpgradeAssistantSettings.class, createLiferay7UpgradeAssistantSettingsObject() );

        assertTrue( stateLocation.append( "Liferay7UpgradeAssistantSettings.json" ).toFile().exists() );
    }

    @Test
    public void readTest() throws Exception
    {
        writeTest();

        Liferay7UpgradeAssistantSettings settings =
            UpgradeAssistantSettingsUtil.getObjectFromStore( Liferay7UpgradeAssistantSettings.class );

        assertNotNull( settings );

        PortalSettings portalSettings = settings.getPortalSettings();

        assertNotNull( portalSettings );

        assertEquals( "Previous Location 6.2", portalSettings.getPreviousLiferayPortalLocation() );
        assertEquals( "New Portal Location 7.0", portalSettings.getNewLiferayPortalLocation() );
        assertEquals( "New Portal Name 7.0", portalSettings.getNewName() );
    }

    protected Liferay7UpgradeAssistantSettings createLiferay7UpgradeAssistantSettingsObject()
    {
        PortalSettings portalSettings =
            new PortalSettings( "Previous Location 6.2", "New Portal Name 7.0", "New Portal Location 7.0" );

        Liferay7UpgradeAssistantSettings settings = new Liferay7UpgradeAssistantSettings();

        settings.setPortalSettings( portalSettings );

        return settings;
    }
}
