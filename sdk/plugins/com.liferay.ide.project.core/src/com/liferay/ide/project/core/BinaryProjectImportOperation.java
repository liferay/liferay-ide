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
 ******************************************************************************/

package com.liferay.ide.project.core;

import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.sdk.SDKManager;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@SuppressWarnings( "restriction" )
public class BinaryProjectImportOperation extends SDKProjectsImportOperation
{

    public BinaryProjectImportOperation( IDataModel model )
    {
        super( model );
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.project.core.LiferayProjectImportOperation#execute(org.eclipse.core.runtime.IProgressMonitor
     * , org.eclipse.core.runtime.IAdaptable)
     */
    @Override
    public IStatus execute( IProgressMonitor monitor, IAdaptable info ) throws ExecutionException
    {
        Object selectedProjects = getDataModel().getProperty( ISDKProjectsImportDataModelProperties.SELECTED_PROJECTS );
        final BridgedRuntime bridgedRuntime =
            (BridgedRuntime) model.getProperty( IFacetProjectCreationDataModelProperties.FACET_RUNTIME );
        final String sdkLocation = model.getStringProperty( ISDKProjectsImportDataModelProperties.SDK_LOCATION );

        if( selectedProjects != null )
        {

            SDKManager sdkManager = SDKManager.getInstance();
            SDK liferaySDK = sdkManager.getSDK( new Path( sdkLocation ) );
            Object[] seleBinaryRecords = (Object[]) selectedProjects;
            ProjectRecord[] projectRecords = new ProjectRecord[1];

            if( seleBinaryRecords != null )
            {
                BinaryProjectRecord pluginBinaryRecord = (BinaryProjectRecord) seleBinaryRecords[0];

                try
                {
                    projectRecords[0] =
                        ProjectImportUtil.createSDKPluginProject( bridgedRuntime, pluginBinaryRecord, liferaySDK );
                }
                catch( IOException e )
                {
                    throw new ExecutionException(
                        "Error while importing Binary:" + pluginBinaryRecord.getBinaryName(), e ); //$NON-NLS-1$
                }

                getDataModel().setProperty( SELECTED_PROJECTS, projectRecords );
            }
        }

        return super.execute( monitor, info );
    }
}
