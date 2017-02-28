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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.workspace.ImportLiferayWorkspaceOp;

import java.io.File;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.junit.Test;

/**
 * @author Andy Wu
 */
public class ImportMavenLiferayWorkspaceOpTests
{

    @Test
    public void testImportMavenLiferayWorkspaceOp() throws Exception
    {
        ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

        final URL wsZipUrl =
            Platform.getBundle( "com.liferay.ide.maven.core.tests" ).getEntry( "projects/maven-liferay-workspace.zip" );

        final File wsZipFile = new File( FileLocator.toFileURL( wsZipUrl ).getFile() );

        File eclipseWorkspaceLocation = CoreUtil.getWorkspaceRoot().getLocation().toFile();

        ZipUtil.unzip( wsZipFile, eclipseWorkspaceLocation );

        File wsFolder = new File( eclipseWorkspaceLocation, "maven-liferay-workspace" );

        op.setWorkspaceLocation( wsFolder.getAbsolutePath() );

        op.execute( new ProgressMonitor() );

        IProject project = CoreUtil.getProject( "maven-liferay-workspace" );
        assertTrue( project.exists() );

        project = CoreUtil.getProject( "maven-liferay-workspace-modules" );
        assertTrue( project.exists() );

        project = CoreUtil.getProject( "maven-liferay-workspace-themes" );
        assertTrue( project.exists() );

        project = CoreUtil.getProject( "maven-liferay-workspace-wars" );
        assertTrue( project.exists() );

        op = ImportLiferayWorkspaceOp.TYPE.instantiate();

        assertEquals( LiferayWorkspaceUtil.hasLiferayWorkspaceMsg, op.validation().message() );

        project = CoreUtil.getProject( "maven-liferay-workspace" );

        project.delete( true, true, new NullProgressMonitor() );
    }
}
