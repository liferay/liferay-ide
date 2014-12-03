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
package com.liferay.ide.portal.core.debug.fm;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portal.core.PortalCore;
import com.liferay.ide.portal.core.debug.ILRDebugConstants;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;


/**
 * @author Gregory Amerson
 */
public class FMLineBreakpoint extends LineBreakpoint
{

    public FMLineBreakpoint()
    {
        super();
    }

    public FMLineBreakpoint( final IResource resource, final int line ) throws CoreException
    {
        final String templateName = getTemplateName( resource );

        final IWorkspaceRunnable runnable = new IWorkspaceRunnable()
        {
            private String createMessage( String templateName )
            {
                return "Freemarker breakpoint: " + templateName + " [line: " + line + "]";
            }

            public void run( IProgressMonitor monitor ) throws CoreException
            {
                final IMarker marker = resource.createMarker( PortalCore.ID_FM_BREAKPOINT_TYPE );
                marker.setAttribute( IBreakpoint.ENABLED, Boolean.TRUE );
                marker.setAttribute( IMarker.LINE_NUMBER, line );
                marker.setAttribute( IBreakpoint.ID, getModelIdentifier() );
                marker.setAttribute( ILRDebugConstants.FM_TEMPLATE_NAME, templateName );
                marker.setAttribute( IMarker.MESSAGE, createMessage( templateName ) );

                setMarker( marker );
            }
        };

        run( getMarkerRule( resource ), runnable );
    }

    private static String getTemplateName( final IResource resource )
    {
        String retval = null;

        final IProject project = resource.getProject();

        if( project != null && CoreUtil.isLiferayProject( project ) )
        {
//            if( "Servers".equals( project.getName() ) )
//            {
//                return new Path(resource.getName()).removeFileExtension().toPortableString();
//            }

            // get context root
            final IVirtualComponent c = ComponentCore.createComponent( project, true );
            final String contextRoot = c.getMetaProperties().getProperty( "context-root" ) + "/";

            for( IContainer parentFolder : c.getRootFolder().getUnderlyingFolders() )
            {
                final IPath parentFullPath = parentFolder.getFullPath();
                final IPath fileFullPath = resource.getFullPath();

                if( parentFullPath.isPrefixOf( fileFullPath ) )
                {
                    final IPath relativePath = fileFullPath.makeRelativeTo( parentFullPath );
                    final String relativePathValue = relativePath.toPortableString();

                    // remove _diffs prefix since they are not in the deployed resource
                    final String prefix = "_diffs/";

                    if( relativePathValue.startsWith( prefix ) )
                    {
                        retval = contextRoot + relativePathValue.substring( prefix.length(), relativePathValue.length() );
                    }
                    else
                    {
                        retval = contextRoot + relativePathValue;
                    }

                    break;
                }
            }
        }

        return retval;
    }



    public String getModelIdentifier()
    {
        return ILRDebugConstants.ID_FM_DEBUG_MODEL;
    }

}
