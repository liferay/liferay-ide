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

package com.liferay.ide.project.ui.action.sdk;

import com.liferay.ide.project.ui.ProjectUIPlugin;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.sdk.util.SDKUtil;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.action.AbstractObjectAction;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Gregory Amerson
 */
public abstract class SDKCommandAction extends AbstractObjectAction
{

    public SDKCommandAction()
    {
        super();
    }

    protected abstract String getSDKCommand();

    public void run( IAction action )
    {
        if( fSelection instanceof IStructuredSelection )
        {
            Object[] elems = ( (IStructuredSelection) fSelection ).toArray();

            IFile buildXmlFile = null;
            IProject project = null;

            Object elem = elems[0];

            if( elem instanceof IFile )
            {
                buildXmlFile = (IFile) elem;
                project = buildXmlFile.getProject();
            }
            else if( elem instanceof IProject )
            {
                project = (IProject) elem;
                buildXmlFile = project.getFile( "build.xml" ); //$NON-NLS-1$
            }

            if( buildXmlFile.exists() )
            {
                final IProject p = project;
                final IFile buildFile = buildXmlFile;

                new Job( p.getName() + " : " + getSDKCommand() ) //$NON-NLS-1$
                {

                    @Override
                    protected IStatus run( IProgressMonitor monitor )
                    {
                        try
                        {
                            SDK sdk = SDKUtil.getSDK( p );

                            Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( p );

                            sdk.runCommand( p, buildFile, getSDKCommand(), null, appServerProperties );

                            p.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                        }
                        catch( Exception e )
                        {
                            return ProjectUIPlugin.createErrorStatus( "Error running SDK command " + getSDKCommand(), e ); //$NON-NLS-1$
                        }

                        return Status.OK_STATUS;
                    }
                }.schedule();

            }
        }

    }

}
