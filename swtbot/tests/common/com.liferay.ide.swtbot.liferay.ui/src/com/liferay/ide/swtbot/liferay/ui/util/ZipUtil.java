/******************************************************************************
 * Copyright (c) 2008 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package com.liferay.ide.swtbot.liferay.ui.util;

import com.liferay.ide.swtbot.ui.util.StringPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.util.NLS;

/**
 * Contains a series of static utility methods for working with zip archives.
 *
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class ZipUtil
{

    private static class Resources

        extends NLS

    {

        public static String progressUnzipped;
        public static String progressUnzipping;
        static
        {
            initializeMessages( ZipUtil.class.getName(), Resources.class );
        }
    }

    private static void delete( File f )

        throws IOException

    {
        if( f.isDirectory() )
        {
            for( File child : f.listFiles() )
            {
                delete( child );
            }
        }

        if( !f.delete() )
        {
            String msg = "Could not delete " + f.getPath() + "."; //$NON-NLS-1$ //$NON-NLS-2$
            throw new IOException( msg );
        }
    }

    public static String getFirstZipEntryName( File zipFile ) throws Exception
    {
        ZipFile zip = new ZipFile( zipFile );
        String name = zip.entries().nextElement().getName();
        zip.close();

        return name;
    }

    public static ZipEntry getZipEntry( ZipFile zip, String name )
    {
        String lcasename = name.toLowerCase();

        for( Enumeration<? extends ZipEntry> itr = zip.entries(); itr.hasMoreElements(); )
        {
            ZipEntry zipentry = itr.nextElement();

            if( zipentry.getName().toLowerCase().equals( lcasename ) )
            {
                return zipentry;
            }
        }

        return null;
    }

    public static ZipFile open( File file )

        throws IOException

    {
        try
        {
            return new ZipFile( file );
        }
        catch( FileNotFoundException e )
        {
            FileNotFoundException fnfe = new FileNotFoundException( file.getAbsolutePath() );

            fnfe.initCause( e );

            throw fnfe;
        }
    }

    public static void unzip( File file, File destdir )

        throws IOException

    {
        unzip( file, destdir, new NullProgressMonitor() );
    }

    public static void unzip( File file, File destdir, IProgressMonitor monitor ) throws IOException
    {
        unzip( file, null, destdir, monitor );
    }

    public static void unzip(
        File file, String entryToStart, File destdir, IProgressMonitor monitor )
        throws IOException
    {
        ZipFile zip = open( file );

        try
        {
            Enumeration<? extends ZipEntry> entries = zip.entries();

            int totalWork = zip.size();
            monitor.beginTask( Resources.progressUnzipping, totalWork );

            int c = 0;
            boolean foundStartEntry = entryToStart == null;

            while( entries.hasMoreElements() )
            {
                ZipEntry entry = entries.nextElement();

                if( !foundStartEntry )
                {
                    foundStartEntry = entryToStart.equals( entry.getName() );
                    continue;
                }

                monitor.worked( 1 );

                String taskMsg =
                    NLS.bind( Resources.progressUnzipped, new Object[] { file.getName(), c++, totalWork } );
                monitor.subTask( taskMsg );

                if( entry.isDirectory() )
                    continue;

                String entryName = null;

                if( entryToStart == null )
                {
                    entryName = entry.getName();
                }
                else
                {
                    entryName = entry.getName().replaceFirst( entryToStart, StringPool.BLANK );
                }

                File f = new File( destdir, entryName );
                File dir = f.getParentFile();

                if( !dir.exists() && !dir.mkdirs() )
                {
                    String msg = "Could not create dir: " + dir.getPath(); //$NON-NLS-1$
                    throw new IOException( msg );
                }

                InputStream in = null;
                FileOutputStream out = null;

                try
                {
                    in = zip.getInputStream( entry );
                    out = new FileOutputStream( f );

                    byte[] bytes = new byte[1024];
                    int count = in.read( bytes );

                    while( count != -1 )
                    {
                        out.write( bytes, 0, count );
                        count = in.read( bytes );
                    }

                    out.flush();
                }
                finally
                {
                    if( in != null )
                    {
                        try
                        {
                            in.close();
                        }
                        catch( IOException e )
                        {
                        }
                    }

                    if( out != null )
                    {
                        try
                        {
                            out.close();
                        }
                        catch( IOException e )
                        {
                        }
                    }
                }
            }
        }
        finally
        {
            try
            {
                zip.close();
            }
            catch( IOException e )
            {
            }
        }
    }

    public static void zip( File dir, File target ) throws IOException
    {

        zip( dir, null, target );
    }

    public static void zip( File dir, FilenameFilter filenameFilter, File target ) throws IOException
    {

        if( target.exists() )
        {
            delete( target );
        }

        ZipOutputStream zip = new ZipOutputStream( new FileOutputStream( target ) );

        try
        {
            zipDir( target, zip, dir, filenameFilter, StringPool.BLANK ); // $NON-NLS-1$
        }
        finally
        {
            try
            {
                zip.close();
            }
            catch( IOException e )
            {
            }
        }
    }

    private static void zipDir(
        File target, ZipOutputStream zip, File dir, FilenameFilter filter, String path )

        throws IOException

    {
        for( File f : filter != null ? dir.listFiles( filter ) : dir.listFiles() )
        {
            String cpath = path + f.getName();

            if( f.isDirectory() )
            {
                zipDir( target, zip, f, filter, cpath + "/" ); //$NON-NLS-1$
            }
            else
            {
                zipFile( target, zip, f, cpath );
            }
        }
    }

    private static void zipFile( File target, ZipOutputStream zip, File file, String path )

        throws IOException

    {
        if( !file.equals( target ) )
        {
            ZipEntry ze = new ZipEntry( path );

            ze.setTime( file.lastModified() + 1999 );
            ze.setMethod( ZipEntry.DEFLATED );

            zip.putNextEntry( ze );

            FileInputStream in = new FileInputStream( file );

            try
            {
                int bufsize = 8 * 1024;
                long flength = file.length();

                if( flength == 0 )
                {
                    return;
                }
                else if( flength < bufsize )
                {
                    bufsize = (int) flength;
                }

                byte[] buffer = new byte[bufsize];
                int count = in.read( buffer );

                while( count != -1 )
                {
                    zip.write( buffer, 0, count );
                    count = in.read( buffer );
                }
            }
            finally
            {
                try
                {
                    in.close();
                }
                catch( IOException e )
                {
                }
            }
        }
    }

    /**
     * This class is a container for static methods and is not meant to be instantiated.
     */

    private ZipUtil()
    {
    }

}
