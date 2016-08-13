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

package com.liferay.ide.project.ui.handlers;

import com.liferay.ide.project.core.ProjectCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Lovett Li
 * @author Terry Jia
 */
public abstract class AbstractCompareFileHandler extends AbstractHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked( event );
        final ISelection selection = HandlerUtil.getActiveMenuSelection( event );

        IStatus retval = Status.OK_STATUS;
        IFile currentFile = null;

        if( selection instanceof ITreeSelection )
        {
            Object firstElement = ( (ITreeSelection) selection ).getFirstElement();

            if( firstElement instanceof IFile )
            {
                currentFile = (IFile) firstElement;
            }
        }
        else if( selection instanceof TextSelection )
        {
            IEditorPart editor = window.getActivePage().getActiveEditor();
            currentFile = editor.getEditorInput().getAdapter( IFile.class );
        }

        retval = openCompareEditor( window, currentFile );

        return retval;
    }

    private IStatus openCompareEditor( IWorkbenchWindow window, IFile currentFile )
    {
        final IWorkbenchPage workBenchPage = window.getActivePage();

        ITypedElement left = null;
        ITypedElement right = null;
        IStatus retval = Status.OK_STATUS;

        try
        {
            File tempFile = getTemplateFile( currentFile );

            if( tempFile == null )
            {
                return ProjectCore.createErrorStatus( "Can't find the original file." );
            }

            left = new CompareItem( tempFile );
            right = new CompareItem( currentFile.getLocation().toFile() );

            openInCompare( left, right, workBenchPage );
        }
        catch( Exception e )
        {
            retval = ProjectCore.createErrorStatus( e );
        }

        return retval;
    }

    private void openInCompare( final ITypedElement left, final ITypedElement right, IWorkbenchPage workBenchPage )
    {
        final CompareConfiguration configuration = new CompareConfiguration();
        configuration.setLeftLabel( "Template" );
        configuration.setRightLabel( ( (CompareItem) right ).getFile().getAbsolutePath() );

        CompareUI.openCompareEditor( new CompareEditorInput( configuration )
        {

            @Override
            protected Object prepareInput( final IProgressMonitor monitor )
                throws InvocationTargetException, InterruptedException
            {
                DiffNode diffNode = new DiffNode( left, right );
                return diffNode;
            }
        } );
    }

    protected abstract File getTemplateFile( IFile currentFile ) throws Exception;

    private class CompareItem implements ITypedElement, IStreamContentAccessor, IModificationDate, IEditableContent
    {

        private File file;

        public CompareItem( File file )
        {
            this.file = file;
        }

        public File getFile()
        {
            return file;
        }

        @Override
        public String getName()
        {
            return null;
        }

        @Override
        public Image getImage()
        {
            return null;
        }

        @Override
        public String getType()
        {
            return null;
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
            catch( FileNotFoundException e )
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
