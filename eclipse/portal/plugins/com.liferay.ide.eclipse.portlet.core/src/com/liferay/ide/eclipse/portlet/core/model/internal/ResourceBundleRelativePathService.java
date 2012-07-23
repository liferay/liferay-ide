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
 *               Kamesh Sampath  initial implementation
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.core.model.internal;

import com.liferay.ide.eclipse.core.model.internal.GenericResourceBundlePathService;
import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;

import org.eclipse.core.resources.IProject;

/**
 * @author Kamesh Sampath
 */
public class ResourceBundleRelativePathService extends GenericResourceBundlePathService
{

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.core.model.internal.GenericResourceBundlePathService#project()
     */
    @Override
    protected IProject project()
    {
        final IProject project = context().find( IPortletApp.class ).adapt( IProject.class );
        return project;
    }

}
