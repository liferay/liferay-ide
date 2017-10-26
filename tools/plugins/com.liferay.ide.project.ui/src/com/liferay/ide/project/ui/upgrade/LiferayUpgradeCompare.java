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
package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.project.ui.ProjectUI;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Simon Jiang
 */
public class LiferayUpgradeCompare
{
    
    private final IPath soruceFile;
    private final IPath targetFile;
    private String fileName;
    
    public LiferayUpgradeCompare( final IPath soruceFile, final IPath targetFile, String fileName )
    {
        this.fileName = fileName;
        this.soruceFile = soruceFile;
        this.targetFile = targetFile;
    }

    protected File getSourceFile()
    {
        return soruceFile.toFile();
    }

    protected File getTargetFile()
    {
        return targetFile.toFile();
    }

    public void openCompareEditor()
    {
        try
        {
            final ITypedElement left = new CompareItem( getSourceFile(), this.fileName );
            final ITypedElement right = new CompareItem( getTargetFile(), this.fileName + "_preview" );

            final CompareConfiguration configuration = new CompareConfiguration();
            configuration.setLeftLabel( "Original File" );
            configuration.setRightLabel( "Upgraded File" );

            CompareEditorInput editorInput = new CompareEditorInput( configuration )
            {

                @Override
                protected Object prepareInput( final IProgressMonitor monitor )
                    throws InvocationTargetException, InterruptedException
                {
                    DiffNode diffNode = new DiffNode( left, right );
                    return diffNode;
                }
            };

            editorInput.setTitle(
                "Compare ('" + soruceFile.toPortableString() + "'-'" + targetFile.toPortableString() + "')" );

            CompareUI.openCompareEditor( editorInput );
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }
    }

    private class CompareItem implements ITypedElement,IStreamContentAccessor,IModificationDate,IEditableContent//,IResourceProvider
    {
        private File file;
        private String name;

        public CompareItem( File file, String name )
        {
            this.file = file;
            this.name = name;
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public Image getImage()
        {
            return null;
        }

        @Override
        public String getType()
        {
            return TEXT_TYPE;
        }

        @Override
        public long getModificationDate()
        {
            return 0;
        }

        @Override
        public InputStream getContents() throws CoreException
        {
            try
            {
                return Files.newInputStream( file.toPath() );
            }
            catch( Exception e )
            {
            }
            return null;
        }

        @Override
        public boolean isEditable()
        {
            return false;
        }

        @Override
        public void setContent( byte[] newContent )
        {
        }

        @Override
        public ITypedElement replace( ITypedElement dest, ITypedElement src )
        {
            return null;
        }
    }
}
