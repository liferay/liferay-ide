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
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;



/**
 * @author Simon Jiang
 */
public class LiferayUpgradeCompre
{
    
    private final IPath soruceFile;
    private final IPath targetFile;
    private IWorkbenchWindow window;
    private String fileName;
    
    public LiferayUpgradeCompre( final IPath soruceFile, final IPath targetFile, String fileName )
    {
        this.fileName = fileName;
        this.window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
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
        final IWorkbenchPage workBenchPage = window.getActivePage();

        ITypedElement left = null;
        ITypedElement right = null;

        try
        {
            left = new CompareItem( getSourceFile(), this.fileName  );
            right = new CompareItem( getTargetFile(), this.fileName + "_preview"   );
            openInCompare( left, right, workBenchPage );
        }
        
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }

    }

    private void openInCompare( final ITypedElement left, final ITypedElement right, IWorkbenchPage workBenchPage )
    {
        final CompareConfiguration configuration = new CompareConfiguration();
        configuration.setLeftLabel( "Source File" );
        configuration.setRightLabel( "Upgrade File" );

        CompareUI.openCompareEditor( new CompareEditorInput( configuration)
        {
            @Override
            protected Object prepareInput( final IProgressMonitor monitor )
                throws InvocationTargetException, InterruptedException
            {
                DiffNode diffNode = new DiffNode( left, right );
                return diffNode;
            }
        });
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
                return new FileInputStream( file );
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
