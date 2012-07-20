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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.operation.INewHookDataModelProperties;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.wizard.StringArrayTableWizardSectionCallback;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
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
public class NewPortalPropertiesHookWizardPage extends DataModelWizardPage implements INewHookDataModelProperties
{
    protected EventActionsTableWizardSection eventActionsSection;
    protected Text portalPropertiesFile;
    protected PropertyOverridesTableWizardSection propertyOverridesSection;

    public NewPortalPropertiesHookWizardPage( IDataModel dataModel, String pageName )
    {
        super( dataModel, pageName, "Create Portal Properties", PortletUIPlugin.imageDescriptorFromPlugin(
            PortletUIPlugin.PLUGIN_ID, "/icons/wizban/hook_wiz.png" ) );

        setDescription( "Specify which portal properties to override." );
    }

    protected void createEventActionsGroup( Composite topComposite )
    {
        Composite composite = SWTUtil.createTopComposite( topComposite, 2 );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );

        eventActionsSection =
            new EventActionsTableWizardSection(
                composite, "Define actions to be executed on portal events:", "Add Event Action", "Add...", "Edit...",
                "Remove...", new String[] { "Event", "Class" }, new String[] { "Event:", "Class:" }, null,
                getDataModel(), PORTAL_PROPERTIES_ACTION_ITEMS );

        GridData gd = new GridData( SWT.FILL, SWT.CENTER, true, true, 1, 1 );
        gd.heightHint = 150;

        eventActionsSection.setLayoutData( gd );
        eventActionsSection.setCallback( new StringArrayTableWizardSectionCallback() );

        IProject project = CoreUtil.getProject( getDataModel().getStringProperty( PROJECT_NAME ) );

        if( project != null )
        {
            eventActionsSection.setProject( project );
        }
    }

    protected void createPortalPropertiesFileGroup( Composite topComposite )
    {
        Composite composite = SWTUtil.createTopComposite( topComposite, 3 );

        GridLayout gl = new GridLayout( 3, false );
        gl.marginLeft = 5;

        composite.setLayout( gl );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );

        SWTUtil.createLabel( composite, SWT.LEAD, "Portal properties file:", 1 );

        portalPropertiesFile = SWTUtil.createText( composite, 1 );
        this.synchHelper.synchText( portalPropertiesFile, PORTAL_PROPERTIES_FILE, null );

        Button iconFileBrowse = SWTUtil.createPushButton( composite, "Browse...", null );
        iconFileBrowse.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                handleBrowseButton( NewPortalPropertiesHookWizardPage.this.portalPropertiesFile );
            }
        } );
    }

    protected void createPropertiesOverridesGroup( Composite topComposite )
    {
        Composite composite = SWTUtil.createTopComposite( topComposite, 2 );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );

        propertyOverridesSection =
            new PropertyOverridesTableWizardSection(
                composite, "Specify properties to override:", "Add Property Override", "Add...", "Edit...",
                "Remove...", new String[] { "Property", "Value" }, new String[] { "Property:", "Value:" }, null,
                getDataModel(), PORTAL_PROPERTIES_OVERRIDE_ITEMS );

        GridData gd = new GridData( SWT.FILL, SWT.CENTER, true, true, 1, 1 );
        gd.heightHint = 150;

        propertyOverridesSection.setLayoutData( gd );
        propertyOverridesSection.setCallback( new StringArrayTableWizardSectionCallback() );

        IProject project = CoreUtil.getProject( getDataModel().getStringProperty( PROJECT_NAME ) );

        if( project != null )
        {
            propertyOverridesSection.setProject( project );
        }
    }

    @Override
    protected Composite createTopLevelComposite( Composite parent )
    {
        Composite topComposite = SWTUtil.createTopComposite( parent, 3 );
        topComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );

        createPortalPropertiesFileGroup( topComposite );

        createEventActionsGroup( topComposite );

        createPropertiesOverridesGroup( topComposite );

        return topComposite;
    }

    protected ISelectionStatusValidator getContainerDialogSelectionValidator()
    {
        return new ISelectionStatusValidator()
        {

            public IStatus validate( Object[] selection )
            {
                if( selection != null && selection.length > 0 && selection[0] != null &&
                    !( selection[0] instanceof IProject ) )
                {
                    return Status.OK_STATUS;
                }

                return PortletUIPlugin.createErrorStatus( "Choose a valid file or folder for portal.properties." );
            }
        };
    }

    protected ViewerFilter getContainerDialogViewerFilter()
    {
        return new ViewerFilter()
        {
            @SuppressWarnings( "deprecation" )
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
                    IFolder folder = (IFolder) element;

                    // only show source folders
                    IProject project =
                        ProjectUtilities.getProject( model.getStringProperty( IArtifactEditOperationDataModelProperties.PROJECT_NAME ) );

                    IPackageFragmentRoot[] sourceFolders = J2EEProjectUtilities.getSourceContainers( project );

                    for( int i = 0; i < sourceFolders.length; i++ )
                    {
                        if( sourceFolders[i].getResource() != null && sourceFolders[i].getResource().equals( folder ) )
                        {
                            return true;
                        }
                        else if( ProjectUtil.isParent( folder, sourceFolders[i].getResource() ) )
                        {
                            return true;
                        }
                    }
                }
                else if( element instanceof IFile )
                {
                    IFile file = (IFile) element;

                    return file.exists() && file.getName().equals( "portal.properties" );
                }

                return false;
            }
        };
    }

    @Override
    protected String[] getValidationPropertyNames()
    {
        return new String[] { PORTAL_PROPERTIES_FILE, PORTAL_PROPERTIES_ACTION_ITEMS, PORTAL_PROPERTIES_OVERRIDE_ITEMS };
    }

    protected void handleBrowseButton( final Text text )
    {
        ISelectionStatusValidator validator = getContainerDialogSelectionValidator();

        ViewerFilter filter = getContainerDialogViewerFilter();

        ITreeContentProvider contentProvider = new WorkbenchContentProvider();

        ILabelProvider labelProvider =
            new DecoratingLabelProvider(
                new WorkbenchLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator() );

        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog( getShell(), labelProvider, contentProvider );
        dialog.setValidator( validator );
        dialog.setTitle( "Portal properties File" );
        dialog.setMessage( "Portal properties File" );
        dialog.addFilter( filter );
        dialog.setInput( CoreUtil.getWorkspaceRoot() );

        if( dialog.open() == Window.OK )
        {
            Object element = dialog.getFirstResult();

            try
            {
                if( element instanceof IFile )
                {
                    IFile file = (IFile) element;

                    text.setText( file.getFullPath().toPortableString() );
                }
                else if( element instanceof IFolder )
                {
                    IFolder folder = (IFolder) element;

                    text.setText( folder.getFullPath().append( "portal.properties" ).toPortableString() );
                }
            }
            catch( Exception ex )
            {
                // Do nothing
            }

        }
    }

}
