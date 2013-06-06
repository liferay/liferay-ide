package com.liferay.ide.server.tomcat.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.jst.server.tomcat.core.internal.TomcatSourcePathComputerDelegate;


@SuppressWarnings( "restriction" )
public class LiferayTomcatSourcePathComputer extends TomcatSourcePathComputerDelegate
{

    public static final String ID = "com.liferay.ide.server.tomcat.portalSourcePathComputer"; //$NON-NLS-1$

    @Override
    public ISourceContainer[] computeSourceContainers( ILaunchConfiguration configuration, IProgressMonitor monitor )
        throws CoreException
    {
        ISourceContainer[] superContainers = super.computeSourceContainers( configuration, monitor );

        // add theme plugin _diffs folders
        List<ISourceContainer> containers = new ArrayList<ISourceContainer>();
//
//        for( IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects() )
//        {
//            if( ProjectUtil.isThemeProject( project ) && ProjectUtil.isSDKProject( project ) )
//            {
//                IFolder diffs = project.getFolder( "docroot/_diffs" ); //$NON-NLS-1$
//
//                if( diffs.exists() )
//                {
//                    containers.add( new FolderSourceContainer( diffs, true ) );
//                }
//            }
//            else if( "Servers".equals( project.getName() ) ) //$NON-NLS-1$
//            {
//                containers.add( new FolderSourceContainer( project, true ) );
//            }
//        }


        Collections.addAll( containers, superContainers );

        return containers.toArray( new ISourceContainer[0] );
    }

}

