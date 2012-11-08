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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.ui.HookUI;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.dialog.FilteredTypesSelectionDialogEx;
import com.liferay.ide.ui.wizard.StringArrayTableWizardSection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.core.search.BasicSearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class EventActionsTableWizardSection extends StringArrayTableWizardSection
{

    protected class AddEventActionDialog extends AddStringArrayDialog
    {
        protected String[] buttonLabels;

        public AddEventActionDialog( Shell shell, String windowTitle, String[] labelsForTextField, String[] buttonLabels )
        {
            super( shell, windowTitle, labelsForTextField );

            setShellStyle( getShellStyle() | SWT.RESIZE );

            this.buttonLabels = buttonLabels;

            setWidthHint( 450 );
        }

        @Override
        protected Text createField( Composite parent, final int index )
        {
            Label label = new Label( parent, SWT.LEFT );
            label.setText( labelsForTextField[index] );
            label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

            // Composite composite = new Composite(parent, SWT.NONE);
            // GridData data = new GridData(GridData.FILL_HORIZONTAL);
            // composite.setLayoutData(data);
            // composite.setLayout(new GridLayout(2, false));

            final Text text = new Text( parent, SWT.SINGLE | SWT.BORDER );

            GridData data = new GridData( GridData.FILL_HORIZONTAL );
            // data.widthHint = 200;
            text.setLayoutData( data );

            Composite buttonComposite = new Composite( parent, SWT.NONE );

            String[] buttonLbls = buttonLabels[index].split( "," );

            GridLayout gl = new GridLayout( buttonLbls.length, true );
            gl.marginWidth = 0;
            gl.horizontalSpacing = 1;

            buttonComposite.setLayout( gl );

            for( final String lbl : buttonLbls )
            {
                Button button = new Button( buttonComposite, SWT.PUSH );
                button.setText( lbl );
                button.addSelectionListener( new SelectionAdapter()
                {

                    @Override
                    public void widgetSelected( SelectionEvent e )
                    {
                        handleArrayDialogButtonSelected( index, lbl, text );
                    }
                } );
            }

            return text;
        }

        protected void handleArrayDialogButtonSelected( int index, String label, Text text )
        {
            if( index == 0 )
            { // select event
                handleSelectEventButton( text );
            }
            else if( index == 1 && "Select...".equals( label ) )
            {
                handleSelectClassButton( text );
            }
            else if( index == 1 && "New...".equals( label ) )
            {
                handleNewClassButton( text );
            }
        }

        protected void handleNewClassButton( Text text )
        {
            NewEventActionClassDialog dialog = new NewEventActionClassDialog( getShell(), model );

            if( dialog.open() == Window.OK )
            {
                String qualifiedClassname = dialog.getQualifiedClassname();

                text.setText( qualifiedClassname );
            }
        }

        protected void handleSelectClassButton( Text text )
        {
            Control control = text;

            // IPackageFragmentRoot packRoot = (IPackageFragmentRoot)
            // model.getProperty(INewJavaClassDataModelProperties.JAVA_PACKAGE_FRAGMENT_ROOT);
            // if (packRoot == null)
            // return;

            IJavaSearchScope scope = null;

            try
            {
                // scope =
                // BasicSearchEngine.createHierarchyScope(packRoot.getJavaProject().findType("com.liferay.portal.kernel.events.SimpleAction"));
                scope =
                    BasicSearchEngine.createJavaSearchScope( new IJavaElement[] { JavaCore.create( CoreUtil.getProject( model.getStringProperty( INewJavaClassDataModelProperties.PROJECT_NAME ) ) ) } );

            }
            catch( Exception e )
            {
                HookUI.logError( e );

                return;
            }

            // This includes all entries on the classpath. This behavior is
            // identical
            // to the Super Class Browse Button on the Create new Java Class
            // Wizard
            // final IJavaSearchScope scope =
            // SearchEngine.createJavaSearchScope(new
            // IJavaElement[] {root.getJavaProject()} );

            FilteredTypesSelectionDialog dialog =
                new FilteredTypesSelectionDialogEx( getShell(), false, null, scope, IJavaSearchConstants.CLASS );
            dialog.setTitle( "Event selection" );
            dialog.setMessage( "Select an event action:" );

            if( dialog.open() == Window.OK )
            {
                IType type = (IType) dialog.getFirstResult();

                String classFullPath = J2EEUIMessages.EMPTY_STRING;

                if( type != null )
                {
                    classFullPath = type.getFullyQualifiedName();
                }

                if( control instanceof Text )
                {
                    ( (Text) control ).setText( classFullPath );
                }
                else if( control instanceof Combo )
                {
                    ( (Combo) control ).setText( classFullPath );
                }

                return;
            }
        }

        protected void handleSelectEventButton( Text text )
        {
            String[] hookProperties = new String[] {};

            ILiferayRuntime runtime;

            try
            {
                runtime = ServerUtil.getLiferayRuntime( project );

                hookProperties = runtime.getSupportedHookProperties();
            }
            catch( CoreException e )
            {
                HookUI.logError( e );
            }

            // if (eventActionPropertiesFile == null) {
            // try {
            // loadEventActionsPropertiesFile();
            // }
            // catch (Exception e) {
            // HookUI.logError(e);
            // return;
            // }
            // }

            PropertiesFilteredDialog dialog = new PropertiesFilteredDialog( getParentShell(), ".*events.*" );
            dialog.setTitle( "Property selection" );
            dialog.setMessage( "Please select a property:" );
            dialog.setInput( hookProperties );

            if( dialog.open() == Window.OK )
            {
                Object[] selected = dialog.getResult();

                text.setText( selected[0].toString() );
            }
        }

    }

    protected String[] buttonLabels;

    // protected File eventActionPropertiesFile;

    protected IProject project;

    public EventActionsTableWizardSection(
        Composite parent, String componentLabel, String dialogTitle, String addButtonLabel, String editButtonLabel,
        String removeButtonLabel, String[] columnTitles, String[] fieldLabels, Image labelProviderImage,
        IDataModel model, String propertyName )
    {

        super( parent, componentLabel, dialogTitle, addButtonLabel, editButtonLabel, removeButtonLabel, columnTitles, fieldLabels, labelProviderImage, model, propertyName );

        this.buttonLabels = new String[] { "Select...", "Select...,New..." };
    }

    public void setProject( IProject project )
    {
        this.project = project;
    }

    @Override
    protected void handleAddButtonSelected()
    {
        AddEventActionDialog dialog = new AddEventActionDialog( getShell(), dialogTitle, fieldLabels, buttonLabels );

        if( dialog.open() == Window.OK )
        {
            String[] stringArray = dialog.getStringArray();

            addStringArray( stringArray );
        }
    }

}
