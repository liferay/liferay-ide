package com.liferay.ide.debug.core.fm;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.debug.core.ILRDebugConstants;
import com.liferay.ide.debug.core.LiferayDebugCore;

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


public class FMLineBreakpoint extends LineBreakpoint
{

    public static final String ATTR_TEMPLATE_NAME = "templateName";

    public FMLineBreakpoint()
    {
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
                final IMarker marker = resource.createMarker( LiferayDebugCore.ID_FM_BREAKPOINT_TYPE );
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
//            else
//            {

            // get context root
            final IVirtualComponent c = ComponentCore.createComponent( project, true );
            final String contextRoot = c.getMetaProperties().getProperty( "context-root" );
            final String servletContext = contextRoot + "_SERVLET_CONTEXT_/";

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
                        retval =
                            servletContext + relativePathValue.substring( prefix.length(), relativePathValue.length() );
                    }
                    else
                    {
                        retval = servletContext + relativePathValue;
                    }

                    break;
                }
            }
//            }
        }

        return retval;
    }



    public String getModelIdentifier()
    {
        return ILRDebugConstants.ID_FM_DEBUG_MODEL;
    }

}
