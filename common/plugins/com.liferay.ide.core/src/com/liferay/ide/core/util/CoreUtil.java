/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.LiferayCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.osgi.framework.Version;
import org.w3c.dom.Node;

/**
 * Core Utility methods
 *
 * @author Gregory Amerson
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
        if( project != null )
        {
            IVirtualFolder webappRoot = getDocroot( project );

            if( webappRoot != null )
            {
                for( IContainer container : webappRoot.getUnderlyingFolders() )
                {
                    if( container != null && container.exists() )
                    {
                        if( container.getFolder( new Path( "WEB-INF" ) ).exists() ) //$NON-NLS-1$
                        {
                            return container instanceof IFolder ? (IFolder) container : null;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static IVirtualFolder getDocroot( IProject project )
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

    public static IVirtualFolder getDocroot( String projectName )
    {
        IProject project = getProject( projectName );

        return getDocroot( project );
    }

    public static IFile getDocrootFile( IProject project, String filePath )
    {
        IFile retval = null;

        if( project != null && !CoreUtil.isNullOrEmpty( filePath ) )
        {
            IVirtualFolder webappRoot = getDocroot( project );

            if( webappRoot != null )
            {
                IVirtualFile file = webappRoot.getFile( filePath );

                if( file != null && file.exists() )
                {
                    retval = file.getUnderlyingFile();
                }
            }
        }

        return retval;
    }

    public static IFolder getFirstSrcFolder( IProject project )
    {
        @SuppressWarnings( "deprecation" )
        IPackageFragmentRoot[] sourceFolders = J2EEProjectUtilities.getSourceContainers( project );

        if( sourceFolders != null && sourceFolders.length > 0 )
        {
            IResource resource = sourceFolders[0].getResource();

            return resource instanceof IFolder ? (IFolder) resource : null;
        }

        return null;
    }

    public static IFolder getFirstSrcFolder( String projectName )
    {
        IProject project = getProject( projectName );

        return getFirstSrcFolder( project );
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

    public static boolean isEqual( Object object1, Object object2 )
    {
        return object1 != null && object2 != null && object1.equals( object2 );
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

    public static boolean isResourceInDocroot( IModuleResource resource )
    {
        IFile file = (IFile) resource.getAdapter( IFile.class );

        if( file != null )
        {
            IVirtualFolder webappRoot = getDocroot( file.getProject() );

            if( webappRoot != null )
            {
                for( IContainer container : webappRoot.getUnderlyingFolders() )
                {
                    return container != null && container.exists() &&
                        container.getFullPath().isPrefixOf( file.getFullPath() );
                }
            }
        }

        return false;
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

    public static String readPropertyFileValue( File propertiesFile, String key ) throws FileNotFoundException,
        IOException
    {
        Properties props = new Properties();
        props.load( new FileInputStream( propertiesFile ) );
        return props.getProperty( key );
    }

    public static String readStreamToString( InputStream contents ) throws IOException
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
}
