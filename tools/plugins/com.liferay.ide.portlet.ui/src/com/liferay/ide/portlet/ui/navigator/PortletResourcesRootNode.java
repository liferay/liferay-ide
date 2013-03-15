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
 * Contributors:
 *      Kamesh Sampath - initial implementation
 *      Gregory Amerson - initial implementation review and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portlet.ui.navigator;

import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
public class PortletResourcesRootNode
{

    private final IProject liferayProject;
    private final PortletResourcesContentProvider provider;

    public PortletResourcesRootNode( PortletResourcesContentProvider provider, IProject project )
    {
        this.provider = provider;
        this.liferayProject = project;
    }

    public IProject getProject()
    {
        return this.liferayProject;
    }

    public boolean hasChildren()
    {
        if( this.liferayProject != null )
        {
            IFile portletXml = ProjectUtil.getPortletXmlFile( this.liferayProject );

            if( portletXml != null && portletXml.exists() )
            {
                return true;
            }
        }

        return false;
    }

    public void refresh()
    {
        this.provider.refresh();
    }

}
