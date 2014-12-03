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
package com.liferay.ide.server.util;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.server.core.LiferayServerCore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.validation.internal.ValType;
import org.eclipse.wst.validation.internal.ValidationRunner;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class ComponentUtil
{
    public static boolean containsMember( IModuleResourceDelta delta, String[] paths )
    {
        if( delta == null )
        {
            return false;
        }

        // iterate over the path and find matching child delta
        IModuleResourceDelta[] currentChildren = delta.getAffectedChildren();

        if( currentChildren == null )
        {
            IFile file = (IFile) delta.getModuleResource().getAdapter( IFile.class );

            if( file != null )
            {
                String filePath = file.getFullPath().toString();

                for( String path : paths )
                {
                    if( filePath.contains( path ) )
                    {
                        return true;
                    }
                }
            }

            return false;
        }

        for( int j = 0, jmax = currentChildren.length; j < jmax; j++ )
        {
            IPath moduleRelativePath = currentChildren[j].getModuleRelativePath();
            String moduleRelativePathValue = moduleRelativePath.toString();
            String moduleRelativeLastSegment = moduleRelativePath.lastSegment();

            for( String path : paths )
            {
                if( moduleRelativePathValue.equals( path ) || moduleRelativeLastSegment.equals( path ) )
                {
                    return true;
                }
            }

            boolean childContains = containsMember( currentChildren[j], paths );

            if( childContains )
            {
                return true;
            }
        }

        return false;
    }

    public static IFile findServiceJarForContext( String context )
    {
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

        for( IProject project : projects )
        {
            if( project.getName().equals( context ) )
            {
                final ILiferayProject lrproject = LiferayCore.create( project );
                final IResource resource =
                    lrproject.findDocrootResource( new Path( "WEB-INF/lib/" + project.getName() + "-service.jar" ) );

                if( resource != null && resource.exists() )
                {
                    return (IFile) resource;
                }
            }
        }

        return null;
    }

    public static boolean hasLiferayFacet( IProject project )
    {
        boolean retval = false;

        if( project == null )
        {
            return retval;
        }

        try
        {
            IFacetedProject facetedProject = ProjectFacetsManager.create( project );

            if( facetedProject != null )
            {
                for( IProjectFacetVersion facet : facetedProject.getProjectFacets() )
                {
                    IProjectFacet projectFacet = facet.getProjectFacet();

                    if( projectFacet.getId().startsWith( "liferay" ) ) //$NON-NLS-1$
                    {
                        retval = true;
                        break;
                    }
                }
            }
        }
        catch( Exception e )
        {
        }

        return retval;
    }

    public static void validateFile( IFile file, IProgressMonitor monitor )
    {
        try
        {
            ValidationRunner.validate( file, ValType.Manual, monitor, false );
        }
        catch( CoreException e )
        {
            LiferayServerCore.logError( "Error while validating file: " + file.getFullPath(), e ); //$NON-NLS-1$
        }
    }

    public static void validateFolder( IFolder folder, IProgressMonitor monitor )
    {
        try
        {
            Map<IProject, Set<IResource>> projects = new HashMap<IProject, Set<IResource>>();
            final Set<IResource> resources = new HashSet<IResource>();

            folder.accept
            (
                new IResourceVisitor()
                {
                    public boolean visit( IResource resource ) throws CoreException
                    {
                        if( resource instanceof IFile || resource instanceof IFile )
                        {
                            resources.add( resource );
                        }

                        return true;
                    }
                }
            );

            projects.put( folder.getProject(), resources );
            ValidationRunner.validate( projects, ValType.Manual, monitor, false );
        }
        catch( CoreException e )
        {
            LiferayServerCore.logError( "Error while validating folder: " + folder.getFullPath(), e );
        }
    }
}
