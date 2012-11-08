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
import com.liferay.ide.hook.core.operation.INewHookDataModelProperties;
import com.liferay.ide.hook.ui.HookUI;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.wizard.StringArrayTableWizardSectionCallback;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class NewCustomJSPsHookWizardPage extends DataModelWizardPage implements INewHookDataModelProperties
{

    protected Text customJSPsFolder;

    protected Button disableJSPFolderValidation;

    protected CustomJSPsTableWizardSection jspItemsSection;

    public NewCustomJSPsHookWizardPage( IDataModel dataModel, String pageName )
    {
        super( dataModel, pageName, "Create Custom JSPs", HookUI.imageDescriptorFromPlugin(
            HookUI.PLUGIN_ID, "/icons/wizban/hook_wiz.png" ) );

        setDescription( "Create customs JSP folder and select JSPs to override." );
    }

    protected void createCustomJSPsGroup( Composite parent )
    {
        Composite composite = SWTUtil.createTopComposite( parent, 2 );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );

        jspItemsSection =
            new CustomJSPsTableWizardSection(
                composite, "JSP files to override", "JSP File Path", "Add...", "Edit...", "Remove...",
                new String[] { "Add" }, new String[] { "JSP File Path" }, null, getDataModel(), CUSTOM_JSPS_ITEMS );

        GridData gd = new GridData( SWT.FILL, SWT.CENTER, true, true, 1, 1 );
        gd.heightHint = 175;

        jspItemsSection.setLayoutData( gd );
        jspItemsSection.setCallback( new StringArrayTableWizardSectionCallback() );

        IProject project = CoreUtil.getProject( getDataModel().getStringProperty( PROJECT_NAME ) );

        if( project != null )
        {
            try
            {
                ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( project );

                IPath portalDir = liferayRuntime.getPortalDir();

                if( portalDir != null && portalDir.toFile().exists() )
                {
                    jspItemsSection.setPortalDir( portalDir.toFile() );
                }
            }
            catch( CoreException e )
            {
                HookUI.logError( e );
            }
        }

    }

    protected void createDisableJSPFolderValidation( Composite topComposite )
    {
        Composite composite = SWTUtil.createTopComposite( topComposite, 3 );

        GridLayout gl = new GridLayout( 1, false );
        // gl.marginLeft = 5;

        composite.setLayout( gl );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );

        disableJSPFolderValidation = new Button( composite, SWT.CHECK );
        disableJSPFolderValidation.setText( "Disable JSP syntax validation for custom JSP folder (recommended)." );
        this.synchHelper.synchCheckbox( disableJSPFolderValidation, DISABLE_CUSTOM_JSP_FOLDER_VALIDATION, null );
    }

    protected void createJSPFolderGroup( Composite topComposite )
    {
        Composite composite = SWTUtil.createTopComposite( topComposite, 3 );

        GridLayout gl = new GridLayout( 3, false );
        // gl.marginLeft = 5;

        composite.setLayout( gl );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );

        SWTUtil.createLabel( composite, SWT.LEAD, "Custom JSP folder:", 1 );

        customJSPsFolder = SWTUtil.createText( composite, 1 );
        this.synchHelper.synchText( customJSPsFolder, CUSTOM_JSPS_FOLDER, null );

        Button iconFileBrowse = SWTUtil.createPushButton( composite, "Browse...", null );
        iconFileBrowse.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                handleFileBrowseButton( NewCustomJSPsHookWizardPage.this.customJSPsFolder );
            }
        } );
    }

    @Override
    protected Composite createTopLevelComposite( Composite parent )
    {
        Composite topComposite = SWTUtil.createTopComposite( parent, 3 );

        createJSPFolderGroup( topComposite );

        createCustomJSPsGroup( topComposite );

        createDisableJSPFolderValidation( topComposite );

        return topComposite;
    }

    @Override
    protected void enter()
    {
        super.enter();

        this.synchHelper.synchAllUIWithModel();
    }

    protected ISelectionStatusValidator getContainerDialogSelectionValidator()
    {
        return new ISelectionStatusValidator()
        {

            public IStatus validate( Object[] selection )
            {
                if( selection != null && selection.length > 0 && selection[0] != null &&
                    !( selection[0] instanceof IProject ) && !( selection[0] instanceof IFile ) )
                {
                    return Status.OK_STATUS;
                }

                return HookUI.createErrorStatus( "Choose a valid folder for custom jsps." );
            }
        };
    }

    protected ViewerFilter getContainerDialogViewerFilter()
    {
        return new ViewerFilter()
        {

            public boolean select( Viewer viewer, Object parent, Object element )
            {
                if( element instanceof IProject )
                {
                    IProject project = (IProject) element;

                    return project.getName().equals(
                        model.getProperty( IArtifactEditOperationDataModelProperties.PROJECT_NAME ) );
                }
                else if( element instanceof IFolder )
                {
                    return true;
                }

                return false;
            }
        };
    }

    @Override
    protected String[] getValidationPropertyNames()
    {
        return new String[] { CUSTOM_JSPS_FOLDER, CUSTOM_JSPS_ITEMS };
    }

    protected void handleFileBrowseButton( final Text text )
    {
        ISelectionStatusValidator validator = getContainerDialogSelectionValidator();

        ViewerFilter filter = getContainerDialogViewerFilter();

        ITreeContentProvider contentProvider = new WorkbenchContentProvider();

        ILabelProvider labelProvider =
            new DecoratingLabelProvider(
                new WorkbenchLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator() );

        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog( getShell(), labelProvider, contentProvider );
        dialog.setValidator( validator );
        dialog.setTitle( J2EEUIMessages.CONTAINER_SELECTION_DIALOG_TITLE );
        dialog.setMessage( J2EEUIMessages.CONTAINER_SELECTION_DIALOG_DESC );
        dialog.addFilter( filter );
        dialog.setInput( CoreUtil.getWorkspaceRoot() );

        if( dialog.open() == Window.OK )
        {
            Object element = dialog.getFirstResult();

            try
            {
                if( element instanceof IFolder )
                {
                    IFolder folder = (IFolder) element;

                    if( folder.equals( CoreUtil.getDocroot( getDataModel().getStringProperty( PROJECT_NAME ) ) ) )
                    {
                        folder = folder.getFolder( "custom_jsps" );
                    }

                    text.setText( folder.getFullPath().toPortableString() );
                }
            }
            catch( Exception ex )
            {
                // Do nothing
            }
        }
    }

}
