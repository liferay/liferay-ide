package com.liferay.ide.server.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.debug.core.ILRDebugConstants;
import com.liferay.ide.debug.core.fm.FMStackFrame;
import com.liferay.ide.server.util.ServerUtil;

import java.util.ArrayList;
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
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;


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
                List<Object> validSourceElements = new ArrayList<Object>();

                for( Object sourceElement : sourceElements )
                {
                    if( sourceElement instanceof IResource )
                    {
                        IResource res = (IResource) sourceElement;

                        if(res.getProject().equals( project ))
                        {
                            validSourceElements.add( sourceElement );
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

    private  IProject getSourceProject( String templateName )
    {
        IProject retval = null;

        if( ! CoreUtil.isNullOrEmpty( templateName ) )
        {
            String projectName = new Path(templateName).segment( 0 );

            IProject project = CoreUtil.getProject( projectName.replaceAll( "_SERVLET_CONTEXT_", "" ) ); //$NON-NLS-1$ //$NON-NLS-2$

            if( CoreUtil.isLiferayProject( project ) )
            {
                retval = project;
            }
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
                    // first if it contains _SERVLET_CONTEXT_ we know that this path is pointing to a plugin

                IPath templatePath = new Path( templateName );

                final String firstSegment = templatePath.segment( 0 );
                final String resourcePath = templatePath.removeFirstSegments( 1 ).toPortableString();
                final String lastSegment = templatePath.lastSegment();

                if( firstSegment.contains( "_SERVLET_CONTEXT_" ) ) //$NON-NLS-1$
                {
                    // get the context name and find the project
                    String contextName = firstSegment.replaceAll( "_SERVLET_CONTEXT_", "" ); //$NON-NLS-1$ //$NON-NLS-2$

                    IProject project = ServerUtil.findProjectByContextName( contextName );

                    IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

                    // first lets see if we can find

                    final String diffsPath = "_diffs/" +  resourcePath; //$NON-NLS-1$
                    IVirtualFile diffsResourceFile = webappRoot.getFile( diffsPath );

                    if( diffsResourceFile.exists() )
                    {
                        retval = diffsPath;
                    }
                    else
                    {
                        IVirtualFile resourceFile = webappRoot.getFile( resourcePath );

                        if( resourceFile.exists() )
                        {
                            retval = resourcePath;
                        }
                    }
                }
//                }
            }
            catch( Exception e )
            {
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
            System.out.println( bps.length );
        }

        return retval;
    }

}
