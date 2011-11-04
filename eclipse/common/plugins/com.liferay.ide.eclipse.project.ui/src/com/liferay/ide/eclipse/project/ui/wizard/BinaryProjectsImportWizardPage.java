/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.project.ui.wizard;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.BinaryProjectRecord;
import com.liferay.ide.eclipse.project.core.ISDKProjectsImportDataModelProperties;
import com.liferay.ide.eclipse.project.core.util.ProjectImportUtil;
import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;
import com.liferay.ide.eclipse.ui.util.SWTUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.wst.web.ui.internal.wizards.DataModelFacetCreationWizardPage;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@SuppressWarnings( { "restriction" } )
public class BinaryProjectsImportWizardPage extends DataModelFacetCreationWizardPage
	implements ISDKProjectsImportDataModelProperties {

	protected final class BinaryLabelProvider extends StyledCellLabelProvider {

		private static final String GREY_COLOR = "already_exist_element_color";
		private final ColorRegistry COLOR_REGISTRY = JFaceResources.getColorRegistry();
		private final Styler GREYED_STYLER;

		public BinaryLabelProvider() {
			COLOR_REGISTRY.put( GREY_COLOR, new RGB( 128, 128, 128 ) );
			GREYED_STYLER = StyledString.createColorRegistryStyler( GREY_COLOR, null );
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
		 */
		@Override
		public void update( ViewerCell cell ) {
			Object obj = cell.getElement();

			BinaryProjectRecord binaryProjectRecord = null;
			if ( obj instanceof BinaryProjectRecord ) {
				binaryProjectRecord = (BinaryProjectRecord) obj;
			}
			StyledString styledString = null;
			if ( binaryProjectRecord.isConflicts() ) {
				// TODO:show warning that some project exists, similar to what we get when importing projects with
				// standard import existing project into workspace
				styledString = new StyledString( binaryProjectRecord.getBinaryName(), GREYED_STYLER );
				styledString.append( " (" + binaryProjectRecord.getFilePath() + ") ", GREYED_STYLER );
			}
			else {
				styledString =
					new StyledString( binaryProjectRecord.getBinaryName(), StyledString.createColorRegistryStyler(
						JFacePreferences.CONTENT_ASSIST_FOREGROUND_COLOR,
						JFacePreferences.CONTENT_ASSIST_BACKGROUND_COLOR ) );
				styledString.append( " (" + binaryProjectRecord.getFilePath() + ") ", GREYED_STYLER );
			}

			cell.setImage( getImage() );
			cell.setText( styledString.getString() );
			cell.setStyleRanges( styledString.getStyleRanges() );
			super.update( cell );
		}

		public Image getImage() {
			Image image = ProjectUIPlugin.getDefault().getImageRegistry().get( ProjectUIPlugin.IMAGE_ID );

			return image;
		}

	}

	protected long lastModified;

	protected String lastPath;

	protected CheckboxTreeViewer binaryList;

	protected Text sdkLocation;

	protected Text sdkVersion;

	protected BinaryProjectRecord[] selectedBinaries = new BinaryProjectRecord[0];

	protected Combo serverTargetCombo;

	protected IProject[] wsProjects;

	protected Text binariesLocation;

	public BinaryProjectsImportWizardPage( IDataModel model, String pageName ) {
		super( model, pageName );

		setTitle( "Import Liferay Binary Plugins" );
		setDescription( "Select binary plugins (wars) to import as new Liferay Plugin Projects" );
	}

	public BinaryProjectRecord[] getPluginBinaryRecords() {
		List<BinaryProjectRecord> binaryProjectRecords = new ArrayList<BinaryProjectRecord>();

		for ( int i = 0; i < selectedBinaries.length; i++ ) {
			if ( isProjectInWorkspace( selectedBinaries[i].getLiferayPluginName() ) ) {
				selectedBinaries[i].setConflicts( true );
			}

			binaryProjectRecords.add( selectedBinaries[i] );
		}
		return binaryProjectRecords.toArray( new BinaryProjectRecord[binaryProjectRecords.size()] );
	}

	public void updateBinariesList() {
		String path = binariesLocation.getText();
		if ( path != null ) {
			updateBinariesList( path );
		}
	}

	public void updateBinariesList( String path ) {
		// on an empty path empty selectedProjects
		if ( path == null || path.length() == 0 ) {
			setMessage( "" ); //$NON-NLS-1$

			selectedBinaries = new BinaryProjectRecord[0];

			binaryList.refresh( true );

			binaryList.setCheckedElements( selectedBinaries );

			setPageComplete( binaryList.getCheckedElements().length > 0 );

			lastPath = path;

			return;
		}

		// Check if the direcotry is the Plugins SDK folder
		String sdkLocationPath = sdkLocation.getText();
		if ( sdkLocationPath != null && sdkLocationPath.equals( path ) ) {
			path = sdkLocationPath + "/dist";
		}

		final File directory = new File( path );

		long modified = directory.lastModified();

		if ( path.equals( lastPath ) && lastModified == modified ) {
			// since the file/folder was not modified and the path did not
			// change, no refreshing is required
			return;
		}

		lastPath = path;

		lastModified = modified;

		final boolean dirSelected = true;

		try {
			getContainer().run( true, true, new IRunnableWithProgress() {

				/*
				 * (non-Javadoc)
				 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org
				 * .eclipse.core.runtime.IProgressMonitor)
				 */
				public void run( IProgressMonitor monitor ) {

					monitor.beginTask( "", 100 ); //$NON-NLS-1$

					selectedBinaries = new BinaryProjectRecord[0];

					Collection<File> projectBinaries = new ArrayList<File>();

					monitor.worked( 10 );

					if ( dirSelected && directory.isDirectory() ) {

						if ( !ProjectImportUtil.collectBinariesFromDirectory( projectBinaries, directory, true, monitor ) ) {
							return;
						}

						selectedBinaries = new BinaryProjectRecord[projectBinaries.size()];

						int index = 0;

						monitor.worked( 50 );

						monitor.subTask( "" ); //$NON-NLS-1$

						for ( File binaryFile : projectBinaries ) {
							selectedBinaries[index++] = new BinaryProjectRecord( binaryFile );
						}

						// for ( File liferayProjectDir : liferayProjectDirs ) {
						// selectedProjects[index++] = new ProjectRecord( liferayProjectDir );
						// }
					}
					else {
						monitor.worked( 60 );
					}

					monitor.done();
				}

			} );
		}
		catch ( InvocationTargetException e ) {
			ProjectUIPlugin.logError( e );
		}
		catch ( InterruptedException e ) {
			// Nothing to do if the user interrupts.
		}

		binaryList.refresh( true );

		setPageComplete( binaryList.getCheckedElements().length > 0 );

		if ( selectedBinaries.length == 0 ) {
			setMessage( "", WARNING ); //$NON-NLS-1$
		}

		Object[] checkedBinaries = binaryList.getCheckedElements();

		if ( checkedBinaries != null && checkedBinaries.length > 0 ) {
			selectedBinaries = new BinaryProjectRecord[checkedBinaries.length];

			for ( int i = 0; i < checkedBinaries.length; i++ ) {
				selectedBinaries[i] = (BinaryProjectRecord) checkedBinaries[i];
			}
			getDataModel().setProperty( SELECTED_PROJECTS, selectedBinaries );
		}
	}

	protected void createPluginsSDKField( Composite parent ) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {

			@Override
			public void widgetSelected( SelectionEvent e ) {
				BinaryProjectsImportWizardPage.this.synchHelper.synchAllUIWithModel();
				validatePage( true );
				updateBinariesList();
			}

		};

		new LiferaySDKField(
			parent, getDataModel(), selectionAdapter, LIFERAY_SDK_NAME, this.synchHelper, "Select SDK to copy into:" );
	}

	protected void createBinaryLocationField( Composite parent ) {

		Label label = new Label( parent, SWT.NONE );
		label.setText( "Select plugins root directory:" );
		label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

		binariesLocation = SWTUtil.createSingleText( parent, 1 );

		Button browse = SWTUtil.createButton( parent, "Browse..." );
		browse.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected( SelectionEvent e ) {
				doBrowse();
			}

		} );

	}

	/**
	 * 
	 */
	protected void doBrowse() {
		DirectoryDialog dd = new DirectoryDialog( this.getShell(), SWT.OPEN );

		String filterPath = binariesLocation.getText();
		if ( filterPath != null ) {
			dd.setFilterPath( filterPath );
			dd.setText( "Select root directory " + " - " + filterPath ); //$NON-NLS-1$
		}
		else {
			dd.setText( "Select root directory" );
		}

		if ( CoreUtil.isNullOrEmpty( binariesLocation.getText() ) ) {
			dd.setFilterPath( binariesLocation.getText() );
		}

		String dir = dd.open();

		if ( !CoreUtil.isNullOrEmpty( dir ) ) {
			binariesLocation.setText( dir );
			updateBinariesList( dir );

		}
	}

	protected void createProjectsList( Composite workArea ) {

		Label title = new Label( workArea, SWT.NONE );
		title.setText( "Binary plugins:" );
		title.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 3, 1 ) );

		binaryList = new CheckboxTreeViewer( workArea, SWT.BORDER );

		GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 );
		gridData.widthHint = new PixelConverter( binaryList.getControl() ).convertWidthInCharsToPixels( 25 );
		gridData.heightHint = new PixelConverter( binaryList.getControl() ).convertHeightInCharsToPixels( 10 );

		binaryList.getControl().setLayoutData( gridData );
		binaryList.setContentProvider( new ITreeContentProvider() {

			public void dispose() {
			}

			public Object[] getChildren( Object parentElement ) {
				return null;
			}

			public Object[] getElements( Object inputElement ) {
				return getPluginBinaryRecords();
			}

			public Object getParent( Object element ) {
				return null;
			}

			public boolean hasChildren( Object element ) {
				return false;
			}

			public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
			}

		} );

		binaryList.setLabelProvider( new BinaryLabelProvider() );

		binaryList.addCheckStateListener( new ICheckStateListener() {

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ICheckStateListener#checkStateChanged
			 * (org.eclipse.jface.viewers.CheckStateChangedEvent)
			 */
			public void checkStateChanged( CheckStateChangedEvent event ) {
				// ProjectRecord element = (ProjectRecord) event.getElement();
				//
				// if ( element.hasConflicts() ) {
				// binaryList.setChecked( element, false );
				// }
				//
				getDataModel().setProperty( SELECTED_PROJECTS, binaryList.getCheckedElements() );

				setPageComplete( binaryList.getCheckedElements().length > 0 );
			}
		} );

		binaryList.setInput( this );
		binaryList.setComparator( new ViewerComparator() );

		createSelectionButtons( workArea );
	}

	protected void createSDKLocationField( Composite topComposite ) {
		SWTUtil.createLabel( topComposite, SWT.LEAD, "Liferay Plugin SDK Location:", 1 );

		sdkLocation = SWTUtil.createText( topComposite, 1 );
		( (GridData) sdkLocation.getLayoutData() ).widthHint = 300;
		this.synchHelper.synchText( sdkLocation, SDK_LOCATION, null );

		SWTUtil.createLabel( topComposite, SWT.LEAD, "", 1 );
	}

	protected void createSDKVersionField( Composite topComposite ) {
		SWTUtil.createLabel( topComposite, SWT.LEAD, "Liferay Plugin SDK Version:", 1 );

		sdkVersion = SWTUtil.createText( topComposite, 1 );
		this.synchHelper.synchText( sdkVersion, SDK_VERSION, null );

		SWTUtil.createLabel( topComposite, "", 1 ); //$NON-NLS-1$
	}

	/**
	 * Create the selection buttons in the listComposite.
	 * 
	 * @param listComposite
	 */
	protected void createSelectionButtons( Composite listComposite ) {
		Composite buttonsComposite = new Composite( listComposite, SWT.NONE );

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;

		buttonsComposite.setLayout( layout );

		buttonsComposite.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );

		Button selectAll = new Button( buttonsComposite, SWT.PUSH );
		selectAll.setText( "Select All" );
		selectAll.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected( SelectionEvent e ) {
				for ( int i = 0; i < selectedBinaries.length; i++ ) {
					if ( selectedBinaries[i].isConflicts() ) {
						binaryList.setChecked( selectedBinaries[i], false );
					}
					else {
						binaryList.setChecked( selectedBinaries[i], true );
					}
				}

				getDataModel().setProperty( SELECTED_PROJECTS, new Object[] { binaryList.getCheckedElements() } );

				validatePage( true );
				// setPageComplete(binaryList.getCheckedElements().length >
				// 0);
			}
		} );

		Dialog.applyDialogFont( selectAll );

		setButtonLayoutData( selectAll );

		Button deselectAll = new Button( buttonsComposite, SWT.PUSH );
		deselectAll.setText( "Deselect All" );
		deselectAll.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected( SelectionEvent e ) {
				binaryList.setCheckedElements( new Object[0] );
				setPageComplete( false );
			}
		} );

		Dialog.applyDialogFont( deselectAll );

		setButtonLayoutData( deselectAll );

		Button refresh = new Button( buttonsComposite, SWT.PUSH );
		refresh.setText( "Refresh" );
		refresh.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected( SelectionEvent e ) {
				// force a project refresh
				lastModified = -1;
				updateBinariesList( binariesLocation.getText() );
			}
		} );

		Dialog.applyDialogFont( refresh );

		setButtonLayoutData( refresh );
	}

	protected void createTargetRuntimeGroup( Composite parent ) {
		Label label = new Label( parent, SWT.NONE );
		label.setText( "Liferay target runtime:" );
		label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

		serverTargetCombo = new Combo( parent, SWT.BORDER | SWT.READ_ONLY );
		serverTargetCombo.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		Button newServerTargetButton = new Button( parent, SWT.NONE );
		newServerTargetButton.setText( "New..." );
		newServerTargetButton.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected( SelectionEvent e ) {
				final DataModelPropertyDescriptor[] preAdditionDescriptors =
					model.getValidPropertyDescriptors( FACET_RUNTIME );

				boolean isOK =
					ServerUIUtil.showNewRuntimeWizard(
						getShell(), getModuleTypeID(), null, "com.liferay.ide.eclipse.server" );

				if ( isOK ) {
					DataModelPropertyDescriptor[] postAdditionDescriptors =
						model.getValidPropertyDescriptors( FACET_RUNTIME );

					Object[] preAddition = new Object[preAdditionDescriptors.length];

					for ( int i = 0; i < preAddition.length; i++ ) {
						preAddition[i] = preAdditionDescriptors[i].getPropertyValue();
					}

					Object[] postAddition = new Object[postAdditionDescriptors.length];

					for ( int i = 0; i < postAddition.length; i++ ) {
						postAddition[i] = postAdditionDescriptors[i].getPropertyValue();
					}

					Object newAddition = CoreUtil.getNewObject( preAddition, postAddition );

					if ( newAddition != null ) // can this ever be null?
						model.setProperty( FACET_RUNTIME, newAddition );
				}
			}
		} );

		Control[] deps = new Control[] { newServerTargetButton };

		synchHelper.synchCombo( serverTargetCombo, FACET_RUNTIME, deps );

		if ( serverTargetCombo.getSelectionIndex() == -1 && serverTargetCombo.getVisibleItemCount() != 0 ) {
			serverTargetCombo.select( 0 );
		}
	}

	@Override
	protected Composite createTopLevelComposite( Composite parent ) {
		Composite topComposite = SWTUtil.createTopComposite( parent, 3 );

		GridLayout gl = new GridLayout( 3, false );
		// gl.marginLeft = 5;
		topComposite.setLayout( gl );
		topComposite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );

		createBinaryLocationField( topComposite );

		SWTUtil.createVerticalSpacer( topComposite, 1, 3 );

		createPluginsSDKField( topComposite );

		SWTUtil.createSeparator( topComposite, 3 );

		createSDKLocationField( topComposite );
		createSDKVersionField( topComposite );

		SWTUtil.createVerticalSpacer( topComposite, 1, 3 );

		createProjectsList( topComposite );
		createTargetRuntimeGroup( topComposite );

		return topComposite;
	}

	protected IProject[] getProjectsInWorkspace() {
		if ( wsProjects == null ) {
			wsProjects = IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getProjects();
		}
		return wsProjects;
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return new String[] { SDK_LOCATION, SDK_VERSION, SELECTED_PROJECTS, FACET_RUNTIME };
	}

	protected void handleFileBrowseButton( final Text text ) {
		DirectoryDialog dd = new DirectoryDialog( this.getShell(), SWT.OPEN );

		dd.setText( "Select Liferay Plugin SDK folder" );

		if ( !CoreUtil.isNullOrEmpty( sdkLocation.getText() ) ) {
			dd.setFilterPath( sdkLocation.getText() );
		}

		String dir = dd.open();

		if ( !CoreUtil.isNullOrEmpty( dir ) ) {
			sdkLocation.setText( dir );

			synchHelper.synchAllUIWithModel();

			validatePage();
		}
	}

	protected boolean isProjectInWorkspace( String projectName ) {
		if ( projectName == null ) {
			return false;
		}

		IProject[] workspaceProjects = getProjectsInWorkspace();

		for ( int i = 0; i < workspaceProjects.length; i++ ) {
			if ( projectName.equals( workspaceProjects[i].getName() ) ) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean showValidationErrorsOnEnter() {
		return true;
	}

}
