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
package com.liferay.ide.core.tests;

import com.liferay.ide.core.util.FileUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;

import junit.framework.TestCase;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class TestUtil
{

    public static void copyDir( File src, File dst ) throws IOException
    {
        copyDir( src, dst, true );
    }

    private static void copyDir( File src, File dst, boolean deleteDst ) throws IOException
    {
        if( !src.isDirectory() )
        {
            throw new IllegalArgumentException( "Not a directory:" + src.getAbsolutePath() );
        }

        if( deleteDst )
        {
            FileUtil.deleteDir( dst, true );
        }

        dst.mkdirs();

        File[] files = src.listFiles();

        if( files != null )
        {
            for( int i = 0; i < files.length; i++ )
            {
                File file = files[i];

                if( file.canRead() )
                {
                    File dstChild = new File( dst, file.getName() );

                    if( file.isDirectory() )
                    {
                        copyDir( file, dstChild, false );
                    }
                    else
                    {
                        copyFile( file, dstChild );
                    }
                }
            }
        }
    }

    private static void copyFile( File src, File dst ) throws IOException
    {
        BufferedInputStream in = new BufferedInputStream( Files.newInputStream( src.toPath() ) );
        BufferedOutputStream out = new BufferedOutputStream( Files.newOutputStream( dst.toPath() ) );

        byte[] buf = new byte[10240];
        int len;
        while( ( len = in.read( buf ) ) != -1 )
        {
            out.write( buf, 0, len );
        }

        out.close();
        in.close();
    }

    public static void waitForBuildAndValidation() throws Exception
    {
        IWorkspaceRoot root = null;

        try
        {
            ResourcesPlugin.getWorkspace().checkpoint( true );
            Job.getJobManager().join( ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor() );
            Job.getJobManager().join( ResourcesPlugin.FAMILY_MANUAL_BUILD, new NullProgressMonitor() );
            Job.getJobManager().join( ValidatorManager.VALIDATOR_JOB_FAMILY, new NullProgressMonitor() );
            Job.getJobManager().join( ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor() );
            Thread.sleep( 200 );
            Job.getJobManager().beginRule( root = ResourcesPlugin.getWorkspace().getRoot(), null );
        }
        catch( InterruptedException e )
        {
            failTest( e );
        }
        catch( IllegalArgumentException e )
        {
            failTest( e );
        }
        catch( OperationCanceledException e )
        {
            failTest( e );
        }
        finally
        {
            if( root != null )
            {
                Job.getJobManager().endRule( root );
            }
        }
    }

    public static void failTest( Exception e )
    {
        StringWriter s = new StringWriter();
        e.printStackTrace( new PrintWriter( s ) );
        TestCase.fail( s.toString() );
    }

}
