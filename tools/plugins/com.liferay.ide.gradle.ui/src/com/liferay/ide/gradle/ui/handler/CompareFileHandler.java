package com.liferay.ide.gradle.ui.handler;

import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.project.ui.handlers.AbstractCompareFileHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author Lovett Li
 */
public class CompareFileHandler extends AbstractCompareFileHandler
{

    protected File getTemplateFile( IFile currentFile ) throws Exception
    {
        final IProject currentProject = currentFile.getProject();
        final IFile bndfile = currentProject.getFile( "bnd.bnd" );

        File templateFile = null;

        try( final BufferedReader reader = new BufferedReader( new InputStreamReader( bndfile.getContents() ) ) )
        {
            String fragment;

            while( ( fragment = reader.readLine() ) != null )
            {
                if( fragment.startsWith( "Fragment-Host:" ) )
                {
                    fragment = fragment.substring( fragment.indexOf( ":" ) + 1, fragment.indexOf( ";" ) ).trim();
                    break;
                }
            }

            final String hookfolder = currentFile.getFullPath().toOSString().substring(
                currentFile.getFullPath().toOSString().lastIndexOf( "META-INF" ) );
            final IPath templateLocation =
                GradleCore.getDefault().getStateLocation().append( fragment ).append( hookfolder );

            templateFile = new File( templateLocation.toOSString() );

            if( !templateFile.exists() )
            {
                throw new FileNotFoundException( "Template not found." );
            }
        }

        return templateFile;
    }

}
