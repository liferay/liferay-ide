/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 * Contributors:
 *    Kamesh Sampath - initial implementation
 *    Gregory Amerson - IDE-568
 ******************************************************************************/

package com.liferay.ide.project.core;

import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.sdk.SDKManager;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@SuppressWarnings( "restriction" )
public class BinaryProjectsImportOperation extends AbstractDataModelOperation
    implements ISDKProjectsImportDataModelProperties
{

    public BinaryProjectsImportOperation( IDataModel model )
    {
        super( model );
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.project.core.SDKProjectsImportOperation#execute(org.eclipse.core.runtime.IProgressMonitor ,
     * org.eclipse.core.runtime.IAdaptable)
     */
    @Override
    public IStatus execute( IProgressMonitor monitor, final IAdaptable info ) throws ExecutionException
    {
        final String sdkLocation = model.getStringProperty( ISDKProjectsImportDataModelProperties.SDK_LOCATION );
        final IRuntime runtime = (IRuntime) model.getProperty( IFacetProjectCreationDataModelProperties.FACET_RUNTIME );
        final Object[] projects =
            (Object[]) model.getProperty( ISDKProjectsImportDataModelProperties.SELECTED_PROJECTS );
        final BridgedRuntime bridgedRuntime =
            (BridgedRuntime) model.getProperty( IFacetProjectCreationDataModelProperties.FACET_RUNTIME );

        WorkspaceJob job = new WorkspaceJob( "Importing Binary Project Plugins" )
        {
            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
            {
                if( projects != null )
                {
                    SDKManager sdkManager = SDKManager.getInstance();
                    SDK liferaySDK = sdkManager.getSDK( new Path( sdkLocation ) );
                    Object[] seleBinaryRecords = (Object[]) projects;
                    monitor.beginTask( "Creating SDK Projects", seleBinaryRecords.length );
                    ProjectRecord[] projectRecords = new ProjectRecord[seleBinaryRecords.length];

                    for( int i = 0; i < seleBinaryRecords.length; i++ )
                    {
                        BinaryProjectRecord pluginBinaryRecord = (BinaryProjectRecord) seleBinaryRecords[i];

                        try
                        {
                            monitor.setTaskName( "Creating Plugin  " + pluginBinaryRecord.getLiferayPluginName() );
                            projectRecords[i] =
                                ProjectImportUtil.createPluginProject( bridgedRuntime, pluginBinaryRecord, liferaySDK );
                            monitor.worked( 1 );
                        }
                        catch( IOException e )
                        {
                            throw new CoreException( ProjectCorePlugin.createErrorStatus( "Error creating project.", e ) );
                        }

                    }

                    monitor.done();

                    ProjectImportUtil.createWorkspaceProjects( projectRecords, runtime, sdkLocation, monitor );

                }

                return Status.OK_STATUS;
            }
        };

        job.setUser( true );
        job.schedule();

        return Status.OK_STATUS;
    }

}
