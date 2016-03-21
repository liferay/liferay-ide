/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.liferay.ide.ui.wizard;

import com.liferay.ide.ui.LiferayUIPlugin;

import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

public class RenameDialog extends SelectionStatusDialog
{
    private ArrayList oldNames;
    private String oldName;
    private String newName;
    private Text text;
    private IStatus status;
    private boolean isCaseSensitive;
    private IInputValidator fValidator;

    /**
     * Create a new rename dialog instance for the given window.
     * 
     * @param shell
     *            The parent of the dialog
     * @param oldName
     *            Current name of the item being renamed
     */
    public RenameDialog( Shell shell, String oldName )
    {
        super( shell );
        this.isCaseSensitive = false;
        initialize();
        setOldName( oldName );
    }

    /**
     * Create a new rename dialog instance for the given window.
     * 
     * @param shell
     *            The parent of the dialog
     * @param isCaseSensitive
     *            Flags whether dialog will perform case sensitive checks against old names
     * @param names
     *            Set of names which the user should not be allowed to rename to
     * @param oldName
     *            Current name of the item being renamed
     */
    public RenameDialog( Shell shell, boolean isCaseSensitive, String[] names, String oldName )
    {
        super( shell );
        this.isCaseSensitive = isCaseSensitive;
        initialize();
        if( names != null )
        {
            for( int i = 0; i < names.length; i++ )
                addOldName( names[i] );
        }
        setOldName( oldName );
    }

    public void initialize()
    {
        oldNames = new ArrayList();
        setStatusLineAboveButtons( true );
    }

    public void addOldName( String oldName )
    {
        if( !oldNames.contains( oldName ) )
            oldNames.add( oldName );

    }

    public void setOldName( String oldName )
    {
        this.oldName = oldName;
        addOldName( oldName );
        if( text != null )
            text.setText( oldName );
        this.newName = oldName;
    }

    /*
     * @see org.eclipse.jface.window.Window#configureShell(Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
    }

    protected Control createDialogArea( Composite parent )
    {
        Composite container = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = layout.marginWidth = 9;
        container.setLayout( layout );

        GridData gd = new GridData( GridData.FILL_BOTH );
        container.setLayoutData( gd );

        Label label = new Label( container, SWT.NULL );
        label.setText( Msgs.enterNewName );

        text = new Text( container, SWT.SINGLE | SWT.BORDER );
        text.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                textChanged( text.getText() );
            }
        } );
        gd = new GridData( GridData.FILL_HORIZONTAL );
        text.setLayoutData( gd );
        applyDialogFont( container );
        return container;
    }

    public int open()
    {
        text.setText( oldName );
        text.selectAll();
        Button okButton = getButton( IDialogConstants.OK_ID );

        status = new Status( IStatus.OK, LiferayUIPlugin.PLUGIN_ID, IStatus.OK, "", //$NON-NLS-1$
        null );
        updateStatus( status );
        okButton.setEnabled( false );
        return super.open();
    }

    private void textChanged( String text )
    {
        Button okButton = getButton( IDialogConstants.OK_ID );
        if( fValidator != null )
        {
            String message = fValidator.isValid( text );
            if( message != null )
            {
                status = new Status( IStatus.ERROR, LiferayUIPlugin.PLUGIN_ID, IStatus.ERROR, message, null );
                updateStatus( status );
                okButton.setEnabled( false );
                return;
            }
        }
        for( int i = 0; i < oldNames.size(); i++ )
        {
            if( ( isCaseSensitive && text.equals( oldNames.get( i ) ) ) ||
                ( !isCaseSensitive && text.equalsIgnoreCase( oldNames.get( i ).toString() ) ) )
            {
                status =
                    new Status( IStatus.ERROR, LiferayUIPlugin.PLUGIN_ID, IStatus.ERROR, Msgs.nameAlreadyExists, null );
                updateStatus( status );
                okButton.setEnabled( false );
                break;
            }
            okButton.setEnabled( true );
            status = new Status( IStatus.OK, LiferayUIPlugin.PLUGIN_ID, IStatus.OK, "", //$NON-NLS-1$
            null );
            updateStatus( status );
        }
    }

    public String getNewName()
    {
        return newName;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        newName = text.getText().trim();
        super.okPressed();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.dialogs.SelectionStatusDialog#computeResult()
     */
    protected void computeResult()
    {
    }

    public void setTitle( String title )
    {
        getShell().setText( title );
    }

    public void setInputValidator( IInputValidator validator )
    {
        fValidator = validator;
    }

    private static class Msgs extends NLS
    {
        public static String enterNewName;
        public static String nameAlreadyExists;

        static
        {
            initializeMessages( RenameDialog.class.getName(), Msgs.class );
        }
    }
}
