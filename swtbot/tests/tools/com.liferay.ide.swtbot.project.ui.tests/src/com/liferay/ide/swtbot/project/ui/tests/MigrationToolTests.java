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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.liferay.ide.swtbot.project.ui.tests.page.LiferayProjectFromExistSourceWizardPO;
import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TreeItemPO;
import com.liferay.ide.swtbot.ui.tests.page.ViewPO;
import com.liferay.ide.swtbot.ui.tests.util.CoreUtil;
import com.liferay.ide.swtbot.ui.tests.util.ZipUtil;

/**
 * @author Li Lu
 */
@RunWith( SWTBotJunit4ClassRunner.class )
public class MigrationToolTests extends SWTBotBase implements MigrateProjectWizard
{

    String MARKER_TYPE = "com.liferay.ide.swtbot.project.core.MigrationProblemMarker";
    private static final String BUNDLE_ID = "com.liferay.ide.swtbot.project.ui.tests";
    private static IProject project;
    
    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    @BeforeClass
    public static void unzipServerAndSdk() throws IOException
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );
        
        unzipServer();
        unzipPluginsSDK();
    }

    @After
    public void cleanup()
    {
        try
        {
            eclipse.getPackageExporerView().deleteResouceByName( project.getName(), true );
        }
        catch( Exception e )
        {
        }
    }

    public void deleteMigrationMarkers( IResource resource ) throws CoreException
    {
        IMarker[] markers = resource.findMarkers( MARKER_TYPE, false, IResource.DEPTH_ZERO );

        for( IMarker marker : markers )
        {
            marker.delete();
        }
    }

    public IMarker findMigrationMarker( IResource resource, String markerMessage, boolean fullMatch )
        throws CoreException
    {
        IMarker[] markers = resource.findMarkers( MARKER_TYPE, false, IResource.DEPTH_INFINITE );

        for( IMarker marker : markers )
        {
            if( fullMatch && marker.getAttribute( IMarker.MESSAGE ).toString().equals( markerMessage ) )
            {
                return marker;
            }
            else if( !fullMatch && marker.getAttribute( IMarker.MESSAGE ).toString().matches( markerMessage ) )
            {
                return marker;
            }
        }
        return null;
    }

    @Before
    public void importProject() throws IOException
    {
        if( !( runTest() || runAllTests() ) )
            return;

        File projectZipFile = getProjectZip( BUNDLE_ID, "knowledge-base-portlet" );
        IPath copyDir = getLiferayPluginsSdkDir().append( "/portlets" );

        ZipUtil.unzip( projectZipFile, copyDir.toFile() );

        eclipse.getCreateLiferayProjectToolbar().menuClick( MENU_NEW_LIFERAY_PROJECT_EXIS_SOURCE );
        LiferayProjectFromExistSourceWizardPO _wizard = new LiferayProjectFromExistSourceWizardPO( bot );
        _wizard.importProject( copyDir.append( "knowledge-base-portlet" ).toOSString() );
    }

    @Test
    public void testMigrateProjectHandlerCancelOnMenu() throws Exception
    {
        if( !( runTest() || runAllTests() ) )
            return;

        project = CoreUtil.getProject( "knowledge-base-portlet" );

        sleep( 2000 );
        IMarker marker = findMigrationMarker( project, ".*", false );
        assertNull( marker );

        TreeItemPO projectTreeItem = eclipse.getProjectTree().getTreeItem( "knowledge-base-portlet" );

        projectTreeItem.doAction( MENU_LIFERAY, MENU_FIND_LIFERAY7_BREAKING_API_CHANGES );

        DialogPO migrateDialog = new DialogPO( bot, TITLE_FINDING_MIGRATION_PROBLEMS );

        migrateDialog.waitForPageToOpen();
        assertTrue( migrateDialog.isOpen() );

        sleep( 120000 );
        ViewPO view = new ViewPO( bot, TITLE_LIFERAY7_MIGRATION_PROBLEMS );
        assertTrue( view.isActive() );

        migrateDialog.closeIfOpen();
        marker = findMigrationMarker( project, ".*", false );
        assertNotNull( marker );
    }
}
