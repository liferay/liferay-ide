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

import org.junit.Test;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;

/**
 * @author Ying Xu
 */
public class NewLiferayJsfProjectWizardTests extends SwtbotBase
{

    @Test
    public void createICEFacesProject()
    {
        String projectName = "testICEFacesProject";

        wizardAction.openNewLiferayJsfProjectWizard();

        wizardAction.prepareJsfProjectGradle( projectName, ICEFACES );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createJSFStandardProject()
    {
        String projectName = "testJSFStandardProject";

        wizardAction.openNewLiferayJsfProjectWizard();

        wizardAction.prepareJsfProjectGradle( projectName, JSF_STANDARD );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createLiferayFacesAlloyProject()
    {
        String projectName = "testLiferayFacesAlloyProject";

        wizardAction.openNewLiferayJsfProjectWizard();

        wizardAction.prepareJsfProjectGradle( projectName, LIFERAY_FACES_ALLOY );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createPrimeFacesProject()
    {
        String projectName = "testPrimeFacesProject";

        wizardAction.openNewLiferayJsfProjectWizard();

        wizardAction.prepareJsfProjectGradle( projectName, PRIMEFACES );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createRichFacesProject()
    {
        String projectName = "testRichFacesProject";

        wizardAction.openNewLiferayJsfProjectWizard();

        wizardAction.prepareJsfProjectGradle( projectName, RICHFACES );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

}
