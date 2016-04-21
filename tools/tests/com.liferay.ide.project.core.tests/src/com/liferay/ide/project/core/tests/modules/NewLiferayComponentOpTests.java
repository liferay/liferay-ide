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

import com.liferay.ide.project.core.modules.NewLiferayComponentOp;

import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class NewLiferayComponentOpTests
{

    @Test
    public void testNewLiferayComponentDefaultValueServiceDashes() throws Exception
    {
        NewLiferayComponentOp op = NewLiferayComponentOp.TYPE.instantiate();

        op.setProjectName( "my-test-project" );

        op.setComponentClassTemplateName( "PortletActionCommand" );

        assertEquals( "MyTestProjectPortletActionCommand", op.getComponentClassName().content( true ) );
    }

    @Test
    public void testNewLiferayComponentDefaultValueServiceUnderscores() throws Exception
    {
        NewLiferayComponentOp op = NewLiferayComponentOp.TYPE.instantiate();

        op.setProjectName( "my_test_project" );

        op.setComponentClassTemplateName( "PortletActionCommand" );

        assertEquals( "MyTestProjectPortletActionCommand", op.getComponentClassName().content( true ) );
    }

    @Test
    public void testNewLiferayComponentDefaultValueServiceDots() throws Exception
    {
        NewLiferayComponentOp op = NewLiferayComponentOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setComponentClassTemplateName( "PortletActionCommand" );

        assertEquals( "MyTestProjectPortletActionCommand", op.getComponentClassName().content( true ) );
    }

    @Test
    public void testNewLiferayComponentDefaultValueServiceIsListeningToProjectName() throws Exception
    {
        NewLiferayComponentOp op = NewLiferayComponentOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setComponentClassTemplateName( "PortletActionCommand" );

        assertEquals( "MyTestProjectPortletActionCommand", op.getComponentClassName().content( true ) );

        op.setProjectName( "my_abc-test" );

        assertEquals( "MyAbcTestPortletActionCommand", op.getComponentClassName().content( true ) );
    }

    @Test
    public void testNewLiferayComponentDefaultValueServiceIsListeningToComponentClassTemplateName() throws Exception
    {
        NewLiferayComponentOp op = NewLiferayComponentOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setComponentClassTemplateName( "PortletActionCommand" );

        assertEquals( "MyTestProjectPortletActionCommand", op.getComponentClassName().content( true ) );

        op.setComponentClassTemplateName( "FriendlyUrlMapper" );

        assertEquals( "MyTestProjectFriendlyUrlMapper", op.getComponentClassName().content( true ) );
    }
}
