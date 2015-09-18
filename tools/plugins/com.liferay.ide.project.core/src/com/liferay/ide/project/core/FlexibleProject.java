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
package com.liferay.ide.project.core;

import com.liferay.ide.core.BaseLiferayProject;
import com.liferay.ide.core.IWebProject;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.common.jdt.internal.javalite.JavaCoreLite;
import org.eclipse.jst.common.jdt.internal.javalite.JavaLiteUtilities;
import org.eclipse.jst.j2ee.componentcore.J2EEModuleVirtualComponent;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;


/**
 * @author Gregory Amerson
 */
public abstract class FlexibleProject extends BaseLiferayProject implements IWebProject
{

    private static IFolder getDefaultDocroot( IProject project )
    {
        IFolder folder = null;

        final IVirtualFolder webappRoot = getVirtualDocroot( project );

        if( webappRoot != null )
        {
            try
            {
                final IPath defaultFolder =
                    J2EEModuleVirtualComponent.getDefaultDeploymentDescriptorFolder( webappRoot );

                if( defaultFolder != null )
                {
                    IFolder f = project.getFolder( defaultFolder );

                    if( f.exists() )
                    {
                        folder = f;
                    }
                }
            }
            catch( Exception e )
            {
                ProjectCore.logError( "Could not determine default docroot", e );
            }
        }

        return folder;
    }

    private static IVirtualFolder getVirtualDocroot( IProject project )
    {
        IVirtualFolder retval = null;

        if( project != null )
        {
            IVirtualComponent comp = ComponentCore.createComponent( project );

            if( comp != null )
            {
                retval = comp.getRootFolder();
            }
        }

        return retval;
    }

    public FlexibleProject( IProject project )
    {
        super( project );
    }

    @Override
    public IResource findDocrootResource( IPath path )
    {
        IFile retval = null;

        final IVirtualFolder docroot = getVirtualDocroot( getProject() );

        if( docroot != null )
        {
            final IVirtualResource virtualResource = docroot.findMember( path );

            if( virtualResource != null && virtualResource.exists() )
            {
                for( IResource r : virtualResource.getUnderlyingResources() )
                {
                    if( r.exists() && r instanceof IFile )
                    {
                        retval = (IFile) r;
                        break;
                    }
                }
            }
        }

        return retval;
    }

    @Override
    public IFolder getDefaultDocrootFolder()
    {
        return getDefaultDocroot( getProject() );
    }

    @Override
    public IFile getDescriptorFile( String name )
    {
        IFile retval = null;

//        if( ! CoreUtil.isLiferayProject( project ) )
//        {
//            project = CoreUtil.getLiferayProject( project );
//        }

        final IFolder defaultDocrootFolder = getDefaultDocrootFolder();

        if( defaultDocrootFolder != null && defaultDocrootFolder.exists() )
        {
            retval = defaultDocrootFolder.getFile( new Path( "WEB-INF" ).append( name ) );
        }

        if( retval == null )
        {
            // fallback to looping through all virtual folders
            final IVirtualFolder webappRoot = getVirtualDocroot( getProject() );

            if( webappRoot != null )
            {
                for( IContainer container : webappRoot.getUnderlyingFolders() )
                {
                    if( container != null && container.exists() )
                    {
                        final IFile descriptorFile = container.getFile( new Path( "WEB-INF" ).append( name ) );

                        if( descriptorFile.exists() )
                        {
                            retval = descriptorFile;
                            break;
                        }
                    }
                }
            }
        }

        return retval;
    }

    @Override
    public IFolder[] getSourceFolders()
    {
        final List<IFolder> retval = new ArrayList<IFolder>();

        final List<IContainer> sourceFolders =
            JavaLiteUtilities.getJavaSourceContainers( JavaCoreLite.create( getProject() ) );

        if( sourceFolders != null && sourceFolders.size() > 0 )
        {
            for( IContainer sourceFolder : sourceFolders )
            {
                if( sourceFolder instanceof IFolder )
                {
                    retval.add( (IFolder) sourceFolder );
                }
            }
        }

        return retval.toArray( new IFolder[retval.size()] );
    }

    public boolean pathInDocroot( IPath path )
    {
        if( path != null )
        {
            IVirtualFolder webappRoot = getVirtualDocroot( getProject() );

            if( webappRoot != null )
            {
                for( IContainer container : webappRoot.getUnderlyingFolders() )
                {
                    boolean isDocrootResource = container != null && container.exists() &&
                        container.getFullPath().isPrefixOf( path );

                    if ( isDocrootResource == true )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
