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

package com.liferay.ide.portlet.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;
import com.liferay.ide.project.core.AbstractPortletFramework;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Simon Jiang
 */
public abstract class BasePortletFramework extends AbstractPortletFramework
{

    @Override
    public IStatus postProjectCreated( IProject project, String frameworkName, String portletName, IProgressMonitor monitor )
    {

        if ( ! CoreUtil.isNullOrEmpty( portletName ) )
        {
            new PortletDescriptorHelper(project).configurePortletXml(portletName); 
        }
        return Status.OK_STATUS;
    }

}
