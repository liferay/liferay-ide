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

package com.liferay.ide.swtbot.project.ui.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assume;
import org.junit.BeforeClass;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.LiferayProjectFromExistSourceWizard;

import org.junit.Test;

/**
 * @author Terry Jia
 */
public class ValidationPluginProjectTests extends SwtbotBase
{

    private LiferayProjectFromExistSourceWizard pluginFromSourcewizard = new LiferayProjectFromExistSourceWizard( bot );

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    @BeforeClass
    public static void shouldRunTests()
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );
    }

    @Test
    public void testDefaults()
    {
        wizardAction.openNewLiferayPluginProjectsFromExistingSourceWizard();

        assertEquals( PLEASE_SELECT_AT_LEAST_ONE_PROJECT_TO_IMPORT, pluginFromSourcewizard.getValidationMsg() );

        assertTrue( pluginFromSourcewizard.getSdkDirectory().isEnabled() );
        assertTrue( pluginFromSourcewizard.getBrowseSdkDirectoryBtn().isEnabled() );
        assertTrue( pluginFromSourcewizard.getSdkVersion().isEnabled() );

        assertTrue( pluginFromSourcewizard.getSdkDirectory().isActive() );
        assertFalse( pluginFromSourcewizard.getBrowseSdkDirectoryBtn().isActive() );
        assertFalse( pluginFromSourcewizard.getSdkVersion().isActive() );

        assertTrue( pluginFromSourcewizard.getSelectAllBtn().isEnabled() );
        assertTrue( pluginFromSourcewizard.getDeselectAllBtn().isEnabled() );
        assertTrue( pluginFromSourcewizard.getRefreshBtn().isEnabled() );

        wizardAction.cancel();
    }

}
