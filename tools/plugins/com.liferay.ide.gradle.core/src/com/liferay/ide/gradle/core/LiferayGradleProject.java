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
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.util.ProjectUtil;

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
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 */
public class LiferayGradleProject extends BaseLiferayProject implements IBundleProject, IResourceBundleProject
{

    private static final String[] ignorePaths = new String[] { ".gradle", "build", "dist", "liferay-theme.json" };

    public <T> T adapt( Class<T> adapterType )
    {
        T adapter = super.adapt( adapterType );

        if( adapter != null )
        {
            return adapter;
        }

        if( IProjectBuilder.class.equals( adapterType ) )
        {
            final IProjectBuilder projectBuilder = new GradleProjectBuilder( getProject() );

            return adapterType.cast( projectBuilder );
        }

        return null;
    }

    @Override
    public IPath getOutputBundlePath()
    {
        IProject gradleProject = getProject();

        File buildFolder = gradleProject.getLocation().append( "build/libs" ).toFile();

        String[] fileNames = buildFolder.list();

        // find the only file
        if( fileNames != null && fileNames.length == 1 )
        {
            File outputFile = new File( buildFolder, fileNames[0] );

            return new Path( outputFile.getAbsolutePath() );
        }
        else
        {
            File nodeThemeOutput =
                gradleProject.getLocation().append( "dist/" + gradleProject.getName() + ".war" ).toFile();

            if( nodeThemeOutput.exists() )
            {
                return new Path( nodeThemeOutput.getAbsolutePath() );
            }
            else
            {
                // using CustomModel if can't find output
                IPath retval = null;

                final CustomModel model = GradleCore.getToolingModel( CustomModel.class, gradleProject );
                
                if( model != null )
                {
                    Set<File> outputFiles = model.getOutputFiles();

                    if( outputFiles.size() > 0 )
                    {
                        // first check to see if there are any outputfiles that are wars, if so use that one.
                        File bundleFile = null;

                        for( File outputFile : outputFiles )
                        {
                            if( outputFile.getName().endsWith( ".war" ) )
                            {
                                bundleFile = outputFile;
                                break;
                            }
                        }

                        if( bundleFile == null )
                        {
                            for( File outputFile : outputFiles )
                            {
                                final String name = outputFile.getName();

                                if( name.endsWith( "javadoc.jar" ) || name.endsWith( "jspc.jar" ) ||
                                    name.endsWith( "sources.jar" ) )
                                {
                                    continue;
                                }

                                if( name.endsWith( ".jar" ) )
                                {
                                    bundleFile = outputFile;
                                    break;
                                }
                            }
                        }

                        if( bundleFile != null )
                        {
                            retval = new Path( bundleFile.getAbsolutePath() );
                        }
                    }

                    else if( model.hasPlugin( "com.liferay.gradle.plugins.gulp.GulpPlugin" ) )
                    {
                        retval = gradleProject.getLocation().append( "dist/" + gradleProject.getName() + ".war" );
                    }
                }

                return retval;
            }
        }
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

    @Override
    public IFolder[] getSourceFolders()
    {
        final IFile gulpFile = getProject().getFile( "gulpfile.js" );

        if( gulpFile != null && gulpFile.exists() )
        {
            return new IFolder[] { getProject().getFolder( "src" ) };
        }

        return super.getSourceFolders();
    }

    private IFolder createResorcesFolder( IProject project )
    {
        try
        {
            IJavaProject javaProject = JavaCore.create( project );

            List<IClasspathEntry> existingRawClasspath;

            existingRawClasspath = Arrays.asList( javaProject.getRawClasspath() );

            List<IClasspathEntry> newRawClasspath = new ArrayList<>();

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
        return getProject().getFile( name );
    }

    @Override
    public IPath getLibraryPath( String filename )
    {
        return null;
    }

    @Override
    public IPath getOutputBundle( boolean cleanBuild, IProgressMonitor monitor ) throws CoreException
    {
        ProjectConnection connection = null;

        try
        {
            GradleConnector connector =
                GradleConnector.newConnector().forProjectDirectory( getProject().getLocation().toFile() );

            connection = connector.connect();

            BuildLauncher launcher = connection.newBuild();

            BlockingResultHandler<Object> handler = new BlockingResultHandler<>( Object.class );

            if( cleanBuild )
            {
                launcher.forTasks( "clean", "assemble" ).run( handler );
            }
            else
            {
                launcher.forTasks( "assemble" ).run( handler );
            }

            handler.getResult();
        }
        catch( Exception e )
        {
            GradleCore.logError( "Project " + getProject().getName() + " build output error", e );
            //throw e;
        }
        finally
        {
            if( connection != null )
            {
                connection.close();
            }
        }

        IPath outputBundlePath = getOutputBundlePath();

        if( outputBundlePath.toFile().exists() )
        {
            return outputBundlePath;
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
        String bsn = ProjectUtil.getBundleSymbolicNameFromBND( getProject() );

        if( !CoreUtil.empty( bsn ) )
        {
            return bsn;
        }

        String retval = null;

        final IPath outputBundle = getOutputBundlePath();

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

}