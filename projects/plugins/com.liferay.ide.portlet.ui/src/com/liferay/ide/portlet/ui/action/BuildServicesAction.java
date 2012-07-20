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
 *******************************************************************************/

package com.liferay.ide.portlet.ui.action;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.core.job.BuildServiceJob;
import com.liferay.ide.ui.action.AbstractObjectAction;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Greg Amerson
 */
public class BuildServicesAction extends AbstractObjectAction
{

    public BuildServicesAction()
    {
        super();
    }

    public void run( IAction action )
    {
        if( fSelection instanceof IStructuredSelection )
        {
            Object[] elems = ( (IStructuredSelection) fSelection ).toArray();

            Object elem = elems[0];

            IProject project = null;

            if( elem instanceof IFile )
            {
                IFile projectFile = (IFile) elem;

                project = projectFile.getProject();
            }
            else if( elem instanceof IProject )
            {
                project = (IProject) elem;

            }

            IFile servicesFile = null;
            IFolder docroot = CoreUtil.getDocroot( project );

            if( docroot != null && docroot.exists() )
            {
                servicesFile = docroot.getFile( "WEB-INF/" + ILiferayConstants.LIFERAY_SERVICE_BUILDER_XML_FILE );
            }

            if( servicesFile != null && servicesFile.exists() )
            {
                BuildServiceJob job = PortletCore.createBuildServiceJob( servicesFile );

                job.schedule();
            }
        }

    }

}
