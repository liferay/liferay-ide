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

import static org.junit.Assert.assertTrue;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ying Xu
 */
public class NewLiferayMavenJSFProjectWizardTests extends SwtbotBase
{

    @Test
    public void createMavenICEFacesProject()
    {
        final String projectName = "testMavenICEFacesProject";

        wizardAction.openNewLiferayJsfProjectWizard();
        wizardAction.prepareJsfProjectMaven( projectName, ICEFACES );
        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createMavenJSFStandardProject()
    {
        final String projectName = "testMavenJSFStandardProject";

        wizardAction.openNewLiferayJsfProjectWizard();
        wizardAction.prepareJsfProjectMaven( projectName, JSF_STANDARD );
        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createMavenLiferayFacesAlloyProject()
    {
        final String projectName = "testMavenLiferayFacesAlloyProject";

        wizardAction.openNewLiferayJsfProjectWizard();
        wizardAction.prepareJsfProjectMaven( projectName, LIFERAY_FACES_ALLOY );
        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createMavenPrimeFacesProject()
    {
        final String projectName = "testMavenPrimeFacesProject";

        wizardAction.openNewLiferayJsfProjectWizard();
        wizardAction.prepareJsfProjectMaven( projectName, PRIMEFACES );
        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createMavenRichFacesProject()
    {
        final String projectName = "testMavenRichFacesProject";

        wizardAction.openNewLiferayJsfProjectWizard();
        wizardAction.prepareJsfProjectMaven( projectName, RICHFACES );
        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Before
    public void shouldRunTests()
    {
        Assume.assumeTrue( runTest() || runAllTests() );
    }

}
