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
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayJsfProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayModuleInfoWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayModuleWizard;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.junit.Test;

/**
 * @author Ying Xu
 * @author Sunny Shi
 */
public class ValidationModuleProjectTests extends SwtbotBase
{

    NewLiferayJsfProjectWizard newJSFProject = new NewLiferayJsfProjectWizard( bot );

    NewLiferayModuleWizard createModuleProjectWizard = new NewLiferayModuleWizard( bot );

    NewLiferayModuleInfoWizard createModuleProjectSecondPageWizard = new NewLiferayModuleInfoWizard( bot );

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
    public void validationTheSecondPage()
    {
        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModule( "test" );

        wizardAction.next();

        wizardAction.prepareLiferayModuleInfo( "@@", StringPool.BLANK );

        assertEquals( INVALID_CLASS_NAME, createModuleProjectSecondPageWizard.getValidationMsg() );

        wizardAction.prepareLiferayModuleInfo( StringPool.BLANK, "!!" );

        assertEquals( INVALID_CLASS_NAME, createModuleProjectSecondPageWizard.getValidationMsg() );

        wizardAction.prepareLiferayModuleInfo( "testClassName", "testPackageName" );

        createModuleProjectSecondPageWizard.getAddPropertyKeyBtn().click();
        sleep();
        createModuleProjectSecondPageWizard.getProperties().setFocus();
        assertEquals( NAME_MUST_BE_SPECIFIED, createModuleProjectSecondPageWizard.getValidationMsg() );
        assertTrue( createModuleProjectSecondPageWizard.getDeleteBtn().isEnabled() );
        createModuleProjectSecondPageWizard.getDeleteBtn().click();

        createModuleProjectSecondPageWizard.getAddPropertyKeyBtn().click();
        sleep();
        createModuleProjectSecondPageWizard.getProperties().setText( 2, "a" );
        createModuleProjectSecondPageWizard.getProperties().setFocus();
        sleep();
        assertEquals( VALUE_MUST_BE_SPECIFIED, createModuleProjectSecondPageWizard.getValidationMsg() );
        sleep( 2000 );
        createModuleProjectSecondPageWizard.getProperties().doubleClick( 0, 1 );
        sleep();
        createModuleProjectSecondPageWizard.getProperties().setText( 2, "b" );

        wizardAction.cancel();
    }

    @Test
    public void validateProjectName()
    {
        assertEquals( PLEASE_ENTER_A_PROJECT_NAME, newJSFProject.getValidationMsg() );
        assertFalse( newJSFProject.finishBtn().isEnabled() );

        newJSFProject.getProjectName().setText( "." );
        sleep();
        assertEquals( " '.'" + IS_AN_INVALID_NAME_ON_PLATFORM, newJSFProject.getValidationMsg() );
        assertFalse( newJSFProject.finishBtn().isEnabled() );

        newJSFProject.getProjectName().setText( "/" );
        sleep();
        assertEquals( " /" + IS_AN_INVALID_CHARACTER_IN_RESOURCE_NAME + "'/'.", newJSFProject.getValidationMsg() );
        assertFalse( newJSFProject.finishBtn().isEnabled() );

        newJSFProject.getProjectName().setText( "$" );
        sleep();
        assertEquals( THE_PROJECT_NAME_IS_INVALID, newJSFProject.getValidationMsg() );
        assertFalse( newJSFProject.finishBtn().isEnabled() );

        newJSFProject.getProjectName().setText( StringPool.BLANK );
        sleep();
        assertEquals( PROJECT_NAME_MUST_BE_SPECIFIED, newJSFProject.getValidationMsg() );
        assertFalse( newJSFProject.finishBtn().isEnabled() );

        newJSFProject.getProjectName().setText( "a" );
        sleep();
        assertEquals( ENTER_A_NAME_AND_CHOOSE_TEMPLATE_FOR_NEW_JSF_PROJECT, newJSFProject.getValidationMsg() );
        assertTrue( newJSFProject.finishBtn().isEnabled() );

        newJSFProject.cancel();
    }
}
