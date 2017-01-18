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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.maven.core.LiferayMavenCore;
import com.liferay.ide.maven.core.LiferayMavenLegacyProjectUpdater;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;

/**
 * @author Andy Wu
 */
public class LiferayMavenLegacyProjectUpdaterTests
{

    private boolean containString( IProject project, String containStr )
    {
        IFile pomFile = project.getFile( "pom.xml" );

        try(InputStream ins = pomFile.getContents())
        {
            String content = FileUtil.readContents( ins );

            if( !CoreUtil.empty( content ) )
            {
                return content.contains( containStr );
            }
        }
        catch( Exception e )
        {
        }

        return false;
    }

    private void makeSureNoLegacyElememnts( IProject project )
    {
        assertFalse( containString( project, "liferay-maven-plugin" ) );
        assertFalse( containString( project, "portal-service" ) );
        assertFalse( containString( project, "util-java" ) );
        assertFalse( containString( project, "util-bridges" ) );
        assertFalse( containString( project, "util-taglib" ) );
        assertFalse( containString( project, "util-slf4j" ) );
    }

    @Test
    public void testUpgrade() throws Exception
    {
        IProject project = CoreUtil.getProject( "testMavenProjects" );

        if( project != null && project.exists() )
        {
            project.delete( true, true, new NullProgressMonitor() );
        }

        final URL projectZipUrl =
            Platform.getBundle( "com.liferay.ide.maven.core.tests" ).getEntry( "projects/testMavenProjects.zip" );

        final File projectZipFile = new File( FileLocator.toFileURL( projectZipUrl ).getFile() );

        IPath stateLocation = LiferayMavenCore.getDefault().getStateLocation();

        File targetFolder = new File( stateLocation.toFile(), "testMavenProjects" );

        if( targetFolder.exists() )
        {
            targetFolder.delete();
        }

        ZipUtil.unzip( projectZipFile, stateLocation.toFile() );

        assertTrue( targetFolder.exists() );

        ILiferayProjectImporter importer = LiferayCore.getImporter( "maven" );

        importer.importProjects( targetFolder.getAbsolutePath(), new NullProgressMonitor() );

        LiferayMavenLegacyProjectUpdater updater = new LiferayMavenLegacyProjectUpdater();

        // portlet project
        IProject ppProject = CoreUtil.getProject( "testpp" );
        assertTrue( updater.isNeedUpgrade( ppProject ) );
        updater.upgradePomFile( ppProject, null );
        assertFalse( updater.isNeedUpgrade( ppProject ) );
        assertTrue( containString( ppProject, "com.liferay.css.builder" ) );
        makeSureNoLegacyElememnts( ppProject );

        // service builder parent project
        IProject testsbProject = CoreUtil.getProject( "testsb" );
        assertTrue( updater.isNeedUpgrade( testsbProject ) );
        updater.upgradePomFile( testsbProject, null );
        assertFalse( updater.isNeedUpgrade( testsbProject ) );
        makeSureNoLegacyElememnts( testsbProject );

        // service builder -portlet subproject
        IProject testsbPortletProject = CoreUtil.getProject( "testsb-portlet" );
        assertTrue( updater.isNeedUpgrade( testsbPortletProject ) );
        updater.upgradePomFile( testsbPortletProject, null );
        assertFalse( updater.isNeedUpgrade( testsbPortletProject ) );
        assertTrue( containString( testsbPortletProject, "com.liferay.css.builder" ) );
        assertTrue( containString( testsbPortletProject, "com.liferay.portal.tools.service.builder" ) );
        assertTrue( containString( testsbPortletProject, "biz.aQute.bnd.annotation" ) );
        makeSureNoLegacyElememnts( testsbPortletProject );

        //// service builder -service subproject
        IProject testsbPortletServiceProject = CoreUtil.getProject( "testsb-portlet-service" );
        assertTrue( updater.isNeedUpgrade( testsbPortletServiceProject ) );
        updater.upgradePomFile( testsbPortletServiceProject, null );
        assertFalse( updater.isNeedUpgrade( testsbPortletServiceProject ) );
        assertTrue( containString( testsbPortletServiceProject, "biz.aQute.bnd.annotation" ) );
        makeSureNoLegacyElememnts( testsbPortletServiceProject );

        // theme project
        IProject testthemeProject = CoreUtil.getProject( "testtheme" );
        assertTrue( updater.isNeedUpgrade( testthemeProject ) );
        updater.upgradePomFile( testthemeProject, null );
        assertFalse( updater.isNeedUpgrade( testthemeProject ) );
        assertTrue( containString( testthemeProject, "com.liferay.portal.tools.theme.builder.outputDir" ) );
        assertTrue( containString( testthemeProject, "project.build.sourceEncoding" ) );
        assertTrue( containString( testthemeProject, "maven-war-plugin" ) );
        assertTrue( containString( testthemeProject, "maven-dependency-plugin" ) );
        assertTrue( containString( testthemeProject, "com.liferay.css.builder" ) );
        assertTrue( containString( testthemeProject, "com.liferay.portal.tools.theme.builder" ) );
        makeSureNoLegacyElememnts( testthemeProject );
    }
}
