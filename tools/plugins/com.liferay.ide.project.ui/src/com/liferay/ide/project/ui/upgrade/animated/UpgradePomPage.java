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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.upgrade.LiferayUpgradeCompre;
import com.liferay.ide.project.ui.upgrade.MavenLegacyPomUpdater;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

/**
 * @author Andy Wu
 */
public class UpgradePomPage extends Page
{

    private class ProjectLabelProvider extends LabelProvider implements IStyledLabelProvider
    {

        @Override
        public Image getImage( Object element )
        {
            return ProjectUI.getDefault().getImage( "pom_file.gif" );
        }

        @Override
        public StyledString getStyledText( Object element )
        {
            IProject project = (IProject) element;
            String projectName = project.getName();
            IFile pomFile = project.getFile( "pom.xml" );
            String location = pomFile.getProjectRelativePath().toOSString();

            boolean finished = true;

            if( updater.isNeedUpgrade( pomFile ) )
            {
                finished = false;
            }

            String text = "pom.xml" + " (" + projectName + " - Location:" + projectName + "/" + location + ")";

            StyledString retVal = new StyledString();

            if( finished )
            {
                text += "(finished)";

                retVal.append( text, StyledString.COUNTER_STYLER );
            }
            else
            {
                retVal.append( text );
            }

            return retVal;
        }
    }

    private CheckboxTableViewer fTableViewer;

    private MavenLegacyPomUpdater updater = new MavenLegacyPomUpdater();

    public UpgradePomPage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel, UPGRADE_POM_PAGE_ID, true );

        GridLayout layout = new GridLayout( 2, false );
        layout.marginHeight = 0;
        layout.marginWidth = 0;

        this.setLayout( layout );

        final GridData descData = new GridData( GridData.FILL_BOTH );
        descData.grabExcessVerticalSpace = true;
        descData.grabExcessHorizontalSpace = true;

        this.setLayoutData( descData );

        fTableViewer = CheckboxTableViewer.newCheckList( this, SWT.BORDER );
        fTableViewer.setContentProvider( new ArrayContentProvider() );
        fTableViewer.setLabelProvider( new DelegatingStyledCellLabelProvider( new ProjectLabelProvider() ) );

        fTableViewer.addDoubleClickListener( new IDoubleClickListener()
        {

            @Override
            public void doubleClick( DoubleClickEvent event )
            {
                handleCompare( (IStructuredSelection) event.getSelection() );
            }
        } );

        final Table table = fTableViewer.getTable();

        final GridData tableData = new GridData( GridData.FILL_BOTH );
        tableData.grabExcessVerticalSpace = true;
        tableData.grabExcessHorizontalSpace = true;
        tableData.horizontalAlignment = SWT.FILL;
        table.setLayoutData( tableData );

        Composite buttonContainer = new Composite( this, SWT.NONE );
        buttonContainer.setLayout( new GridLayout( 1, false ) );
        buttonContainer.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false, 1, 1 ) );

        final Button findButton = new Button( buttonContainer, SWT.NONE );
        findButton.setText( "Find..." );
        findButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false, 1, 1 ) );
        findButton.addListener( SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent( Event event )
            {
                handleFindEvent();
            }
        } );

        final Button selectAllButton = new Button( buttonContainer, SWT.NONE );
        selectAllButton.setText( "Select All" );
        selectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false, 1, 1 ) );
        selectAllButton.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                fTableViewer.setAllChecked( true );
            }
        } );

        final Button disSelectAllButton = new Button( buttonContainer, SWT.NONE );
        disSelectAllButton.setText( "Disselect All" );
        disSelectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false, 1, 1 ) );
        disSelectAllButton.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                fTableViewer.setAllChecked( false );
            }
        } );

        final Button upgradeButton = new Button( buttonContainer, SWT.NONE );
        upgradeButton.setText( "Upgrade Selected" );
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

    public void createSpecialDescriptor( Composite parent, int style )
    {
        final String descriptor =
            "This step will help you to upgrade maven pom.xml files. double-click items to preview fixed file content";
        String url = "";

        Link link = SWTUtil.createHyperLink( this, style, descriptor, 1, url );
        link.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false, 2, 1 ) );
    }

    @Override
    public String getPageTitle()
    {
        return "Upgrade POM Files";
    }

    private void handleCompare( IStructuredSelection selection )
    {
        IProject project = (IProject) selection.getFirstElement();

        IPath tmpDirPath = ProjectUI.getDefault().getStateLocation().append( "tmp" );

        File tmpDir = tmpDirPath.toFile();
        tmpDir.mkdirs();

        File tempPomFile = new File( tmpDir, "pom.xml" );

        updater.upgradePomFile( project, tempPomFile );

        IFile pomfile = project.getFile( "pom.xml" );

        final LiferayUpgradeCompre lifeayDescriptorUpgradeCompre =
            new LiferayUpgradeCompre( pomfile.getLocation(), tmpDirPath.append( "pom.xml" ), "pom.xml" );

        lifeayDescriptorUpgradeCompre.openCompareEditor();
    }

    private void handleFindEvent()
    {
        IProject[] projectArrys = CoreUtil.getAllProjects();

        List<IProject> projectList = new ArrayList<IProject>();

        for( IProject project : projectArrys )
        {
            if( ProjectUtil.isMavenProject( project ) )
            {
                projectList.add( project );
            }
        }

        UIUtil.async( new Runnable()
        {

            @Override
            public void run()
            {
                String message = "ok";

                IProject[] projectsArray = projectList.toArray( new IProject[] {} );

                fTableViewer.setInput( projectsArray );

                if( projectsArray.length < 1 )
                {
                    message = "No pom file needs to be upgraded";
                }

                PageValidateEvent pe = new PageValidateEvent();
                pe.setMessage( message );
                pe.setType( PageValidateEvent.WARNING );

                triggerValidationEvent( pe );
            }
        } );
    }

    private void handleUpgradeEvent()
    {
        try
        {
            final Object[] selectedProjects = fTableViewer.getCheckedElements();

            List<IProject> projects = new ArrayList<>();

            if( selectedProjects != null )
            {
                for( Object project : selectedProjects )
                {
                    if( project instanceof IProject )
                    {
                        IProject p = (IProject) project;
                        projects.add( p );
                    }
                }
            }

            for( IProject project : projects )
            {
                updater.upgradePomFile( project, null );
            }

            handleFindEvent();
            fTableViewer.setAllChecked( false );
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }
    }
}
