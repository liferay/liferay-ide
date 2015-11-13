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
package com.liferay.ide.project.core.upgrade.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.upgrade.PortalSettings;
import com.liferay.ide.project.core.util.UpgradeAssistantSettingsUtil;

/**
 * @author Lovett Li
 */
public class UpgradeAssistantSettingsUtilTests
{

    @Test
    public void writeTest() throws Exception
    {
        boolean result =
            UpgradeAssistantSettingsUtil.JavaObjectToJSONFile(
                ProjectCore.getDefault().getStateLocation().toOSString(), getPortalSettingsObjects() );

        assertEquals( true, result );
    }

    @Test
    public void readTest() throws Exception
    {

        PortalSettings result =
            UpgradeAssistantSettingsUtil.JSONFileToJavaObject(
                ProjectCore.getDefault().getStateLocation().toOSString(), PortalSettings.class );

        assertNotNull( result );

        boolean found = false;

        if( result.getLiferayPortalLocation().equals( "Previous Location 6.2" ) ||
            result.getPreviousName().equals( "Previous Portal Name 6.2" ) ||
            result.getNewliferayPortalLocation().equals( "New Portal Name 7.0" ) ||
            result.getNewName().equals( "New Location 7.0" ) )
        {
            found = true;
        }

        if( !found )
        {
            fail();
        }
    }

    protected PortalSettings getPortalSettingsObjects()
    {
        return new PortalSettings(
            "Previous Portal Name 6.2", "Previous Location 6.2", "New Portal Name 7.0", "New Location 7.0" );
    }
}
