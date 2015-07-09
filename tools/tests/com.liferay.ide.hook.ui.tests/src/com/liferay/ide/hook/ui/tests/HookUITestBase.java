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

package com.liferay.ide.hook.ui.tests;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.operation.NewEventActionClassDataModelProvider;
import com.liferay.ide.hook.core.operation.NewEventActionClassOperation;
import com.liferay.ide.hook.core.operation.NewServiceWrapperClassDataModelProvider;
import com.liferay.ide.hook.core.operation.NewServiceWrapperClassOperation;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.tests.ProjectCoreBase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValOperation;
import org.eclipse.wst.validation.internal.ValType;

/**
 * @author Li Lu
 */
@SuppressWarnings( "restriction" )
public class HookUITestBase extends ProjectCoreBase
{

    public static void buildAndValidate( IFile file ) throws Exception
    {
        ValManager valManager = ValManager.getDefault();
        valManager.validate(
            file.getProject(), file, IResourceDelta.CHANGED, ValType.Build, IncrementalProjectBuilder.FULL_BUILD,
            new ValOperation(), new NullProgressMonitor() );
    }

    public static IMarker findMarkerByMessage(
        IResource resource, String markerType, String markerMessage, boolean fullMatch ) throws Exception
    {
        resource.refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );
        final IMarker[] markers = resource.findMarkers( markerType, false, IResource.DEPTH_ZERO );

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

    public static IFile getFile( IProject project, String path, String fileName )
    {
        IFolder docroot = CoreUtil.getDefaultDocrootFolder( project );
        IFile file = docroot.getFolder( path ).getFile( fileName );
        if( file.exists() )
            return file;
        return null;
    }

    public boolean checkFileExist( IProject project, String path, String fileName )
    {
        return getFile( project, path, fileName ) == null ? false : true;
    }

    public boolean checkFileHasContent( IFile file, String matchString ) throws Exception
    {
        final String contents = CoreUtil.readStreamToString( file.getContents() );

        return contents.contains( matchString );
    }

    public boolean containPropertyDescriptor( DataModelPropertyDescriptor[] properties, String expectedValue )
    {
        boolean flag = false;
        for( DataModelPropertyDescriptor property : properties )
        {
            if( property.getPropertyDescription().equals( expectedValue ) )
            {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public IProject createProject( String projectName, PluginType pluginType, String portletFramework )
        throws Exception
    {
        IProject[] projects = CoreUtil.getWorkspaceRoot().getProjects();
        if( projects != null )
        {
            for( IProject project : projects )
            {
                if( project.exists() && project.getName().contains( projectName ) )
                {
                    return project;
                }
            }
        }

        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setPluginType( pluginType );
        op.setPortletFramework( portletFramework );
        op.setIncludeSampleCode( false );
        return createAntProject( op );
    }

    public void mockCreateNewEventClass( IDataModel model, String javaPackage, String className ) throws Exception
    {
        IDataModel dataModel =
            DataModelFactory.createDataModel( new NewEventActionClassDataModelProvider(
                model, className, "com.liferay.portal.kernel.events.Action" ) );
        dataModel.setProperty( INewJavaClassDataModelProperties.JAVA_PACKAGE, javaPackage );
        NewEventActionClassOperation operation = new NewEventActionClassOperation( dataModel );
        operation.execute( null, null );
    }

    public void mockCreateServiceImplClass( IDataModel model, String serviceType, String javaPackage, String className )
        throws Exception
    {
        IDataModel dataModel =
            DataModelFactory.createDataModel( new NewServiceWrapperClassDataModelProvider(
                model, className, serviceType + "Wrapper" ) );
        dataModel.setProperty( INewJavaClassDataModelProperties.JAVA_PACKAGE, javaPackage );
        NewServiceWrapperClassOperation operation = new NewServiceWrapperClassOperation( dataModel );
        operation.execute( null, null );
    }
}
