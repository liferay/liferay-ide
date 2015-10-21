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

package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NamedItem;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.upgrade.UpgradeLiferayProjectsOp;
import com.liferay.ide.project.core.upgrade.UpgradeLiferayProjectsOpMethods;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Simon Jiang
 */
public class UpgradeLiferayProjectsAlloyUIOpTests extends ProjectCoreBase
{
    @AfterClass
    public static void removePluginsSDK() throws Exception
    {
        deleteAllWorkspaceProjects();
    }

    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.1.1" );
    }


    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-6.1.1-ce-ga2-20120731132656558.zip" );
    }

    @Override
    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-6.1.1/";
    }

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.1.1-ce-ga2/tomcat-7.0.27" );
    }

    @Override
    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.1.1-ce-ga2-20120731132656558.zip" );
    }

    @Override
    protected String getRuntimeId()
    {
        return "com.liferay.ide.eclipse.server.tomcat.runtime.70";
    }

    @Override
    public String getRuntimeVersion()
    {
        return "6.1.1";
    }


    @Before
    public void removeAllVersionRuntime()  throws Exception
    {
        removeAllRuntimes();
        setupPluginsSDKAndRuntime();
    }

    @Test
    @Ignore
    public void testExecAlloyUpgradeTool() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp projectOp = newProjectOp( "exec-alloy-upgrade-tool" );
        projectOp.setPluginType( PluginType.portlet );
        projectOp.setIncludeSampleCode( true );

        final IProject project = createAntProject( projectOp );

        final UpgradeLiferayProjectsOp op = UpgradeLiferayProjectsOp.TYPE.instantiate();

        final IFolder webappRoot = LiferayCore.create( IWebProject.class, project ).getDefaultDocrootFolder();

        assertNotNull( webappRoot );

        final IFile mainCss = webappRoot.getFile( "css/main.css" );

        assertEquals( true, mainCss.exists() );

        mainCss.setContents(
            new ByteArrayInputStream( ".aui-field-select{}".getBytes() ), true, false, new NullProgressMonitor() );

        List<String> actionString = new ArrayList<String>();
        List<String> projectString = new ArrayList<String>();

        NamedItem upgradeRuntimAction = op.getSelectedActions().insert();
        upgradeRuntimAction.setName( "AlloyUIExecute" );
        actionString.add( upgradeRuntimAction.getName().content() );

        NamedItem upgradeProjectItem = op.getSelectedProjects().insert();
        upgradeProjectItem.setName( project.getName() );
        projectString.add( upgradeProjectItem.getName().content() );

        final NullProgressMonitor npm = new NullProgressMonitor();

        UpgradeLiferayProjectsOpMethods.execute( op, ProgressMonitorBridge.create( npm ) );

        mainCss.refreshLocal( IResource.DEPTH_ZERO, npm );

        final IFile serviceJarXml = webappRoot.getFile( "css/main.css" );

        assertEquals( true, serviceJarXml.exists() );

        String cssContent = CoreUtil.readStreamToString( mainCss.getContents() );

        assertEquals( false, cssContent.contains( "aui" ) );
    }

}
