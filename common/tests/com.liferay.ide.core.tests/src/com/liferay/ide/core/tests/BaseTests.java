
package com.liferay.ide.core.tests;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

public class BaseTests
{

    protected final IProject createProject( final String name ) throws Exception
    {
        String n = getClass().getName();

        if( name != null )
        {
            n = n + "." + name;
        }

        final IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject( n );
        p.create( null );
        p.open( null );

        return p;
    }

    protected final void deleteProject( final String name ) throws Exception
    {
        String n = getClass().getName();

        if( name != null )
        {
            n = n + "." + name;
        }

        final IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject( n );

        if( p.exists() )
        {
            p.delete( true, null );
        }
    }

    protected final IFolder createFolder( final IProject project, final String path ) throws Exception
    {
        final IFolder folder = project.getFolder( path );
        createFolder( folder );
        return folder;
    }

    protected final void createFolder( final IFolder folder ) throws Exception
    {
        if( !folder.exists() )
        {
            final IContainer parent = folder.getParent();

            if( parent instanceof IFolder )
            {
                createFolder( (IFolder) parent );
            }

            folder.create( true, true, null );
        }
    }

    protected final IFile createFile( final IProject project, final String path, final InputStream content ) throws Exception
    {
        final IFile file = project.getFile( path );
        final IContainer parent = file.getParent();

        if( parent instanceof IFolder )
        {
            createFolder( (IFolder) parent );
        }

        file.create( content, true, null );

        return file;
    }

    protected final IFile createFile( final IProject project, final String path, final byte[] content ) throws Exception
    {
        return createFile( project, path, new ByteArrayInputStream( content ) );
    }

    protected final IFile createFile( final IProject project, final String path ) throws Exception
    {
        return createFile( project, path, new byte[0] );
    }

}
