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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.core.LiferayProjectImportDataModelProvider;
import com.liferay.ide.project.ui.ProjectUIPlugin;
import com.liferay.ide.ui.wizard.INewProjectWizard;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayProjectImportWizard extends DataModelWizard implements IWorkbenchWizard, INewProjectWizard
{

    protected LiferayProjectImportWizardPage liferayProjectImportWizardPage;
    protected IFolder folder;

    public LiferayProjectImportWizard()
    {
        this( (IDataModel) null );
    }

    public LiferayProjectImportWizard( IDataModel dataModel )
    {
        super( dataModel );

        setWindowTitle( "Import Project" );
        setDefaultPageImageDescriptor( ProjectUIPlugin.imageDescriptorFromPlugin(
            ProjectUIPlugin.PLUGIN_ID, "/icons/wizban/import_wiz.png" ) );
        setNeedsProgressMonitor( true );
    }

    public LiferayProjectImportWizard( IFolder folder )
    {
        this( (IDataModel) null );

        this.folder = folder;
    }

    @Override
    public boolean canFinish()
    {
        return getDataModel().isValid();
    }

    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }

    @Override
    protected void doAddPages()
    {
        if( folder != null )
        {
            IDataModel model = getDataModel();
            model.setProperty(
                LiferayProjectImportDataModelProvider.PROJECT_LOCATION, folder.getRawLocation().toOSString() );

        }

        liferayProjectImportWizardPage = new LiferayProjectImportWizardPage( getDataModel(), "pageOne", this );

        if( folder != null )
        {
            liferayProjectImportWizardPage.updateProjectRecord( folder.getRawLocation().toOSString() );
        }

        addPage( liferayProjectImportWizardPage );
    }

    @Override
    protected IDataModelProvider getDefaultProvider()
    {
        return new LiferayProjectImportDataModelProvider();
    }

    @Override
    protected boolean runForked()
    {
        return false;
    }

    public void setProjectType( String projectType )
    {

    }

}
