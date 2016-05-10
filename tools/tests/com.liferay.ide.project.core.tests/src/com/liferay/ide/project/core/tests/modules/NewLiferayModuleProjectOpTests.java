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

import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import org.junit.Test;

/**
 * @author Simon Jiang
 */
public class NewLiferayModuleProjectOpTests
{

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceDashes() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my-test-project" );

        op.setProjectTemplateName( "Portlet" );

        assertEquals( "MyTestProjectPortlet", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceDots() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setProjectTemplateName( "Portlet" );

        assertEquals( "MyTestProjectPortlet", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceIsListeningToProjectName() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setProjectTemplateName( "Portlet" );

        assertEquals( "MyTestProjectPortlet", op.getComponentName().content( true ) );

        op.setProjectName( "my_abc-test" );

        assertEquals( "MyAbcTestPortlet", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceIsListeningToProjectTemplateName() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setProjectTemplateName( "Activator" );

        assertEquals( "MyTestProjectActivator", op.getComponentName().content( true ) );

        op.setProjectTemplateName( "Portlet" );

        assertEquals( "MyTestProjectPortlet", op.getComponentName().content( true ) );

        op.setProjectTemplateName( "MvcPortlet" );

        assertEquals( "MyTestProjectMvcPortlet", op.getComponentName().content( true ) );

        op.setProjectTemplateName( "Service" );

        assertEquals( "MyTestProjectService", op.getComponentName().content( true ) );

        op.setProjectTemplateName( "ServiceWrapper" );

        assertEquals( "MyTestProjectServiceWrapper", op.getComponentName().content( true ) );

        op.setProjectTemplateName( "ServiceBuilder" );

        assertEquals( "MyTestProjectServiceBuilder", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceUnderscores() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my_test_project" );

        op.setProjectTemplateName( "Portlet" );

        assertEquals( "MyTestProjectPortlet", op.getComponentName().content( true ) );
    }
}
