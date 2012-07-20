/*******************************************************************************
 *  Copyright (c) 2003, 2008 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.ui.form.FormLayoutFactory;
import com.liferay.ide.ui.form.IDEFormPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class DependenciesFormPage extends IDEFormPage
{

    public static final String PAGE_ID = "dependencies"; //$NON-NLS-1$

    public DependenciesFormPage( FormEditor editor )
    {
        super( editor, PAGE_ID, "Dependencies" );
    }

    protected void createFormContent( IManagedForm managedForm )
    {
        super.createFormContent( managedForm );
        ScrolledForm form = managedForm.getForm();
        form.setImage( PortletUIPlugin.imageDescriptorFromPlugin( PortletUIPlugin.PLUGIN_ID, "/icons/e16/plugin.png" ).createImage() );
        form.setText( "Dependencies" );
        Composite body = form.getBody();
        body.setLayout( FormLayoutFactory.createFormGridLayout( true, 2 ) );

        Composite left, right;
        FormToolkit toolkit = managedForm.getToolkit();
        left = toolkit.createComposite( body, SWT.NONE );
        left.setLayout( FormLayoutFactory.createFormPaneGridLayout( false, 1 ) );
        left.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        right = toolkit.createComposite( body, SWT.NONE );
        right.setLayout( FormLayoutFactory.createFormPaneGridLayout( false, 1 ) );
        right.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        PortalJarsSection jarsSection = new PortalJarsSection( this, left, getRequiredSectionLabels() );
        managedForm.addPart( jarsSection );

        GridData gd = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING );
        gd.widthHint = 150;
        PortalTldsSection tldsSection = new PortalTldsSection( this, right, getRequiredSectionLabels() );
        managedForm.addPart( tldsSection );
        // managedForm.addPart(new PortalJarsSection(this, left, getRequiredSectionLabels()));
    }

    private String[] getRequiredSectionLabels()
    {
        return new String[] { "Add...", "Remove",
        // "Up",
        // "Down"
        };
    }

}
