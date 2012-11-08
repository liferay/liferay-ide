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

import com.liferay.ide.hook.core.operation.NewServiceWrapperClassDataModelProvider;
import com.liferay.ide.hook.core.operation.NewServiceWrapperClassOperation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class NewServiceWrapperClassDialog extends NewEventActionClassDialog
{
    protected String serviceType;
    protected Text superclassText;
    protected String wrapperType;

    public NewServiceWrapperClassDialog( Shell shell, IDataModel model, String serviceType, String wrapperType )
    {
        super( shell, model );

        this.serviceType = serviceType;
        this.wrapperType = wrapperType;
    }

    @Override
    protected Control createDialogArea( Composite parent )
    {
        Control control = super.createDialogArea( parent );

        String defaultClassname =
            "Ext" + this.serviceType.substring( this.serviceType.lastIndexOf( '.' ) + 1, this.serviceType.length() );

        classText.setText( defaultClassname );

        return control;
    }

    protected void createSuperclassGroup( Composite parent )
    {
        // superclass
        superLabel = new Label( parent, SWT.LEFT );
        superLabel.setText( J2EEUIMessages.SUPERCLASS_LABEL );
        superLabel.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_FILL ) );

        superclassText = new Text( parent, SWT.SINGLE | SWT.BORDER );
        superclassText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        superclassText.setText( this.wrapperType );
        superclassText.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                qualifiedSuperclassname = classText.getText();
            }

        } );

        new Label( parent, SWT.NONE );
    }

    @Override
    protected void okPressed()
    {
        // Create the class
        IDataModel dataModel =
            DataModelFactory.createDataModel( new NewServiceWrapperClassDataModelProvider(
                model, getQualifiedClassname(), superclassText.getText() ) );

        NewServiceWrapperClassOperation operation = new NewServiceWrapperClassOperation( dataModel );

        try
        {
            operation.execute( null, null );
        }
        catch( ExecutionException e )
        {
            e.printStackTrace();
        }

        setReturnCode( OK );

        close();
    }

}
