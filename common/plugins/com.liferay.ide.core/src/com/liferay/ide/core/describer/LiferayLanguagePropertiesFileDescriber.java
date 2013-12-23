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

import com.liferay.ide.core.util.PropertiesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;

/**
 * @author Kuo Zhang
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayLanguagePropertiesFileDescriber implements ITextContentDescriber
{

    public LiferayLanguagePropertiesFileDescriber()
    {
        super();
    }

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

            if( fileStore != null )
            {
                final IFile file =
                    ResourcesPlugin.getWorkspace().getRoot().getFileForLocation( FileUtil.toPath( fileStore.toURI() ) );

                if( PropertiesUtil.isLanguagePropertiesFile( file ) )
                {
                    return VALID;
                }
            }
        }
        catch( Exception e )
        {
            // ignore errors
        }

        return retval;
    }

    public int describe( Reader contents, IContentDescription description ) throws IOException
    {
        try
        {
            final Field documentReaderField = contents.getClass().getDeclaredField( "in" );

            documentReaderField.setAccessible( true );

            final Object documentReader = documentReaderField.get( contents );

            final Field fDocumentField = documentReader.getClass().getDeclaredField( "fDocument" );

            fDocumentField.setAccessible( true );

            final Object fDocument = fDocumentField.get( documentReader );

            final Field fDocumentListenersField = fDocument.getClass().getSuperclass().getSuperclass().getDeclaredField( "fDocumentListeners" );

            fDocumentListenersField.setAccessible( true );

            final ListenerList fDocumentListeners = (ListenerList) fDocumentListenersField.get( fDocument );

            final Object[] listeners = fDocumentListeners.getListeners();

            for( Object listener : listeners )
            {
                try
                {
                    final Field fFileField = listener.getClass().getEnclosingClass().getSuperclass().getDeclaredField( "fFile" );

                    fFileField.setAccessible( true );

                    // get enclosing instance of listener

                    final Field thisField = listener.getClass().getDeclaredField( "this$0" );

                    thisField.setAccessible( true );

                    Object enclosingObject = thisField.get( listener );

                    Object fFile = fFileField.get( enclosingObject );

                    if( fFile instanceof IFile )
                    {
                        IFile file = (IFile) fFile;

                        if( PropertiesUtil.isLanguagePropertiesFile( file ) )
                        {
                            return VALID;
                        }
                    }
                }
                catch( Exception e )
                {
                }
            }
        }
        catch ( Exception e )
        {
            // ignore errors
        }

        return INVALID;
    }

    public QualifiedName[] getSupportedOptions()
    {
        return null;
    }

}
