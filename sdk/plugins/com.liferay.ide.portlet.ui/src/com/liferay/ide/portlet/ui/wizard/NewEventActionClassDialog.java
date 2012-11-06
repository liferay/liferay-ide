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

package com.liferay.ide.portlet.ui.wizard;

import com.liferay.ide.portlet.core.operation.NewEventActionClassDataModelProvider;
import com.liferay.ide.portlet.core.operation.NewEventActionClassOperation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class NewEventActionClassDialog extends Dialog
{
    protected Label classLabel;
    protected Text classText;
    protected IDataModel model;
    protected Button packageButton;
    protected Label packageLabel;
    protected Text packageText;
    protected String qualifiedClassname;
    protected String qualifiedSuperclassname;
    protected Combo superCombo;
    protected Label superLabel;
    
    protected NewEventActionClassDialog( Shell parentShell, IDataModel model )
    {
        super( parentShell );
        this.model = model;
    }

    public String getQualifiedClassname()
    {
        return this.qualifiedClassname;
    }

    public String getQualifiedSuperclassname()
    {
        return this.qualifiedSuperclassname;
    }

    private void createPackageGroup( Composite parent )
    {
        // package
        packageLabel = new Label( parent, SWT.LEFT );
        packageLabel.setText( J2EEUIMessages.JAVA_PACKAGE_LABEL );
        packageLabel.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_FILL ) );

        packageText = new Text( parent, SWT.SINGLE | SWT.BORDER );
        packageText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        packageText.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                updateQualifiedClassname();
            }
        } );

        packageButton = new Button( parent, SWT.PUSH );
        packageButton.setText( J2EEUIMessages.BROWSE_BUTTON_LABEL );
        packageButton.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_FILL ) );
        packageButton.addSelectionListener( new SelectionListener()
        {

            public void widgetDefaultSelected( SelectionEvent e )
            {
                // Do nothing
            }

            public void widgetSelected( SelectionEvent e )
            {
                handlePackageButtonPressed();
            }
        } );
    }

    @Override
    protected Button createButton( Composite parent, int id, String label, boolean defaultButton )
    {
        if( IDialogConstants.OK_ID == id )
        {
            return super.createButton( parent, id, "Create", defaultButton );
        }

        return super.createButton( parent, id, label, defaultButton );
    }

    protected void createClassnameGroup( Composite parent )
    {
        // class name
        classLabel = new Label( parent, SWT.LEFT );
        classLabel.setText( "Classname:" );
        classLabel.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_FILL ) );

        classText = new Text( parent, SWT.SINGLE | SWT.BORDER );
        classText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        classText.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                updateQualifiedClassname();
            }
        } );

        new Label( parent, SWT.LEFT );
    }

    @Override
    protected Control createDialogArea( Composite parent )
    {
        Composite areaParent = (Composite) super.createDialogArea( parent );
        areaParent.setLayout( new GridLayout( 3, false ) );

        createClassnameGroup( areaParent );

        createPackageGroup( areaParent );

        createSuperclassGroup( areaParent );

        return areaParent;
    }

    protected void createSuperclassGroup( Composite parent )
    {
        // superclass
        superLabel = new Label( parent, SWT.LEFT );
        superLabel.setText( J2EEUIMessages.SUPERCLASS_LABEL );
        superLabel.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_FILL ) );

        superCombo = new Combo( parent, SWT.DROP_DOWN );
        superCombo.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        superCombo.setItems( new String[] { "com.liferay.portal.kernel.events.SimpleAction",
            "com.liferay.portal.kernel.events.SessionAction", "com.liferay.portal.kernel.events.Action" } );
        superCombo.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                qualifiedSuperclassname = superCombo.getItem( superCombo.getSelectionIndex() );
            }
        } );
        superCombo.select( 0 );

        new Label( parent, SWT.NONE );
    }

    protected void handlePackageButtonPressed()
    {
        IPackageFragmentRoot packRoot =
            (IPackageFragmentRoot) model.getProperty( INewJavaClassDataModelProperties.JAVA_PACKAGE_FRAGMENT_ROOT );

        if( packRoot == null )
        {
            return;
        }

        IJavaElement[] packages = null;

        try
        {
            packages = packRoot.getChildren();
        }
        catch( JavaModelException e )
        {
            // Do nothing
        }

        if( packages == null )
        {
            packages = new IJavaElement[0];
        }

        ElementListSelectionDialog dialog =
            new ElementListSelectionDialog( getShell(), new JavaElementLabelProvider(
                JavaElementLabelProvider.SHOW_DEFAULT ) );
        dialog.setTitle( J2EEUIMessages.PACKAGE_SELECTION_DIALOG_TITLE );
        dialog.setMessage( J2EEUIMessages.PACKAGE_SELECTION_DIALOG_DESC );
        dialog.setEmptyListMessage( J2EEUIMessages.PACKAGE_SELECTION_DIALOG_MSG_NONE );
        dialog.setElements( packages );

        if( dialog.open() == Window.OK )
        {
            IPackageFragment fragment = (IPackageFragment) dialog.getFirstResult();

            if( fragment != null )
            {
                packageText.setText( fragment.getElementName() );
            }
            else
            {
                packageText.setText( J2EEUIMessages.EMPTY_STRING );
            }
        }
    }

    @Override
    protected void initializeBounds()
    {
        super.initializeBounds();

        getShell().setSize( 450, 200 );
    }

    @Override
    protected void okPressed()
    {
        // Create the class
        IDataModel dataModel =
            DataModelFactory.createDataModel( new NewEventActionClassDataModelProvider(
                model, getQualifiedClassname(), superCombo.getText() ) );

        NewEventActionClassOperation operation = new NewEventActionClassOperation( dataModel );

        try
        {
            operation.execute( null, null );
        }
        catch( ExecutionException e )
        {
            e.printStackTrace();
        }

        super.okPressed();
    }

    protected void updateQualifiedClassname()
    {
        this.qualifiedClassname = packageText.getText() + "." + classText.getText();
    }

}
