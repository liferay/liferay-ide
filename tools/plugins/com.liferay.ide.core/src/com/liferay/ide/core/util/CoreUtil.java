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

package com.liferay.ide.core.util;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.adapter.NoopLiferayProject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Version;
import org.w3c.dom.Node;

/**
 * Core Utility methods
 *
 * @author Gregory Amerson
 * @author Kuo Zhang
 * @author Simon Jiang
 * @author Terry Jia
 */
public class CoreUtil
{

    public static void addNaturesToProject( IProject proj, String[] natureIds, IProgressMonitor monitor )
        throws CoreException
    {
        IProjectDescription description = proj.getDescription();

        String[] prevNatures = description.getNatureIds();
        String[] newNatures = new String[prevNatures.length + natureIds.length];

        System.arraycopy( prevNatures, 0, newNatures, 0, prevNatures.length );

        for( int i = prevNatures.length; i < newNatures.length; i++ )
        {
            newNatures[i] = natureIds[i - prevNatures.length];
        }

        description.setNatureIds( newNatures );
        proj.setDescription( description, monitor );
    }

    /**
     * Compares v1 <-> v2, if v1 is greater than v2, then result will be > 0
     * if v2 is greater than v1 the result will be < 0
     *
     * @param v1
     * @param v2
     * @return
     */
    public static int compareVersions( Version v1, Version v2 )
    {
        if( v2 == v1 )
        { // quicktest
            return 0;
        }

        int result = v1.getMajor() - v2.getMajor();

        if( result != 0 )
        {
            return result;
        }

        result = v1.getMinor() - v2.getMinor();

        if( result != 0 )
        {
            return result;
        }

        result = v1.getMicro() - v2.getMicro();

        if( result != 0 )
        {
            return result;
        }

        return v1.getQualifier().compareTo( v2.getQualifier() );
    }

    public static boolean containsNullElement( Object[] array )
    {
        if( isNullOrEmpty( array ) )
        {
            return true;
        }

        for( int i = 0; i < array.length; i++ )
        {
            if( array[i] == null )
            {
                return true;
            }
        }

        return false;
    }

    public static void createEmptyFile( IFile newFile ) throws CoreException
    {
        if( newFile.getParent() instanceof IFolder )
        {
            prepareFolder( (IFolder) newFile.getParent() );
        }

        newFile.create( new ByteArrayInputStream( new byte[0] ), true, null );
    }

    public static IStatus createErrorStatus( String msg )
    {
        return new Status( IStatus.ERROR, LiferayCore.PLUGIN_ID, msg );
    }

    public static final String createStringDigest( final String str )
    {
        try
        {
            final MessageDigest md = MessageDigest.getInstance( "SHA-256" ); //$NON-NLS-1$
            final byte[] input = str.getBytes( "UTF-8" ); //$NON-NLS-1$
            final byte[] digest = md.digest( input );

            final StringBuilder buf = new StringBuilder();

            for( int i = 0; i < digest.length; i++ )
            {
                String hex = Integer.toHexString( 0xFF & digest[ i ] );

                if( hex.length() == 1 )
                {
                    buf.append( '0' );
                }

                buf.append( hex );
            }

            return buf.toString();
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public static void deleteResource( IResource resource ) throws CoreException
    {
        if( resource == null || !resource.exists() )
        {
            return;
        }

        resource.delete( true, null );
    }

    public static boolean empty( Object[] array )
    {
        return isNullOrEmpty( array );
    }

    public static boolean empty( String val )
    {
        return isNullOrEmpty( val );
    }

    public static IProject[] getAllProjects()
    {
        return ResourcesPlugin.getWorkspace().getRoot().getProjects();
    }

    /**
     * @param project
     * @return
     */
    public static IClasspathEntry[] getClasspathEntries( IProject project )
    {
        if( project != null )
        {
            IJavaProject javaProject = JavaCore.create( project );
            try
            {
                IClasspathEntry[] classPathEntries = javaProject.getRawClasspath();
                return classPathEntries;
            }
            catch( JavaModelException e )
            {
            }
        }
        return null;
    }

    public static IFolder getDefaultDocrootFolder( IProject project )
    {
        IFolder retval = null;

        final IWebProject webproject = LiferayCore.create( IWebProject.class, project );

        if( webproject != null )
        {
            retval = webproject.getDefaultDocrootFolder();
        }

        return retval;
    }

    public static IProject getLiferayProject( IResource resource )
    {
        IProject project = null;

        if( resource != null && resource.exists() )
        {
            project = resource.getProject();
        }

        if( project != null && project.exists() )
        {
            if( isLiferayProject( project ) )
            {
                return project;
            }
            else
            {
                final IProject[] projects = getAllProjects();

                for( IProject proj : projects )
                {
                    if( proj.getLocation() != null &&
                        project.getLocation().isPrefixOf( proj.getLocation() ) &&
                        isLiferayProject( proj ) )
                    {
                        return proj;
                    }
                }
            }
        }

        return null;

        /*IProject retval = null;

        if( resource == null || ! resource.exists() )
        {
            return retval;
        }

        if( CoreUtil.isLiferayProject( resource.getProject() ) )
        {
            retval = resource.getProject();
        }
        else
        {
            IContainer container = resource.getParent();

            while( container != null && container.exists() && container.getParent() != null )
            {
                IFile projectFile = container.getFile( new Path( ".project" ) );

                if( projectFile != null && projectFile.exists() )
                {
                    for( IProject project : getAllProjects() )
                    {
                        if( container.getLocation().equals( project.getLocation() ) )
                        {
                            if( CoreUtil.isLiferayProject( project ) )
                            {
                                retval = project;
                                break;
                            }
                        }
                    }

                    if( retval != null )
                    {
                        break;
                    }
                }

                container = container.getParent();
            }
        }

        return retval;*/
    }
    public static IProject getLiferayProject( String projectName )
    {
        return getLiferayProject( getProject( projectName ) );
    }

    public static Object getNewObject( Object[] oldObjects, Object[] newObjects )
    {
        if( oldObjects != null && newObjects != null && oldObjects.length < newObjects.length )
        {
            for( int i = 0; i < newObjects.length; i++ )
            {
                boolean found = false;
                Object object = newObjects[i];

                for( int j = 0; j < oldObjects.length; j++ )
                {
                    if( oldObjects[j] == object )
                    {
                        found = true;
                        break;
                    }
                }

                if( !found )
                {
                    return object;
                }
            }
        }

        if( oldObjects == null && newObjects != null && newObjects.length == 1 )
        {
            return newObjects[0];
        }

        return null;
    }

    public static IProject getProject( String projectName )
    {
        return getWorkspaceRoot().getProject( projectName );
    }

    public static IPath getResourceLocation( IResource resource )
    {
        IPath retval = null;

        if( resource != null )
        {
            retval = resource.getLocation();

            if( retval == null )
            {
                retval = resource.getRawLocation();
            }
        }

        return retval;
    }

    public final static List<IFolder> getSourceFolders( final IJavaProject project )
    {
        final List<IFolder> folders = new ArrayList<IFolder>();

        if( project != null )
        {
            final IClasspathEntry[] entries = project.readRawClasspath();

            for( final IClasspathEntry entry : entries )
            {
                if( entry.getEntryKind() == IClasspathEntry.CPE_SOURCE && entry.getPath().segmentCount() > 0 )
                {
                    IContainer container = null;

                    if( entry.getPath().segmentCount() == 1 )
                    {
                        container = ResourcesPlugin.getWorkspace().getRoot().getProject( entry.getPath().segment( 0 ) );
                    }
                    else
                    {
                        container = ResourcesPlugin.getWorkspace().getRoot().getFolder( entry.getPath() );
                    }

                    if( !folders.contains( container ) && container instanceof IFolder )
                    {
                        folders.add( (IFolder) container );
                    }
                }
            }
        }

        return folders;
    }

    public static IWorkspace getWorkspace()
    {
        return ResourcesPlugin.getWorkspace();
    }

    public static IWorkspaceRoot getWorkspaceRoot()
    {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    public static Object invoke( String methodName, Object object, Class<?>[] argTypes, Object[] args )
        throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
        InvocationTargetException
    {
        Method method = object.getClass().getDeclaredMethod( methodName, argTypes );
        method.setAccessible( true );

        return method.invoke( object, args );
    }

    public static boolean isBundleOnlyProject( IProject project )
    {
        final ILiferayProject liferayProject = LiferayCore.create( project );

       return liferayProject instanceof IBundleProject && !( liferayProject instanceof IWebProject );
    }

    public static boolean isEqual( Object object1, Object object2 )
    {
        return object1 != null && object2 != null && object1.equals( object2 );
    }

    public static boolean isLiferayProject( IProject project )
    {
        if( project != null )
        {
            ILiferayProject lrproject = LiferayCore.create( project );

            return lrproject != null && !( lrproject instanceof NoopLiferayProject );
        }

        return false;
    }

    public static boolean isLinux()
    {
        return Platform.OS_LINUX.equals( Platform.getOS() );
    }

    public static boolean isMac()
    {
        return Platform.OS_MACOSX.equals( Platform.getOS() );
    }

    public static boolean isNotNullOrEmpty( Object[] array )
    {
        return !isNullOrEmpty( array );
    }

    public static boolean isNullOrEmpty( List<?> list )
    {
        return list == null || list.size() == 0;
    }

    public static boolean isNullOrEmpty( Object[] array )
    {
        return array == null || array.length == 0;
    }

    public static boolean isNullOrEmpty( String val )
    {
        return val == null || val.equals( StringPool.EMPTY ) || val.trim().equals( StringPool.EMPTY );
    }

    public static boolean isNumeric( String str )
    {
        try
        {
            Double.parseDouble( str );
        }
        catch( NumberFormatException nfe )
        {
            return false;
        }

        return true;
    }

    public static boolean isWindows()
    {
        return Platform.OS_WIN32.equals( Platform.getOS() );
    }

    public static void makeFolders( IFolder folder ) throws CoreException
    {
        if( folder == null )
        {
            return;
        }

        IContainer parent = folder.getParent();

        if( parent instanceof IFolder )
        {
            makeFolders( (IFolder) parent );
        }

        if( !folder.exists() )
        {
            folder.create( true, true, null );
        }

    }

    public static IProgressMonitor newSubMonitor( final IProgressMonitor parent, final int ticks )
    {
        return( parent == null ? null : new SubProgressMonitor( parent, ticks ) );
    }

    public static void prepareFolder( IFolder folder ) throws CoreException
    {
        IContainer parent = folder.getParent();

        if( parent instanceof IFolder )
        {
            prepareFolder( (IFolder) parent );
        }

        if( !folder.exists() )
        {
            folder.create( IResource.FORCE, true, null );
        }
    }

    public static String readPropertyFileValue( File propertiesFile, String key ) throws IOException
    {
        try(FileInputStream fis = new FileInputStream( propertiesFile ))
        {
            Properties props = new Properties();
            props.load( fis );

            return props.getProperty( key );
        }
    }

    public static String readStreamToString( InputStream contents ) throws IOException
    {
        return readStreamToString( contents, true );
    }

    public static String readStreamToString( InputStream contents, boolean closeStream ) throws IOException
    {
        if( contents == null )
        {
            return null;
        }

        final char[] buffer = new char[0x10000];

        StringBuilder out = new StringBuilder();

        Reader in = new InputStreamReader( contents, "UTF-8" ); //$NON-NLS-1$

        int read;
        do
        {
            read = in.read( buffer, 0, buffer.length );
            if( read > 0 )
            {
                out.append( buffer, 0, read );
            }
        }
        while( read >= 0 );

        if( closeStream )
        {
            contents.close();
        }

        return out.toString();
    }

    public static Version readVersionFile( File versionInfoFile )
    {
        String versionContents = FileUtil.readContents( versionInfoFile );

        if( CoreUtil.isNullOrEmpty( versionContents ) )
        {
            return Version.emptyVersion;
        }

        Version version = null;;

        try
        {
            version = Version.parseVersion( versionContents.trim() );
        }
        catch( NumberFormatException e )
        {
            version = Version.emptyVersion;
        }

        return version;
    }

    public static void removeChildren( Node node )
    {
        if( node != null )
        {
            while( node.hasChildNodes() )
            {
                node.removeChild( node.getFirstChild() );
            }
        }
    }

    public static void writeStreamFromString( String contents, OutputStream outputStream ) throws IOException
    {
        if( contents == null )
        {
            return;
        }

        final char[] buffer = new char[0x10000];

        Reader in = new InputStreamReader( new ByteArrayInputStream( contents.getBytes( "UTF-8" ) ) );  //$NON-NLS-1$

        Writer out = new OutputStreamWriter( outputStream, "UTF-8" ); //$NON-NLS-1$

        int read;
        do
        {
            read = in.read( buffer, 0, buffer.length );

            if( read > 0 )
            {
                out.write( buffer, 0, read );
            }
        }
        while( read >= 0 );

        in.close();
        out.flush();
        out.close();
    }

}
