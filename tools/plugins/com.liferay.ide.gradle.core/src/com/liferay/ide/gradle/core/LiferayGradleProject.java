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

package com.liferay.ide.gradle.core;

import aQute.bnd.osgi.Jar;

import com.liferay.blade.gradle.model.CustomModel;
import com.liferay.ide.core.BaseLiferayProject;
import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.IResourceBundleProject;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.PropertiesUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 */
public class LiferayGradleProject extends BaseLiferayProject implements IBundleProject, IResourceBundleProject
{

    private static final String[] ignorePaths = new String[] { ".gradle", "build" };

    public static IPath getOutputBundle( IProject gradleProject )
    {
        final CustomModel model = GradleCore.getToolingModel(
            GradleCore.getDefault(), CustomModel.class, gradleProject );

        Set<File> outputFiles = model.getOutputFiles();

        if( outputFiles.size() > 0 )
        {
            return new Path( outputFiles.iterator().next().getAbsolutePath() );
        }
        else if( model.hasPlugin( "com.liferay.gradle.plugins.gulp.GulpPlugin" ) )
        {
            return gradleProject.getLocation().append( "dist/" + gradleProject.getName() + ".war" );
        }

        return null;
    }

    public LiferayGradleProject( IProject project )
    {
        super( project );
    }

    @Override
    public boolean filterResource( IPath resourcePath )
    {
        if( filterResource( resourcePath, ignorePaths ) )
        {
            return true;
        }

        return false;
    }

    @Override
    public String getBundleShape()
    {
        return "jar";
    }

    @Override
    public List<IFile> getDefaultLanguageProperties()
    {
        return PropertiesUtil.getDefaultLanguagePropertiesFromModuleProject( getProject() );
    }

    @Override
    public IFolder getSourceFolder( String classification )
    {
        IFolder retval = null;
        IFolder[] sourceFolders = getSourceFolders();

        for( IFolder folder : sourceFolders )
        {
            if( folder.getName().equals( classification ) )
            {
                retval = folder;

                break;
            }
        }

        if( classification.equals( "resources" ) )
        {
            if( retval == null )
            {
                retval = createResorcesFolder( getProject() );
            }
        }

        return retval;
    }

    private IFolder createResorcesFolder( IProject project )
    {
        try
        {
            IJavaProject javaProject = JavaCore.create( project );

            List<IClasspathEntry> existingRawClasspath;

            existingRawClasspath = Arrays.asList( javaProject.getRawClasspath() );

            List<IClasspathEntry> newRawClasspath = new ArrayList<IClasspathEntry>();

            IClasspathAttribute[] attributes =
                new IClasspathAttribute[] { JavaCore.newClasspathAttribute( "FROM_GRADLE_MODEL", "true" ) }; //$NON-NLS-1$ //$NON-NLS-2$

            IClasspathEntry resourcesEntry = JavaCore.newSourceEntry(
                project.getFullPath().append( "src/main/resources" ), new IPath[0], new IPath[0], null, attributes );

            for( IClasspathEntry entry : existingRawClasspath )
            {
                newRawClasspath.add( entry );
            }

            if( !existingRawClasspath.contains( resourcesEntry ) )
            {
                newRawClasspath.add( resourcesEntry );
            }

            javaProject.setRawClasspath( newRawClasspath.toArray( new IClasspathEntry[0] ), new NullProgressMonitor() );

            project.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );

            IFolder[] sourceFolders = getSourceFolders();

            for( IFolder folder : sourceFolders )
            {
                if( folder.getName().equals( "resources" ) )
                {
                    return folder;
                }
            }
        }
        catch( CoreException e )
        {
            GradleCore.logError( e );
        }

        return null;
    }

    @Override
    public IFile getDescriptorFile( String name )
    {
        return null;
    }

    @Override
    public IPath getLibraryPath( String filename )
    {
        return null;
    }

    @Override
    public IPath getOutputBundle( boolean build, IProgressMonitor monitor ) throws CoreException
    {
        IPath outputBundle = getOutputBundle( getProject() );

        if( !build && outputBundle != null && outputBundle.toFile().exists() )
        {
            return outputBundle;
        }
        else
        {
            final String task = isThemeProject( getProject() ) ? "build" : "jar";
            ProjectConnection connection = null;

            try
            {
                GradleConnector connector =
                    GradleConnector.newConnector().forProjectDirectory( getProject().getLocation().toFile() );

                connection = connector.connect();

                BuildLauncher launcher = connection.newBuild();

                BlockingResultHandler<Object> handler = new BlockingResultHandler<>( Object.class );

                launcher.forTasks( task ).run( handler );

                handler.getResult();
            }
            catch( GradleConnectionException e)
            {
                throw new CoreException( GradleCore.createErrorStatus( "Unable to build output", e ) );
            }
            finally
            {
                connection.close();
            }

            outputBundle = getOutputBundle( getProject() );
        }

        if( outputBundle.toFile().exists() )
        {
            return outputBundle;
        }

        return null;
    }

    @Override
    public String getProperty( String key, String defaultValue )
    {
        return null;
    }

    @Override
    public String getSymbolicName() throws CoreException
    {
        String retval = null;

        final IPath outputBundle = getOutputBundle( getProject() );

        if( outputBundle == null || outputBundle.lastSegment().endsWith( ".war" ) )
        {
            return getProject().getName();
        }
        else if( outputBundle != null && outputBundle.toFile().exists() )
        {
            try( final Jar jar = new Jar( outputBundle.toFile() ) )
            {
                retval = jar.getBsn();
            }
            catch( Exception e )
            {
            }
        }

        return retval;
    }

    public boolean isFragmentBundle()
    {
        final IFile bndFile = getProject().getFile( "bnd.bnd" );

        if( bndFile.exists() )
        {
            try
            {
                String content = FileUtil.readContents( bndFile.getContents() );

                if( content.contains( "Fragment-Host" ) )
                {
                    return true;
                }
            }
            catch( Exception e )
            {
            }
        }

        return false;
    }

    private boolean isThemeProject( IProject project )
    {
        return project.getFile( "gulpfile.js" ).exists() &&
            project.getFile( "package.json" ).exists() &&
            project.getFile( "src/WEB-INF/liferay-plugin-package.properties" ).exists();
    }

}