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

package com.liferay.ide.adt.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.adt.core.model.GenerateCustomServicesOp;
import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.mobile.sdk.core.PortalAPI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class GenerateCustomServicesOpTests extends BaseTests
{

    @Test
    public void testMobileLibrariesOpAddDefault() throws Exception
    {
        final String projectName = "SampleAndroidApp";

        ADTCoreTests.deleteSampleProject( projectName );

        final String propertiesFileName = "libs/liferay-android-sdk-6.2.0.1.jar.properties";

        final IProject sampleProject = ADTCoreTests.importAndroidProject( projectName, projectName + ".zip" );

        assertNotNull( sampleProject );

        assertTrue( sampleProject.exists() );

        final GenerateCustomServicesOp op = GenerateCustomServicesOp.TYPE.instantiate();

        op.setProjectName( projectName );

        op.getLibraries().insert().setContext( PortalAPI.NAME );

        op.execute( npm() );

        assertTrue( sampleProject.getFile( "libs/liferay-android-sdk-6.2.0.1.jar" ).exists() );

        assertTrue( sampleProject.getFile( propertiesFileName ).exists() );

        assertTrue( sampleProject.getFile( "libs/src/liferay-android-sdk-6.2.0.1-sources.jar" ).exists() );

        final String propertiesContent =
            CoreUtil.readStreamToString( sampleProject.getFile( propertiesFileName ).getContents(
                true ) );

        assertEquals(
            stripCarriageReturns( "src=src/liferay-android-sdk-6.2.0.1-sources.jar" ),
            stripCarriageReturns( propertiesContent ) );
    }

    private ProgressMonitor npm()
    {
        return ProgressMonitorBridge.create( new NullProgressMonitor() );
    }

}
