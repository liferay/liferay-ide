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

package com.liferay.ide.ui.wizard;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.viewsupport.FilteredElementTreeSelectionDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class ExternalFileSelectionDialog extends FilteredElementTreeSelectionDialog
{

    protected static class FileContentProvider implements ITreeContentProvider
    {
        private final Object[] EMPTY = new Object[0];

        public void dispose()
        {
        }

        public Object[] getChildren( Object parentElement )
        {
            if( parentElement instanceof File )
            {
                File[] children = ( (File) parentElement ).listFiles();

                if( children != null )
                {
                    return children;
                }
            }

            return EMPTY;
        }

        public Object[] getElements( Object element )
        {
            return getChildren( element );
        }

        public Object getParent( Object element )
        {
            if( element instanceof File )
            {
                return ( (File) element ).getParentFile();
            }

            return null;
        }

        public boolean hasChildren( Object element )
        {
            return getChildren( element ).length > 0;
        }

        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }

    }

    protected static class FileLabelProvider extends LabelProvider
    {
        private final Image IMG_FILE =
            PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_FILE );

        private final Image IMG_FOLDER = PlatformUI.getWorkbench().getSharedImages().getImage(
            ISharedImages.IMG_OBJ_FOLDER );

        public Image getImage( Object element )
        {
            if( element instanceof File )
            {
                File curr = (File) element;

                if( curr.isDirectory() )
                {
                    return IMG_FOLDER;
                }
                else
                {
                    return IMG_FILE;
                }
            }

            return null;
        }

        public String getText( Object element )
        {
            if( element instanceof File )
            {
                return TextProcessor.process( ( (File) element ).getName() );
            }

            return super.getText( element );
        }
    }

    protected static class FileSelectionValidator implements ISelectionStatusValidator
    {
        protected boolean fAcceptFolders;
        protected boolean fMultiSelect;

        public FileSelectionValidator( boolean multiSelect, boolean acceptFolders )
        {
            fMultiSelect = multiSelect;
            fAcceptFolders = acceptFolders;
        }

        public IStatus validate( Object[] selection )
        {
            int nSelected = selection.length;

            if( nSelected == 0 || ( nSelected > 1 && !fMultiSelect ) )
            {
                return new StatusInfo( IStatus.ERROR, "" ); //$NON-NLS-1$
            }

            for( int i = 0; i < selection.length; i++ )
            {
                Object curr = selection[i];

                if( curr instanceof File )
                {
                    File file = (File) curr;

                    if( !fAcceptFolders && !file.isFile() )
                    {
                        return new StatusInfo( IStatus.ERROR, "" ); //$NON-NLS-1$
                    }
                }
            }

            return new StatusInfo();
        }
    }

    protected static class FileViewerComparator extends ViewerComparator
    {
        public int category( Object element )
        {
            if( element instanceof File )
            {
                if( ( (File) element ).isFile() )
                {
                    return 1;
                }
            }

            return 0;
        }
    }

    public ExternalFileSelectionDialog( Shell parent, ViewerFilter filter, boolean multiSelect, boolean acceptFolders )
    {
        super( parent, new FileLabelProvider(), new FileContentProvider(), true );

        setComparator( new FileViewerComparator() );

        addFilter( filter );

        setValidator( new FileSelectionValidator( multiSelect, acceptFolders ) );

        setHelpAvailable( false );
    }

    @Override
    protected TreeViewer doCreateTreeViewer( Composite parent, int style )
    {
        TreeViewer viewer = super.doCreateTreeViewer( parent, style );

        viewer.setAutoExpandLevel( 5 );

        return viewer;
    }

}
