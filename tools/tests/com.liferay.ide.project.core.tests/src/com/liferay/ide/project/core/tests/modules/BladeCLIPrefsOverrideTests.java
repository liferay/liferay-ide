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
import com.liferay.ide.project.core.modules.BladeCLI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class BladeCLIPrefsOverrideTests
{

    @BeforeClass
    public static void setupBladeCLIPrefs() throws Exception
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        prefs.put( BladeCLI.BLADE_CLI_REPO_URL, "https://liferay-test-01.ci.cloudbees.com/job/liferay-blade-cli/lastSuccessfulBuild/artifact/build/generated/p2/" );
    }

    @AfterClass
    public static void restoreBladeCLIPrefsToDefault() throws Exception
    {
        IEclipsePreferences defaults = DefaultScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        final String defaultValue = defaults.get( BladeCLI.BLADE_CLI_REPO_URL, "" );

        prefs.put( BladeCLI.BLADE_CLI_REPO_URL, defaultValue );
    }


    @Test
    public void testBladeCLIJar() throws Exception
    {
        IPath path = BladeCLI.getBladeCLIPath();

        assertTrue( path.toFile().exists() );

        assertTrue( path.toFile().getName().contains( "blade.cli_2.0.0" ) );
    }

    @Test
    public void testBladeCLITemplatesList() throws Exception
    {
        String[] projectTemplates = BladeCLI.getProjectTemplates();

        assertNotNull( projectTemplates );

        assertEquals( "activator", projectTemplates[0] );

        assertEquals( "theme", projectTemplates[projectTemplates.length - 1] );
    }

}
