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

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.gradle.core.modules.NewJSPHookModuleOp;
import com.liferay.ide.gradle.core.modules.OSGiCustomJSP;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.modules.BladeCLI;

import java.io.File;
import java.io.IOException;

import org.eclipse.buildship.core.configuration.GradleProjectNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Terry Jia
 */
public class JSPHookProjectProvider extends AbstractLiferayProjectProvider
    implements NewLiferayProjectProvider<NewJSPHookModuleOp>
{

    public JSPHookProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    @Override
    public synchronized ILiferayProject provide( Object adaptable )
    {
        ILiferayProject retval = null;

        if( adaptable instanceof IProject )
        {
            final IProject project = (IProject) adaptable;

            try
            {
                if( LiferayNature.hasNature( project ) && GradleProjectNature.INSTANCE.isPresentOn( project ) )
                {
                    return new LiferayGradleProject( project );
                }
            }
            catch( Exception e )
            {
                // ignore errors
            }
        }

        return retval;
    }

    @Override
    public IStatus createNewProject( NewJSPHookModuleOp op, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = Status.OK_STATUS;

        final String projectName = op.getProjectName().content();

        IPath location = PathBridge.create( op.getLocation().content() );

        final String hostBundle = op.getCustomOSGiBundle().content();

        IPath temp = GradleCore.getDefault().getStateLocation().append( hostBundle );

        try
        {
            ZipUtil.unzip( new File( op.getRealOSGiBundleFile().content() ), temp.toFile() );
        }
        catch( IOException e1 )
        {
            e1.printStackTrace();
        }

        String bundleSymbolicName = "";
        String version = "";

        if( temp.toFile().exists() )
        {
            File file = temp.append( "META-INF" ).append( "MANIFEST.MF" ).toFile();
            String[] contents = FileUtil.readLinesFromFile( file );

            for( String content : contents )
            {
                if( content.contains( "Bundle-SymbolicName:" ) )
                {
                    bundleSymbolicName =
                        content.substring( content.indexOf( "Bundle-SymbolicName:" ) + "Bundle-SymbolicName:".length() );
                }

                if( content.contains( "Bundle-Version:" ) )
                {
                    version = content.substring( content.indexOf( "Bundle-Version:" ) + "Bundle-Version:".length() );
                }
            }
        }

        ElementList<OSGiCustomJSP> jsps = op.getCustomJSPs();

        StringBuilder sb = new StringBuilder();
        sb.append( "create " );
        sb.append( "-d \"" + location.toFile().getAbsolutePath() + "\" " );
        sb.append( "-t " + "jsphook" + " " );

        if( !bundleSymbolicName.equals( "" ) )
        {
            sb.append( "-h " + bundleSymbolicName + " " );
        }

        if( !version.equals( "" ) )
        {
            sb.append( "-H " + version + " " );
        }

        sb.append( "\"" + projectName + "\" " );

        try
        {
            final String[] ret = BladeCLI.execute( sb.toString() );

            final String errors = BladeCLI.checkForErrors( ret );

            if( errors.length() > 0 )
            {
                retval = GradleCore.createErrorStatus( "Project create error: " + errors );
                return retval;
            }

            IPath projecLocation = location;

            final String lastSegment = location.lastSegment();

            if( location != null && location.segmentCount() > 0 )
            {
                if( !lastSegment.equals( projectName ) )
                {
                    projecLocation = location.append( projectName );
                }
            }

            for( OSGiCustomJSP jsp : jsps )
            {
                File jspFile = temp.append( jsp.getValue().content() ).toFile();

                if( jspFile.exists() )
                {
                    String parent = jspFile.getParentFile().getPath();
                    parent = parent.replaceAll( "\\\\", "/" );
                    String metaInfResources = "META-INF/resources";

                    parent = parent.substring( parent.indexOf( metaInfResources ) + metaInfResources.length() );

                    IPath resources =
                        location.append( projectName ).append( "src" ).append( "main" ).append( "resources" ).append(
                            "META-INF" ).append( "resources" );
                    File folder = resources.toFile();

                    if( !parent.equals( "resources" ) && !parent.equals( "" ) )
                    {
                        folder = resources.append( parent ).toFile();
                        folder.mkdirs();
                    }

                    FileUtil.copyFileToDir( jspFile, folder );
                }
            }

            GradleUtil.importGradleProject( projecLocation.toFile(), monitor );
        }
        catch( Exception e )
        {
            retval = GradleCore.createErrorStatus( "can't create module project.", e );
        }

        return retval;
    }

    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        IStatus retval = Status.OK_STATUS;

        File projectFodler = path.append( projectName ).toFile();

        if( projectFodler.exists() )
        {
            retval = GradleCore.createErrorStatus( " Project folder is not empty. " );
        }

        return retval;
    }

}
