/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.PluginsSDKProjectRuntimeValidator;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.PluginProjectSDKNotSetResolution;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class LiferayPluginProjectSDKValidator620Tests extends NewLiferayPluginProjectOpBase
{

    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.2.0" );
    }

    @Override
    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-6.2.0/";
        
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-6.2.0-ce-rc5-with-ivy-cache.zip" );
    }

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2.0-ce-rc5/tomcat-7.0.40" );
    }

    @Override
    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.2.0-ce-rc5-20131017114004875.zip" );
    }

    @Override
    protected String getRuntimeId()
    {
        return "com.liferay.ide.eclipse.server.tomcat.runtime.70";
    }

    @Override
    protected String getRuntimeVersion()
    {
        return "6.2.0";
    }

    @Override
    protected String getServiceXmlDoctype()
    {
        return "service-builder PUBLIC \"-//Liferay//DTD Service Builder 6.2.0//EN\" \"http://www.liferay.com/dtd/liferay-service-builder_6_2_0.dtd";
    }

    @Test
    public void testSDKProjectsValidator() throws Exception
    {
        final String projectName = "Test2";
        final NewLiferayPluginProjectOp op = newProjectOp();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.portlet );
        IProject portletProject = createAntProject( op );

        PluginsSDKProjectRuntimeValidator validator = new PluginsSDKProjectRuntimeValidator();
        validator.validate( ProjectUtil.getFacetedProject( portletProject ) );
        IMarker sdkMarker =
            getProjectMarkers(
                portletProject, LiferayProjectCore.LIFERAY_PROJECT_MARKR_TYPE,
                PluginsSDKProjectRuntimeValidator.ID_PLUGIN_SDK_NOT_SET );
        assertNull( sdkMarker );

        String sdkName = SDKUtil.getSDK( portletProject ).getName();
        IProjectDescription oldDescription = portletProject.getDescription();
        ProjectDescription newDescripton = new ProjectDescription();
        newDescripton.setName( oldDescription.getName() );
        newDescripton.setLocation( new Path( ResourcesPlugin.getWorkspace().getRoot().getLocation().removeLastSegments(
            1 ) +
            "\\" + projectName ) );
        newDescripton.setBuildSpec( oldDescription.getBuildSpec() );
        newDescripton.setNatureIds( oldDescription.getNatureIds() );
        portletProject.move( newDescripton, true, new NullProgressMonitor() );
        portletProject.open( IResource.FORCE, new NullProgressMonitor() );

        validator.validate( ProjectUtil.getFacetedProject( portletProject ) );
        IMarker newSdkMarker =
            getProjectMarkers(
                portletProject, LiferayProjectCore.LIFERAY_PROJECT_MARKR_TYPE,
                PluginsSDKProjectRuntimeValidator.ID_PLUGIN_SDK_NOT_SET );
        assertNotNull( newSdkMarker );

        PluginProjectSDKNotSetResolution sdkNotSetResolution = new PluginProjectSDKNotSetResolution();
        sdkNotSetResolution.saveSDKSetting( portletProject, sdkName );

        validator.validate( ProjectUtil.getFacetedProject( portletProject ) );
        IMarker resolutionSdkMarker =
            getProjectMarkers(
                portletProject, LiferayProjectCore.LIFERAY_PROJECT_MARKR_TYPE,
                PluginsSDKProjectRuntimeValidator.ID_PLUGIN_SDK_NOT_SET );
        assertNull( resolutionSdkMarker );

        portletProject.delete( true, new NullProgressMonitor() );

    }

    private IMarker getProjectMarkers( IProject proj, String markerType, String markerSourceId ) 
                    throws CoreException
    {
        if( proj.isOpen() )
        {
            IMarker[] markers = proj.findMarkers( markerType, true, IResource.DEPTH_INFINITE );

            for( IMarker marker : markers )
            {
                if( markerSourceId.equals( marker.getAttribute( IMarker.SOURCE_ID ) ) )
                {
                    return marker;
                }

            }
        }

        return null;
    }

}
