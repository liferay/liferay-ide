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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.core.util;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.StringBufferOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.util.NLS;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

/**
 * @author Greg Amerson
 */
public class FileUtil
{

    public static void clearContents( File versionFile )
    {
        if( versionFile != null && versionFile.exists() )
        {
            try
            {
                RandomAccessFile file = new RandomAccessFile( versionFile, "rw" ); //$NON-NLS-1$
                file.setLength( 0 );
                file.close();
            }
            catch( Exception ex )
            {
                ex.printStackTrace();
            }
        }
    }

    public static void copyFileToDir( File file, File dir )
    {
        if( file == null || ( !file.exists() ) || dir == null || ( !dir.exists() ) || ( !dir.isDirectory() ) )
        {
            return;
        }

        byte[] buf = new byte[4096];

        OutputStream out = null;
        FileInputStream in = null;

        try
        {
            out = new FileOutputStream( new File( dir, file.getName() ) );
            in = new FileInputStream( file );

            int avail = in.read( buf );
            while( avail > 0 )
            {
                out.write( buf, 0, avail );
                avail = in.read( buf );
            }
        }
        catch( Exception e )
        {
            LiferayCore.logError( "Unable to copy file " + file.getName() + " to " + dir.getAbsolutePath() ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        finally
        {
            try
            {
                if( in != null )
                    in.close();
            }
            catch( Exception ex )
            {
                // ignore
            }
            try
            {
                if( out != null )
                    out.close();
            }
            catch( Exception ex )
            {
                // ignore
            }
        }
    }

    public static void deleteDir( File directory, boolean removeAll )
    {
        if( directory == null || !directory.isDirectory() )
        {
            return;
        }

        for( File file : directory.listFiles() )
        {
            if( file.isDirectory() && removeAll )
            {
                deleteDir( file, removeAll );
            }
            else
            {
                file.delete();
            }
        }

        directory.delete();
    }

    public static void deleteDirContents( final File directory )
    {
        if( directory == null || !directory.isDirectory() )
        {
            return;
        }

        for( File file : directory.listFiles() )
        {
            if( file.isDirectory() )
            {
                deleteDir( file, true );
            }
            else
            {
                file.delete();
            }
        }

    }

    public static String readContents( File file )
    {
        return readContents( file, false );
    }

    public static String readContents( File file, boolean includeNewlines )
    {
        if( file == null )
        {
            return null;
        }

        if( !file.exists() )
        {
            return null;
        }

        StringBuffer contents = new StringBuffer();
        BufferedReader bufferedReader = null;
        
        try
        {
            FileReader fileReader = new FileReader( file );

            bufferedReader = new BufferedReader( fileReader );

            String line;

            while( ( line = bufferedReader.readLine() ) != null )
            {
                contents.append( line );

                if( includeNewlines )
                {
                    contents.append( "\n" ); //$NON-NLS-1$
                }
            }
        }
        catch( Exception e )
        {
            LiferayCore.logError( "Could not read file: " + file.getPath() ); //$NON-NLS-1$
        }
        finally
        {
            if( bufferedReader != null )
            {
                try
                {
                    bufferedReader.close();
                }
                catch( IOException e )
                {
                    // best effort no need to log
                }
            }
        }

        return contents.toString();
    }

    public static String readContents( InputStream contents ) throws IOException
    {
        byte[] buffer = new byte[4096];

        BufferedInputStream bin = new BufferedInputStream( contents );
        StringBufferOutputStream out = new StringBufferOutputStream();

        int bytesRead = 0;
//        int bytesTotal = 0;

        // Keep reading from the file while there is any content
        // when the end of the stream has been reached, -1 is returned
        while( ( bytesRead = bin.read( buffer ) ) != -1 )
        {
            out.write( buffer, 0, bytesRead );
//            bytesTotal += bytesRead;
        }

        if( bin != null )
        {
            bin.close();
        }

        if( out != null )
        {
            out.flush();
            out.close();
        }

        return out.toString();
    }

    public static String[] readLinesFromFile( File file )
    {
        if( file == null )
        {
            return null;
        }

        if( !file.exists() )
        {
            return null;
        }

        List<String> lines = new ArrayList<String>();
        BufferedReader bufferedReader = null;
        
        try
        {
            FileReader fileReader = new FileReader( file );

            bufferedReader = new BufferedReader( fileReader );

            String line;

            while( ( line = bufferedReader.readLine() ) != null )
            {
                lines.add( line );
            }
        }
        catch( Exception e )
        {
            LiferayCore.logError( "Could not read file: " + file.getPath() ); //$NON-NLS-1$
        }
        finally
        {
            if( bufferedReader != null )
            {
                try
                {
                    bufferedReader.close();
                }
                catch( Exception e )
                {
                    // no need to log, best effort
                }
            }
        }

        return lines.toArray( new String[lines.size()] );
    }

    public static Document readXML( InputStream inputStream, EntityResolver resolver, ErrorHandler error )
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;

        try
        {
            db = dbf.newDocumentBuilder();

            if( resolver != null )
            {
                db.setEntityResolver( resolver );
            }

            if( error != null )
            {
                db.setErrorHandler( error );
            }

            return db.parse( inputStream );
        }
        catch( Throwable t )
        {
            return null;
        }
    }

    public static Document readXML( String content )
    {
        return readXML( new ByteArrayInputStream( content.getBytes() ), null, null );
    }

    public static Document readXMLFile( File file )
    {
        return readXMLFile( file, null );
    }

    public static Document readXMLFile( File file, EntityResolver resolver )
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;

        try
        {
            db = dbf.newDocumentBuilder();

            if( resolver != null )
            {
                db.setEntityResolver( resolver );
            }

            return db.parse( file );
        }
        catch( Throwable t )
        {
            return null;
        }
    }

    public static String validateNewFolder( IFolder folder, String folderValue )
    {
        if( folder == null || folderValue == null )
        {
            return null;
        }

        if( CoreUtil.isNullOrEmpty( folderValue ) )
        {
            return Msgs.folderValueNotEmpty;
        }

        if( !Path.ROOT.isValidPath( folderValue ) )
        {
            return Msgs.folderValueInvalid;
        }

        IWorkspace workspace = ResourcesPlugin.getWorkspace();

        IStatus result =
            workspace.validatePath( folder.getFolder( folderValue ).getFullPath().toString(), IResource.FOLDER );

        if( !result.isOK() )
        {
            return result.getMessage();
        }

        if( folder.getFolder( new Path( folderValue ) ).exists() )
        {
            return Msgs.folderAlreadyExists;
        }

        return null;
    }

    public static int writeFileFromStream( File tempFile, InputStream in ) throws IOException
    {
        byte[] buffer = new byte[1024];

        BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( tempFile ) );
        BufferedInputStream bin = new BufferedInputStream( in );

        int bytesRead = 0;
        int bytesTotal = 0;

        // Keep reading from the file while there is any content
        // when the end of the stream has been reached, -1 is returned
        while( ( bytesRead = bin.read( buffer ) ) != -1 )
        {
            out.write( buffer, 0, bytesRead );
            bytesTotal += bytesRead;
        }

        if( bin != null )
        {
            bin.close();
        }

        if( out != null )
        {
            out.flush();
            out.close();
        }

        return bytesTotal;
    }

    private static class Msgs extends NLS
    {
        public static String folderAlreadyExists;
        public static String folderValueInvalid;
        public static String folderValueNotEmpty;

        static
        {
            initializeMessages( FileUtil.class.getName(), Msgs.class );
        }
    }
}
