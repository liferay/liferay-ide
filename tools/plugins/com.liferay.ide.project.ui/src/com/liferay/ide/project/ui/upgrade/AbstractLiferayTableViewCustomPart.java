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

package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.upgrade.CodeUpgradeOp;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.dialog.JavaProjectSelectionDialog;
import com.liferay.ide.project.ui.wizard.ElementLabelProvider;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

/**
 * @author Simon Jiang
 */
public abstract class AbstractLiferayTableViewCustomPart extends FormComponentPart
{
    protected Status retval = Status.createOkStatus();

    protected TableViewer tableViewer;

    private LiferayUpgradeElement[] tableViewElements;
    
    public class LiferayUpgradeElement
    {

        public String name;
        public String context;
        public final String location;
        public final String itemName;

        public LiferayUpgradeElement( String name, String context, String location, String itemName )
        {
            this.context = context;
            this.name = name;
            this.location = location;
            this.itemName = itemName;
        }
    }

    protected abstract class LiferayUpgradeTabeViewLabelProvider extends ElementLabelProvider
        implements IColorProvider, IStyledLabelProvider
    {

        private String GREY_COLOR;

        private final ColorRegistry COLOR_REGISTRY = JFaceResources.getColorRegistry();

        protected Styler GREYED_STYLER;

        public LiferayUpgradeTabeViewLabelProvider()
        {
            COLOR_REGISTRY.put( GREY_COLOR, new RGB( 128, 128, 128 ) );
            GREYED_STYLER = StyledString.createColorRegistryStyler( GREY_COLOR, null );
        }

        public LiferayUpgradeTabeViewLabelProvider( final String greyColorName )
        {
            this.GREY_COLOR = greyColorName;
        }

        @Override
        public Color getForeground( Object element )
        {
            if( element instanceof LiferayUpgradeElement )
            {
                final String srcLableString = ( (LiferayUpgradeElement) element ).context;

                if( srcLableString.contains( "Finished" ) )
                {
                    return Display.getCurrent().getSystemColor( SWT.COLOR_BLUE );
                }
            }

            return Display.getCurrent().getSystemColor( SWT.COLOR_BLACK );
        }

        @Override
        public StyledString getStyledText( Object element )
        {
            if( element instanceof LiferayUpgradeElement )
            {
                final String srcLableString = ( (LiferayUpgradeElement) element ).context;
                final String elementName = ( (LiferayUpgradeElement) element ).name;
                final StyledString styled = new StyledString( elementName );
                return StyledCellLabelProvider.styleDecoratedString( srcLableString, GREYED_STYLER, styled );
            }

            return new StyledString( ( (LiferayUpgradeElement) element ).context );
        }
    }

    protected class TableViewContentProvider implements IStructuredContentProvider
    {
        @Override
        public void dispose()
        {
        }

        @Override
        public Object[] getElements( Object inputElement )
        {
            if( inputElement instanceof LiferayUpgradeElement[] )
            {
                return (LiferayUpgradeElement[]) inputElement;
            }

            return new Object[] { inputElement };
        }

        @Override
        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }
    }

    protected abstract boolean isNeedUpgrade( File srcFile );
    
    public static IPath getTempLocation( String prefix, String fileName )
    {
        return ProjectUI.getDefault().getStateLocation().append( "tmp" ).append(
            prefix + "/" + System.currentTimeMillis() +
                ( CoreUtil.isNullOrEmpty( fileName ) ? StringPool.EMPTY : "/" + fileName ) );
    }

    
    protected abstract void createTempFile( final File srcFile, final File templateFile, final String projectName  );
    protected abstract void doUpgrade( File srcFile, IProject project );
    protected abstract IFile[] getAvaiableUpgradeFiles( IProject project );
    protected abstract IStyledLabelProvider getLableProvider();
    
    @Override
    protected Status computeValidation()
    {
        return retval;
    }

    @Override
    public FormComponentPresentation createPresentation( SwtPresentation parent, Composite composite )
    {
        return new FormComponentPresentation( this, parent, composite)
        {

            @Override
            public void render()
            {
                final Composite parent = SWTUtil.createComposite( composite(), 2, 2, GridData.FILL_BOTH );

                tableViewer = new TableViewer( parent );

                tableViewer.setContentProvider( new TableViewContentProvider() );

                tableViewer.setLabelProvider( new DelegatingStyledCellLabelProvider( getLableProvider() ) );

                tableViewer.addDoubleClickListener( new IDoubleClickListener()
                {

                    @Override
                    public void doubleClick( DoubleClickEvent event )
                    {
                        handleCompare( (IStructuredSelection) event.getSelection() );
                    }
                } );

                final Table table = tableViewer.getTable();
                final GridData tableData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 4 );

                tableData.heightHint = 225;
                tableData.widthHint = 400;
                table.setLayoutData( tableData );

                final Button selectAllButton = new Button( parent, SWT.NONE );

                selectAllButton.setText( "Find..." );
                selectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                selectAllButton.addListener( SWT.Selection, new Listener()
                {

                    @Override
                    public void handleEvent( Event event )
                    {
                        handleFindEvent();
                    }
                } );

                final Button upgradeButton = new Button( parent, SWT.NONE );

                upgradeButton.setText( "Upgrade..." );
                upgradeButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                upgradeButton.addListener( SWT.Selection, new Listener()
                {

                    @Override
                    public void handleEvent( Event event )
                    {
                        handleUpgradeEvent();
                    }
                } );
            }
        };
    }
    
    private IPath createPreviewerFile(
        final String projectName, final IPath srcFilePath, final String location )
    {
        final IPath templateLocation = getTempLocation( projectName, srcFilePath.lastSegment() );
        templateLocation.toFile().getParentFile().mkdirs();
        try
        {
            createTempFile( new File( location ), templateLocation.toFile(), projectName );
        }
        catch( Exception e )
        {
            ProjectCore.logError( e );
        }

        return templateLocation;
    }

    private List<LiferayUpgradeElement> getInitItemsList( List<IProject> projects, IProgressMonitor monitor )
    {
        final List<LiferayUpgradeElement> tableViewElementList = new ArrayList<>();

        final Path sdkLocation = op().getNewLocation().content();
        
        String context = null;

        int count = projects.size();

        if( count <= 0 )
        {
            return tableViewElementList;
        }

        int unit = 100 / count;

        monitor.beginTask( "Find needed upgrade file......", 100 );

        for( int i = 0; i < count; i++ )
        {
            monitor.worked( i + 1 * unit );

            if( i == count - 1 )
            {
                monitor.worked( 100 );
            }

            IProject project = projects.get( i );
            monitor.setTaskName( "Finding needed upgrade file for " + project.getName() );
            IFile[] upgradeFiles = getAvaiableUpgradeFiles( project );

            for( IFile upgradeFilePath : upgradeFiles )
            {
                IPath filePath = upgradeFilePath.getLocation();
                if( isNeedUpgrade( filePath.toFile() ) )
                {
                    final String projectLocation = filePath.makeRelativeTo( PathBridge.create( sdkLocation ) ).toPortableString();

                    context =
                        filePath.lastSegment() + " (" + project.getName() + " - Location: " + projectLocation + ")";

                    LiferayUpgradeElement tableViewElement = new LiferayUpgradeElement(
                        project.getName(), context, filePath.toPortableString(), filePath.lastSegment() );

                    tableViewElementList.add( tableViewElement );
                }
            }
        }

        return tableViewElementList;
    }
    
    
    protected List<IProject> getSelectedProjects()
    {
        List<IProject> projects = new ArrayList<>();

        final JavaProjectSelectionDialog dialog =
            new JavaProjectSelectionDialog( Display.getCurrent().getActiveShell() );

        if( dialog.open() == Window.OK )
        {
            final Object[] selectedProjects = dialog.getResult();

            if( selectedProjects != null )
            {
                for( Object project : selectedProjects )
                {
                    if( project instanceof IJavaProject )
                    {
                        IJavaProject p = (IJavaProject) project;
                        projects.add( p.getProject() );
                    }
                }
            }
        }
        return projects;
    }
    
    private void handleCompare( IStructuredSelection selection )
    {
        final LiferayUpgradeElement descriptorElement =
            (LiferayUpgradeElement) selection.getFirstElement();

        final String projectName = descriptorElement.name;
        final String itemName = descriptorElement.itemName;
        final String srcFileLocation = descriptorElement.location;
        final IPath srcFileIPath = PathBridge.create( new Path( srcFileLocation ) );
        final IPath createPreviewerFile =
            createPreviewerFile( projectName, srcFileIPath, srcFileLocation);

        final LiferayUpgradeCompre lifeayDescriptorUpgradeCompre =
            new LiferayUpgradeCompre( srcFileIPath, createPreviewerFile, itemName );

        lifeayDescriptorUpgradeCompre.openCompareEditor();
    }
    
    private void handleFindEvent()
    {
        List<IProject> projects = getSelectedProjects();
        try
        {
            final WorkspaceJob workspaceJob = new WorkspaceJob( "Find needed upgrade files......")
            {

                @Override
                public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                {
                    final List<LiferayUpgradeElement> tableViewElementList = getInitItemsList( projects, monitor );

                    tableViewElements = tableViewElementList.toArray(
                        new LiferayUpgradeElement[tableViewElementList.size()] );

                    UIUtil.async( new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            tableViewer.setInput( tableViewElements );
                        }
                    } );

                    return StatusBridge.create( Status.createOkStatus() );
                }
            };
            workspaceJob.setUser( true );
            workspaceJob.schedule();
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }

    }
    
    private void handleUpgradeEvent()
    {
        try
        {
            final WorkspaceJob workspaceJob = new WorkspaceJob( "Find needed upgrade files......")
            {

                @Override
                public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                {
                    int count = tableViewElements.length;

                    if( count <= 0 )
                    {
                        return StatusBridge.create( Status.createOkStatus() );
                    }

                    int unit = 100 / count;

                    monitor.beginTask( "Start to upgrade files.....", 100 );

                    for( int i = 0; i < count; i++ )
                    {
                        monitor.worked( i + 1 * unit );

                        if( i == count - 1 )
                        {
                            monitor.worked( 100 );
                        }

                        LiferayUpgradeElement tableViewElement = tableViewElements[i];

                        final String srcFileLocation = tableViewElement.location;
                        final String projectName = tableViewElement.name;

                        monitor.setTaskName( "Upgrading files for " + projectName );

                        try
                        {
                            IProject project = ProjectUtil.getProject( projectName );

                            doUpgrade( new File( srcFileLocation ), project );

                            if( project != null )
                            {
                                project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                            }

                            final int loopNum = i;

                            UIUtil.async( new Runnable()
                            {

                                @Override
                                public void run()
                                {
                                    tableViewElement.context = tableViewElement.context + "( Finished )";

                                    tableViewElements[loopNum] = tableViewElement;

                                    tableViewer.setInput( tableViewElements );

                                    tableViewer.refresh();

                                }
                            } );
                        }
                        catch( Exception e )
                        {
                            ProjectCore.logError( "Error upgrade files...... ", e );
                        }
                    }
                    return StatusBridge.create( Status.createOkStatus() );
                }

            };

            workspaceJob.setUser( true );
            workspaceJob.schedule();
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }
    }

    private CodeUpgradeOp op()
    {
        return getLocalModelElement().nearest( CodeUpgradeOp.class );
    }

}
