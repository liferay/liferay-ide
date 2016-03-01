/*******************************************************************************
 *
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Andy Wu
 */
public class PortalRuntimeComposite extends Composite implements ModifyListener
{

    private Text dirField;
    private List<IVMInstall> installedJREs;
    private String[] jreNames;
    private Button jreButton;
    private Combo jreCombo;
    private Label jreLabel;
    private Text nameField;
    private IRuntimeWorkingCopy runtimeWC;
    private Text typeField;

    private final IWizardHandle wizard;

    public PortalRuntimeComposite( Composite parent, IWizardHandle wizard )
    {
        super( parent, SWT.NONE );
        this.wizard = wizard;

        wizard.setTitle( Msgs.liferayPortalRuntime );
        wizard.setDescription( Msgs.specifyInstallationDirectory );
        wizard.setImageDescriptor( LiferayServerUI.getImageDescriptor( LiferayServerUI.IMG_WIZ_RUNTIME ) );

        createControl( parent );
    }

    protected void createControl( final Composite parent )
    {
        setLayout( createLayout() );
        setLayoutData( createLayoutData() );

        setBackground( parent.getBackground() );
        createFields();

        enableJREControls( false );

        Dialog.applyDialogFont( this );
    }

    private void createFields()
    {
        this.nameField = createTextField( Msgs.name );
        this.nameField.addModifyListener( this );

        this.dirField = createTextField( Msgs.liferayPortalRuntimeDirectory );
        this.dirField.addModifyListener( this );

        SWTUtil.createButton( this, Msgs.browse ).addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                final DirectoryDialog dd = new DirectoryDialog( getShell() );
                dd.setMessage( Msgs.selectLiferayPortalDirectory );

                final String selectedDir = dd.open();

                if( selectedDir != null )
                {
                    dirField.setText( selectedDir );
                }
            }
        });

        this.typeField = createReadOnlyTextField( Msgs.detectedPortalBundleType );

        jreLabel = createLabel( Msgs.selecteRuntimeJRE );

        jreCombo = new Combo( this, SWT.DROP_DOWN | SWT.READ_ONLY );
        jreCombo.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        jreCombo.addModifyListener( new ModifyListener()
        {

            @Override
            public void modifyText( ModifyEvent e )
            {
                int sel = jreCombo.getSelectionIndex();

                IVMInstall vmInstall = null;

                if( sel > 0 )
                {
                    vmInstall = installedJREs.get( sel - 1 );
                }

                PortalRuntime portalRuntime = getPortalRuntime();

                if( portalRuntime != null )
                {
                    portalRuntime.setVMInstall( vmInstall );
                }

                validate();
            }
        } );

        jreButton = SWTUtil.createButton( this, Msgs.installedJREs );
        jreButton.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                if( SWTUtil.showPreferencePage(
                    "org.eclipse.jdt.debug.ui.preferences.VMPreferencePage", getShell() ) )
                {
                    updateJREs();
                    validate();
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

    protected Text createReadOnlyTextField( String labelText )
    {
        return createTextField( labelText, SWT.READ_ONLY );
    }

    protected Text createTextField( String labelText )
    {
        return createTextField( labelText, SWT.NONE );
    }

    protected Text createTextField( String labelText, int style )
    {
        createLabel( labelText );

        Text text = new Text( this, SWT.BORDER | style );
        text.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        return text;
    }

    protected void enableJREControls( boolean enabled )
    {
        jreLabel.setEnabled( enabled );
        jreCombo.setEnabled( enabled );
        jreButton.setEnabled( enabled );
    }

    protected PortalRuntime getPortalRuntime()
    {
        return (PortalRuntime) getRuntime().loadAdapter( PortalRuntime.class, null );
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

        updateFields();
    }

    @Override
    public void modifyText( ModifyEvent e )
    {
        if( e.getSource().equals( dirField ) )
        {
            getRuntime().setLocation( new Path( dirField.getText() ) );
        }
        else if( e.getSource().equals( nameField ) )
        {
            getRuntime().setName( nameField.getText() );
        }

        updateFields();

        validate();

        enableJREControls( true );

        updateJREs();
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

        try
        {
            validate();
        }
        catch( NullPointerException e )
        {
            // ignore exception because this composite haven't been created and there are no shell
        }
    }

    private void updateFields()
    {
        final PortalRuntime portalRuntime = getPortalRuntime();

        if( portalRuntime != null )
        {
            final PortalBundle portalBundle = portalRuntime.getPortalBundle();

            setFieldValue( this.typeField, portalBundle != null ? portalBundle.getType() : StringPool.BLANK );
        }
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

    public static void setFieldValue( Text field, String value )
    {
        if( field != null && !field.isDisposed() )
        {
            field.setText( value != null ? value : StringPool.EMPTY );
        }
    }

    protected void updateJREs()
    {
        PortalRuntime portalRuntime = getPortalRuntime();

        IVMInstall currentVM = null;

        if ( portalRuntime!=null && portalRuntime.getVMInstall()!=null )
        {
            currentVM = portalRuntime.getVMInstall();
        }
        else
        {
            currentVM = JavaRuntime.getDefaultVMInstall();
        }

        int currentJREIndex = -1;

        // get all installed JVMs
        installedJREs = new ArrayList<IVMInstall>();

        IVMInstallType[] vmInstallTypes = JavaRuntime.getVMInstallTypes();

        int size = vmInstallTypes.length;

        for( int i = 0; i < size; i++ )
        {
            IVMInstall[] vmInstalls = vmInstallTypes[i].getVMInstalls();

            int size2 = vmInstalls.length;

            for( int j = 0; j < size2; j++ )
            {
                installedJREs.add( vmInstalls[j] );
            }
        }

        // get names
        size = installedJREs.size();

        jreNames = new String[size + 1];
        jreNames[0] = Msgs.defaultWorkbenchJRE;

        for( int i = 0; i < size; i++ )
        {
            IVMInstall vmInstall = installedJREs.get( i );

            jreNames[i + 1] = vmInstall.getName();

            if( vmInstall.equals( currentVM ) )
            {
                currentJREIndex = i + 1;
            }
        }

        if( jreCombo != null )
        {
            jreCombo.setItems( jreNames );
            jreCombo.select( currentJREIndex );
         }
    }


    static class Msgs extends NLS
    {
        public static String browse;
        public static String defaultWorkbenchJRE;
        public static String detectedPortalBundleType;
        public static String installedJREs;
        public static String liferayPortalRuntime;
        public static String liferayPortalRuntimeDirectory;
        public static String name;
        public static String selecteRuntimeJRE;
        public static String selectLiferayPortalDirectory;
        public static String specifyInstallationDirectory;

        static
        {
            initializeMessages( PortalRuntimeComposite.class.getName(), Msgs.class );
        }
    }
}