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

package com.liferay.ide.core.describer;

import com.liferay.ide.core.util.CoreUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;

/**
 * @author Kuo Zhang
 */
public class LiferayLanguageFileDescriber implements ITextContentDescriber
{

    public LiferayLanguageFileDescriber()
    {
        super();
    }

    @SuppressWarnings( "restriction" )
    public int describe( InputStream contents, IContentDescription description ) throws IOException
    {
        int retval = INVALID;

        try
        {
            final Field inputStreamField = contents.getClass().getDeclaredField( "in" );

            inputStreamField.setAccessible( true );

            final InputStream inputStream = (InputStream) inputStreamField.get( contents );

            final Field fileStoreField = inputStream.getClass().getDeclaredField( "target" );

            fileStoreField.setAccessible( true );

            final IFileStore fileStore = (IFileStore) fileStoreField.get( inputStream );

            if( fileStore != null && CoreUtil.isValidLiferayLanguageFileName( fileStore.fetchInfo().getName() ) )
            {
                final IFile iFile =
                    ResourcesPlugin.getWorkspace().getRoot().getFileForLocation( FileUtil.toPath( fileStore.toURI() ) );

                if( iFile != null && iFile.getProject() != null && CoreUtil.isLiferayProject( iFile.getProject() ) )
                {
                    retval = VALID;
                }
            }

        }
        catch( Exception e )
        {

        }

        return retval;
    }

    public QualifiedName[] getSupportedOptions()
    {
        return null;
    }

    public int describe( Reader contents, IContentDescription description ) throws IOException
    {
        return INVALID;
    }

}
