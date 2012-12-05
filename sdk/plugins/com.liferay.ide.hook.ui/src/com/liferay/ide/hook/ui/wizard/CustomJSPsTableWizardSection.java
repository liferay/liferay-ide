/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.hook.ui.wizard;

import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.ui.wizard.ExternalFileSelectionDialog;
import com.liferay.ide.ui.wizard.StringArrayTableWizardSection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
public class CustomJSPsTableWizardSection extends StringArrayTableWizardSection
{

    protected static class JSPFileViewerFilter extends ViewerFilter
    {
        protected File base;

        protected List<File> cachedDirs = new ArrayList<File>();

        protected String[] roots = null;

        protected IPath[] validRoots;

        public JSPFileViewerFilter( File base, String[] roots )
        {
            this.base = base;

            this.roots = roots;

            this.validRoots = new IPath[roots.length];

            for( int i = 0; i < roots.length; i++ )
            {
                File fileRoot = new File( base, roots[i] );

                if( fileRoot.exists() )
                {
                    validRoots[i] = new Path( fileRoot.getPath() );
                }
            }
        }

        @Override
        public boolean select( Viewer viewer, Object parent, Object element )
        {
            if( element instanceof File )
            {
                File file = (File) element;

                IPath filePath = new Path( file.getPath() );

                boolean validRootFound = false;

                for( IPath validRoot : validRoots )
                {
                    if( validRoot.isPrefixOf( filePath ) )
                    {
                        validRootFound = true;

                        break;
                    }
                }

                if( !validRootFound )
                {
                    return false;
                }

                if( cachedDirs.contains( file ) )
                {
                    return true;
                }
                else if( file.isDirectory() )
                {
                    // we only want to show the directory if it had children
                    // that have jsps
                    if( directoryContainsFiles( file, "jsp", viewer ) ) //$NON-NLS-1$
                    {
                        cachedDirs.add( file );

                        return true;
                    }
                }
                else
                {
                    if( filePath.getFileExtension().contains( "jsp" ) ) //$NON-NLS-1$
                    {
                        return true;
                    }
                }
            }

            return false;
        }

        protected boolean directoryContainsFiles( File dir, String ext, Viewer viewer )
        {
            try
            {
                List<File> files = FileListing.getFileListing( dir );

                for( File file : files )
                {
                    IPath filePath = new Path( file.getPath() );

                    if( filePath.getFileExtension() != null && filePath.getFileExtension().contains( ext ) )
                    {
                        return true;
                    }
                }
            }
            catch( FileNotFoundException e )
            {
                // do nothing
            }

            return false;
        }

    }

    protected Button addFromPortalButton;

    protected File portalDir;

    public CustomJSPsTableWizardSection(
        Composite parent, String componentLabel, String dialogTitle, String addButtonLabel, String editButtonLabel,
        String removeButtonLabel, String[] columnTitles, String[] fieldLabels, Image labelProviderImage,
        IDataModel model, String propertyName )
    {

        super( parent, componentLabel, dialogTitle, addButtonLabel, editButtonLabel, removeButtonLabel, columnTitles, fieldLabels, labelProviderImage, model, propertyName );
    }

    public void setPortalDir( File dir )
    {
        this.portalDir = dir;
    }

    @Override
    protected void addButtonsToButtonComposite(
        Composite buttonCompo, String addButtonLabel, String editButtonLabel, String removeButtonLabel )
    {

        addFromPortalButton = new Button( buttonCompo, SWT.PUSH );
        addFromPortalButton.setText( Msgs.addFromLiferay );
        addFromPortalButton.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_BEGINNING |
            GridData.HORIZONTAL_ALIGN_FILL ) );
        addFromPortalButton.addSelectionListener( new SelectionListener()
        {

            public void widgetDefaultSelected( SelectionEvent event )
            {
                // Do nothing
            }

            public void widgetSelected( SelectionEvent event )
            {
                handleAddFromPortalButtonSelected();
            }
        } );

        super.addButtonsToButtonComposite( buttonCompo, addButtonLabel, editButtonLabel, removeButtonLabel );
    }

    protected void handleAddFromPortalButtonSelected()
    {
        if( portalDir == null || !portalDir.exists() )
        {
            MessageDialog.openWarning( getShell(), Msgs.addJSP, Msgs.couldNotFindPortalRoot );

            return;
        }

        IPath rootPath = new Path( portalDir.getPath() );

        ExternalFileSelectionDialog dialog =
            new ExternalFileSelectionDialog(
                getShell(), new JSPFileViewerFilter( portalDir, new String[] { "html" } ), true, false ); //$NON-NLS-1$
        dialog.setTitle( Msgs.liferayCustomJSP );
        dialog.setMessage( Msgs.selectJSPToCustomize );
        dialog.setInput( portalDir );

        if( dialog.open() == Window.OK )
        {
            Object[] selected = dialog.getResult();

            for( int i = 0; i < selected.length; i++ )
            {
                IPath filePath = Path.fromOSString( ( (File) selected[i] ).getPath() );

                addStringArray( new String[] { "/" + filePath.makeRelativeTo( rootPath ).toPortableString() } ); //$NON-NLS-1$
            }
        }
    }

    private static class Msgs extends NLS
    {
        public static String addFromLiferay;
        public static String addJSP;
        public static String couldNotFindPortalRoot;
        public static String liferayCustomJSP;
        public static String selectJSPToCustomize;

        static
        {
            initializeMessages( CustomJSPsTableWizardSection.class.getName(), Msgs.class );
        }
    }
}
