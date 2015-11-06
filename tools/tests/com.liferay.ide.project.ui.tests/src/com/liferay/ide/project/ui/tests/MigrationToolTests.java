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

package com.liferay.ide.project.ui.tests;

import static org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory.withTitle;
import static org.eclipse.swtbot.eclipse.finder.waits.Conditions.waitForView;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.ui.IViewReference;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.liferay.blade.api.MigrationConstants;

/**
 * @author Li Lu
 */
@RunWith( SWTBotJunit4ClassRunner.class )
public class MigrationToolTests extends SWTBotTestBase
{

    private static IProject project;

    @AfterClass
    public static void cleanWS() throws Exception
    {
        deleteAllWorkspaceProjects();
    }

    @After
    public void cleanup() throws CoreException
    {
        deleteMigrationMarkers( project );
    }

    public void deleteMigrationMarkers( IResource resource ) throws CoreException
    {
        IMarker[] markers = resource.findMarkers( MigrationConstants.MARKER_TYPE, false, IResource.DEPTH_ZERO );

        for( IMarker marker : markers )
        {
            marker.delete();
        }
    }

    public IMarker findMigrationMarker( IResource resource, String markerMessage, boolean fullMatch )
        throws CoreException
    {

        IMarker[] markers = resource.findMarkers( MigrationConstants.MARKER_TYPE, false, IResource.DEPTH_ZERO );

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

    protected String getBundleId()
    {
        return BUNDLE_ID;
    }

    @Test
    public void testMigrateProjectHandlerCancelOnMenu() throws Exception
    {
        if(shouldSkipBundleTests())return;

        project = getProject( "portlets", "knowledge-base-portlet" );

        IMarker marker = findMigrationMarker( project, ".*", false );
        assertNull( marker );

        bot.viewByTitle( "Package Explorer" ).show();

        bot.sleep( 2000 );

        bot.tree().getTreeItem( "knowledge-base-portlet" ).select();

        bot.menu( "Migrate Liferay project to 7.0..." ).click();

        assertTrue( bot.shell( "Finding migration problems..." ).isOpen() );

        bot.button( "Cancel" ).click();

        waitForBuildAndValidation();

        Matcher<IViewReference> withTitle = withTitle( "Liferay Plugin Migration" );
        ICondition condition = waitForView( withTitle );
        bot.waitUntil( condition, 5000 );

        marker = findMigrationMarker( project, ".*", false );
        assertNotNull( marker );
    }

}