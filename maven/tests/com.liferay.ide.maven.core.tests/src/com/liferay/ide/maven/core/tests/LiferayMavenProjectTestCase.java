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

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.project.core.model.ProfileLocation;
import com.liferay.ide.project.core.tests.ProjectCoreBase;

import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;



/**
 * @author Gregory Amerson
 *
 */
@SuppressWarnings( "restriction" )
public abstract class LiferayMavenProjectTestCase extends AbstractMavenProjectTestCase
{
    protected final ProjectCoreBase base = new ProjectCoreBase();

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        base.setupPluginsSDKAndRuntime();
    }

    protected void createTestBundleProfile( NewLiferayPluginProjectOp op )
    {
        NewLiferayProfile profile = op.getNewLiferayProfiles().insert();
        profile.setLiferayVersion( base.getRuntimeVersion() );
        profile.setId( "test-bundle" );
        profile.setRuntimeName( base.getRuntimeVersion() );
        profile.setProfileLocation( ProfileLocation.projectPom );

        op.setActiveProfilesValue( "test-bundle" );
    }

}
