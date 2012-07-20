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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class BinaryProjectImportDataModelProvider extends SDKProjectsImportDataModelProvider
{

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.project.core.LiferayProjectImportDataModelProvider#getDefaultOperation()
     */
    @Override
    public IDataModelOperation getDefaultOperation()
    {
        return new BinaryProjectImportOperation( this.model );
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.project.core.SDKProjectsImportDataModelProvider#createProjectErrorStatus()
     */
    @Override
    public IStatus createSelectedProjectsErrorStatus()
    {

        return ProjectCorePlugin.createErrorStatus( "Select a binary to import." );

    }

}
