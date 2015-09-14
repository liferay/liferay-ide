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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author Gregory Amerson
 */
public class TestUtil
{

    public static void copyDir( File src, File dst ) throws IOException
    {
        copyDir( src, dst, true );
    }

    public static void copyDir( File src, File dst, boolean deleteDst ) throws IOException
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
        BufferedInputStream in = new BufferedInputStream( new FileInputStream( src ) );
        BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( dst ) );

        byte[] buf = new byte[10240];
        int len;
        while( ( len = in.read( buf ) ) != -1 )
        {
            out.write( buf, 0, len );
        }

        out.close();
        in.close();
    }

}
