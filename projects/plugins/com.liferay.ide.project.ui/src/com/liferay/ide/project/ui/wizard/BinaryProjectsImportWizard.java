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

import com.liferay.ide.project.core.BinaryProjectsImportDataModelProvider;
import com.liferay.ide.project.core.SDKProjectsImportDataModelProvider;
import com.liferay.ide.project.ui.ProjectUIPlugin;
import com.liferay.ide.sdk.SDK;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@SuppressWarnings( "restriction" )
public class BinaryProjectsImportWizard extends DataModelWizard implements IWorkbenchWizard
{
    protected BinaryProjectsImportDataModelProvider dataModelProvider;
    protected BinaryProjectsImportWizardPage pluginBinaryProjectsImportWizardPage;
    protected SDK sdk;

    public BinaryProjectsImportWizard()
    {
        this( (IDataModel) null );
        dataModelProvider = new BinaryProjectsImportDataModelProvider();
    }

    public BinaryProjectsImportWizard( IDataModel dataModel )
    {
        super( dataModel );

        setWindowTitle( "Import Projects" );

        setDefaultPageImageDescriptor( ProjectUIPlugin.imageDescriptorFromPlugin(
            ProjectUIPlugin.PLUGIN_ID, "/icons/wizban/import_wiz.png" ) );
    }

    public BinaryProjectsImportWizard( SDK sdk )
    {
        this( (IDataModel) null );

        this.sdk = sdk;
    }

    @Override
    public boolean canFinish()
    {
        return getDataModel().isValid();
    }

    @Override
    protected void doAddPages()
    {
        if( sdk != null )
        {
            IDataModel model = getDataModel();
            model.setStringProperty( SDKProjectsImportDataModelProvider.LIFERAY_SDK_NAME, sdk.getName() );
        }

        pluginBinaryProjectsImportWizardPage = new BinaryProjectsImportWizardPage( getDataModel(), "pageOne" );

        addPage( pluginBinaryProjectsImportWizardPage );
    }

    @Override
    protected IDataModelProvider getDefaultProvider()
    {
        return dataModelProvider;
    }

    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard#runForked()
     */
    @Override
    protected boolean runForked()
    {
        return false;
    }

}
