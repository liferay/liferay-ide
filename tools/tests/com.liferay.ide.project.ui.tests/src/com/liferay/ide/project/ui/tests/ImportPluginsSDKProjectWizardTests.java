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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.junit.AfterClass;
import org.junit.Test;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.model.SDKProjectImportOp;
import com.liferay.ide.sdk.core.SDKUtil;

/**
 * @author Li Lu
 */

public class ImportPluginsSDKProjectWizardTests extends ProjectUITestBase
{

    private static final String BUNDLE_ID = "com.liferay.ide.project.ui.tests";

    static private IProject project;

    @AfterClass
    public static void cleanUp() throws Exception
    {
        deleteAllWorkspaceProjects();
    }

    protected void deleteSDK() throws CoreException
    {
        IProject[] projects = CoreUtil.getWorkspaceRoot().getProjects();
        for( IProject sdkProject : projects )
        {
            if( sdkProject.getName().startsWith( "liferay-plugins-sdk" ) )
                sdkProject.delete( false, false, null );
        }
    }

    protected String getBundleId()
    {
        return BUNDLE_ID;
    }

    protected IProject importSDKProjectTest( String path, String projectName ) throws Exception
    {
        SDKProjectImportOp op = SDKProjectImportOp.TYPE.instantiate();

        project = getProject( path, projectName );

        String location = project.getRawLocation().toOSString();

        project.delete( false, false, null );

        op.setLocation( location );
        assertTrue( path.startsWith( op.getPluginType().content() ) );
        assertEquals( "6.2.0", op.getSdkVersion().content() );

        op.execute( new ProgressMonitor() );

        assertTrue( project.exists() );

        return project;
    }

    @Test
    public void testContentDefaultValues() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        SDKProjectImportOp op = SDKProjectImportOp.TYPE.instantiate();
        assertEquals( null, op.getLocation().content() );
        assertEquals( null, op.getPluginType().content() );
        assertEquals( null, op.getSdkVersion().content() );
    }

    @Test
    public void testLocation() throws Exception
    {
        if( shouldSkipBundleTests() )return;
        
        SDKProjectImportOp op = SDKProjectImportOp.TYPE.instantiate();

        op.setLocation( "AAA" );
        assertEquals( '"' + "AAA" + '"' + " is not an absolute path.", op.validation().message() );

        op.setLocation( "C:/" );
        assertEquals( "Invalid project location", op.validation().message() );

        op.setLocation( "C:/AAA" );
        assertEquals( "Project isn't exist at \"" + "C:\\AAA" + "\"", op.validation().message() );

        project = importSDKProjectTest( "portlets", "Import-223-portlet" );

        IPath dir = project.getRawLocation();
        String location = dir.toOSString();

        final File projectZipFile = getProjectZip( BUNDLE_ID, "Import-223-portlet" );

        ZipUtil.unzip( projectZipFile, dir.removeLastSegments( 2 ).toFile() );

        String projectCopyDir = dir.removeLastSegments( 2 ).append( project.getName() ).toOSString();

        op.setLocation( location );
        assertEquals( "Project name already exists.", op.validation().message() );

        project.close( null );
        op = SDKProjectImportOp.TYPE.instantiate();
        op.setLocation( location );
        assertEquals( "Project name already exists.", op.validation().message() );

        project.delete( false, false, null );
        op = SDKProjectImportOp.TYPE.instantiate();
        op.setLocation( location );
        assertEquals( "ok", op.validation().message() );

        deleteSDK();
        op = SDKProjectImportOp.TYPE.instantiate();
        op.setLocation( location );
        assertEquals( "ok", op.validation().message() );
        op.execute( new ProgressMonitor() );
        assertNotNull( SDKUtil.getSDK( project ) );

        project.delete( false, false, null );
        op.setLocation( projectCopyDir );
        assertTrue( op.validation().message().startsWith( "Could not determine SDK from project location" ) );
    }

    @Test
    public void testPluginType() throws Exception
    {
        if( shouldSkipBundleTests() )return;
        
        importSDKProjectTest( "hooks", "Import-223-hook" );
        importSDKProjectTest( "themes", "Import-223-theme" );
        importSDKProjectTest( "ext", "Import-223-ext" );
        importSDKProjectTest( "layouttpl", "Import-223-layouttpl" );
    }
}
