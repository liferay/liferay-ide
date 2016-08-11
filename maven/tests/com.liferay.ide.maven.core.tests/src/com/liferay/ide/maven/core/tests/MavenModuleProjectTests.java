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

package com.liferay.ide.maven.core.tests;

import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class MavenModuleProjectTests extends AbstractMavenProjectTestCase
{

    @Test
    public void testNewLiferayMavenModuleMVCPortletProject() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "foo" );
        op.setProjectProvider( "maven-module" );

        Status status = op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertNotNull( status );
        assertEquals( status.message(), Status.createOkStatus(), status );
    }
}
