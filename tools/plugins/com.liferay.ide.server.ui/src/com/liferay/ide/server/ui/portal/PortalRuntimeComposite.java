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

package com.liferay.ide.server.ui.portal;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Gregory Amerson
 */
public class PortalRuntimeComposite extends Composite implements ModifyListener
{
    public static void setFieldValue( Text field, String value )
    {
        if( field != null && !field.isDisposed() )
        {
            field.setText( value != null ? value : StringPool.EMPTY );
        }
    }

    private Text dirField;
    private Text nameField;
    private IRuntimeWorkingCopy runtimeWC;
    private final IWizardHandle wizard;

    public PortalRuntimeComposite( Composite parent, IWizardHandle wizard )
    {
        super( parent, SWT.NONE );
        this.wizard = wizard;

        wizard.setTitle( "Liferay Portal Runtime" );
        wizard.setDescription( "Liferay Portal Runtime" );
        wizard.setDescription( "Specify the installation directory of the Liferay Portal bundle." );
        wizard.setImageDescriptor( LiferayServerUI.getImageDescriptor( LiferayServerUI.IMG_WIZ_RUNTIME ) );

        createControl( parent );
    }

    protected void createControl( final Composite parent )
    {
        setLayout( createLayout() );
        setLayoutData( createLayoutData() );

        setBackground( parent.getBackground() );
        createFields();

        Dialog.applyDialogFont( this );
    }

    private void createFields()
    {
        this.nameField = createTextField( "Name" );
        this.nameField.addModifyListener( this );

        this.dirField = createTextField( "Liferay Portal Bundle Directory" );
        this.dirField.addModifyListener( this );

        SWTUtil.createButton( this, "Browse..." ).addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                final DirectoryDialog dd = new DirectoryDialog( getShell() );
                dd.setMessage( "Select Liferay Portal bundle directory" );

                final String selectedDir = dd.open();

                if( selectedDir != null )
                {
                    dirField.setText( selectedDir );
                }
            }
        });
    }

    protected Label createLabel( String text )
    {
        Label label = new Label( this, SWT.NONE );
        label.setText( text );

        GridDataFactory.generate( label, 2, 1 );

        return label;
    }

    protected Layout createLayout()
    {
        return new GridLayout( 2, false );
    }

    private GridData createLayoutData()
    {
        return new GridData( GridData.FILL_BOTH );
    }

    protected Text createTextField( String labelText )
    {
        createLabel( labelText );

        Text text = new Text( this, SWT.BORDER );
        text.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        return text;
    }

    protected IRuntimeWorkingCopy getRuntime()
    {
        return this.runtimeWC;
    }

    protected void init()
    {
        if( this.dirField == null || this.nameField == null || getRuntime() == null )
        {
            return;
        }

        setFieldValue( this.nameField, getRuntime().getName() );
        setFieldValue( this.dirField, getRuntime().getLocation() != null ?
            getRuntime().getLocation().toOSString() : StringPool.EMPTY );
    }

    @Override
    public void modifyText( ModifyEvent e )
    {

    }

    public void setRuntime( IRuntimeWorkingCopy newRuntime )
    {
        if( newRuntime == null )
        {
            this.runtimeWC = null;
        }
        else
        {
            this.runtimeWC = newRuntime;
        }

        init();
        validate();
    }

    protected void validate()
    {
        final IStatus status = this.runtimeWC.validate( null );

        if( status == null || status.isOK() )
        {
            this.wizard.setMessage( null, IMessageProvider.NONE );
        }
        else if( status.getSeverity() == IStatus.WARNING )
        {
            this.wizard.setMessage( status.getMessage(), IMessageProvider.WARNING );
        }
        else
        {
            this.wizard.setMessage( status.getMessage(), IMessageProvider.ERROR );
        }

        this.wizard.update();
    }

}
