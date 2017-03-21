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
package com.liferay.ide.portal.core.debug;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portal.core.debug.fm.FMDebugTarget;
import com.liferay.ide.portal.core.debug.fm.FMStackFrame;
import com.liferay.ide.portal.core.debug.fm.FMThread;
import com.liferay.ide.server.util.ServerUtil;

import freemarker.debug.Breakpoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;


/**
 * @author Gregory Amerson
 */
public class PortalSourceLookupParticipant extends AbstractSourceLookupParticipant
{

    @Override
    public Object[] findSourceElements( Object object ) throws CoreException
    {
        Object[] retval = null;

        final Object[] sourceElements = super.findSourceElements( object );

        if( object instanceof FMStackFrame )
        {
            String templateName = getTemplateName( (IStackFrame) object  );

            IProject project = getSourceProject( templateName );

            if( project != null )
            {
                // go through all containers and only include the ones in this project
                List<IResource> validSourceElements = new ArrayList<IResource>();

                for( Object sourceElement : sourceElements )
                {
                    if( sourceElement instanceof IResource )
                    {
                        IResource res = (IResource) sourceElement;

                        if(res.getProject().equals( project ))
                        {
                            validSourceElements.add( (IResource) sourceElement );
                        }
                    }
                }

                // check for two source elements in the same project, try to weed one of them out
                if( validSourceElements.size() > 1 )
                {
                    for( Iterator<IResource> i = validSourceElements.iterator(); i.hasNext(); )
                    {
                        IResource res = i.next();

                        if( res.getProjectRelativePath().toPortableString().contains(
                            "target/m2e-liferay/theme-resources" ) ) //$NON-NLS-1$
                        {
                            i.remove();
                        }
                    }
                }

                retval = validSourceElements.toArray(  new Object[0] );
            }
        }

        if( retval == null )
        {
            retval = sourceElements;
        }

        return retval;
    }

    public String getSourceName( Object object ) throws CoreException
    {
        String retval = null;

        if( object instanceof FMStackFrame )
        {
            try
            {
                FMStackFrame frame = (FMStackFrame) object;

                String templateName = getTemplateName( frame );

//                if( templateName.matches( "^\\d+#\\d+#\\d+$" ) ) //$NON-NLS-1$
//                {
//                    retval = templateName + ".ftl"; //$NON-NLS-1$
//                }
//                else
//                {
                // we need to figure out how to map this back to a valid project path.
                // first if it contains a path that points to an existing plugin project

                IPath templatePath = new Path( templateName );

                final String firstSegment = templatePath.segment( 0 );
                final String resourcePath = templatePath.removeFirstSegments( 1 ).toPortableString();

                IProject project = ServerUtil.findProjectByContextName( firstSegment );

                final IWebProject webproject = LiferayCore.create( IWebProject.class, project );

                if( webproject != null )
                {
                    // first lets see if we can find

                    final String diffsPath = "_diffs/" + resourcePath;
                    final IResource diffsResourceFile = webproject.findDocrootResource( new Path( diffsPath ) );

                    if( diffsResourceFile.exists() )
                    {
                        retval = diffsPath;
                    }
                    else
                    {
                        final IResource resourceFile = webproject.findDocrootResource( new Path( resourcePath ) );

                        if( resourceFile.exists() )
                        {
                            retval = resourcePath;
                        }
                    }
                }
            }
            catch( Exception e )
            {
            }
        }

        return retval;
    }

    private  IProject getSourceProject( String templateName )
    {
        IProject retval = null;

        if( ! CoreUtil.isNullOrEmpty( templateName ) )
        {
            final String firstTemplateNameSegment = new Path( templateName ).segment( 0 );

            final IProject project = CoreUtil.getProject( firstTemplateNameSegment );

            if( CoreUtil.isLiferayProject( project ) )
            {
                retval = project;
            }
        }

        return retval;
    }

    private String getTemplateName( IStackFrame frame ) throws DebugException
    {
        String retval = null;
        IThread thread = frame.getThread();

        IBreakpoint[] bps = thread.getBreakpoints();

        if( bps.length == 1 )
        {
            IBreakpoint bp = thread.getBreakpoints()[0];

            retval = bp.getMarker().getAttribute( ILRDebugConstants.FM_TEMPLATE_NAME, null );
        }
        else
        {
            if( thread instanceof FMThread )
            {
                FMThread fmThread = (FMThread) thread;

                Breakpoint stepBp = fmThread.getStepBreakpoint();

                if( stepBp != null )
                {
                    retval = stepBp.getTemplateName().replaceAll( FMDebugTarget.FM_TEMPLATE_SERVLET_CONTEXT, "" ); //$NON-NLS-1$
                }
            }
        }

        return retval;
    }

}
