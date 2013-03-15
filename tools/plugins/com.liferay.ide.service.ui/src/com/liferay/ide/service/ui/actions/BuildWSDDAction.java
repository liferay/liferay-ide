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

package com.liferay.ide.service.ui.actions;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.service.core.ServiceCore;
import com.liferay.ide.service.core.job.BuildWSDDJob;
import com.liferay.ide.service.ui.ServiceUIUtil;
import com.liferay.ide.ui.action.AbstractObjectAction;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;

/**
 * @author Greg Amerson
 */
public class BuildWSDDAction extends AbstractObjectAction
{

    public BuildWSDDAction()
    {
        super();
    }

    public void run( IAction action )
    {
        if( fSelection instanceof IStructuredSelection )
        {
            Object[] elems = ( (IStructuredSelection) fSelection ).toArray();

            IFile servicesFile = null;

            Object elem = elems[0];

            if( elem instanceof IFile )
            {
                servicesFile = (IFile) elem;

            }
            else if( elem instanceof IProject )
            {
                IProject project = (IProject) elem;

                // IDE-110 IDE-648
                IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

                if( webappRoot != null )
                {
                    for( IContainer container : webappRoot.getUnderlyingFolders() )
                    {
                        if( container != null && container.exists() )
                        {
                            final Path path =
                                new Path( "WEB-INF/" + ILiferayConstants.LIFERAY_SERVICE_BUILDER_XML_FILE ); //$NON-NLS-1$
                            servicesFile = container.getFile( path );

                            break;
                        }
                    }
                }
            }

            if( servicesFile != null && servicesFile.exists() )
            {
                if( ServiceUIUtil.shouldCreateServiceBuilderJob( servicesFile ) )
                {
                    BuildWSDDJob job = ServiceCore.createBuildWSDDJob( servicesFile );

                    job.schedule();
                }
            }
        }

    }

}
