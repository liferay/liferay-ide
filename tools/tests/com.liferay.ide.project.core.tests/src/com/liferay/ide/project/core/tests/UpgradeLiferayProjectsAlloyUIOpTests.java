/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.NamedItem;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.UpgradeLiferayProjectsOp;
import com.liferay.ide.project.core.model.UpgradeLiferayProjectsOpMethods;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Simon Jiang
 */

public class UpgradeLiferayProjectsAlloyUIOpTests extends ProjectCoreBase
{

    private IProject createServicePluginTypeAntProject(String prefixProjectName) throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( PluginType.servicebuilder.name() + "-" + prefixProjectName );
        op.setPluginType( PluginType.servicebuilder.name() );
        IProject project = createAntProject( op );

        return project;
    }

    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.1.1" );
    }


    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-6.1.1-ce-ga2-20121004092655026.zip" );
    }

    @Override
    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-6.1.1/";
    }

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.1.1-ce-ga2/tomcat-7.0.27" );
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
    protected String getRuntimeVersion()
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
    public void testExecAlloyUpgradeTool() throws Exception
    {
        UpgradeLiferayProjectsOp op = UpgradeLiferayProjectsOp.TYPE.instantiate();

        IProject project = createServicePluginTypeAntProject( "service");

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

        assertNotNull( webappRoot );

        final IVirtualFile mainCss = webappRoot.getFile( "css/main.css" );

        assertEquals( true, mainCss.exists() );

        CoreUtil.writeStreamFromString( ".aui-field-select{}", new FileOutputStream( mainCss.getUnderlyingFile().getLocation().toFile() ) );

        List<String> actionString = new ArrayList<String>();
        List<String> projectString = new ArrayList<String>();

        NamedItem upgradeRuntimAction = op.getSelectedActions().insert();
        upgradeRuntimAction.setName( "AlloyUIExecute" );
        actionString.add( upgradeRuntimAction.getName().content() );

        NamedItem upgradeProjectItem = op.getSelectedProjects().insert();
        upgradeProjectItem.setName( project.getName() );
        projectString.add( upgradeProjectItem.getName().content() );

        UpgradeLiferayProjectsOpMethods.performUpgrade( projectString, actionString, op.getRuntimeName().content(), new NullProgressMonitor() );

        mainCss.getUnderlyingFile().refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );

        final IVirtualFile serviceJarXml = webappRoot.getFile( "css/main.css" );

        assertEquals( true, serviceJarXml.exists() );

        String cssContent = CoreUtil.readStreamToString( mainCss.getUnderlyingFile().getContents() );

        assertEquals( false, cssContent.contains( "aui" ) );
    }

}
