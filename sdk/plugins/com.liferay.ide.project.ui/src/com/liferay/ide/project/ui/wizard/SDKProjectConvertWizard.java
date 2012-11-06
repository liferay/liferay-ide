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

import com.liferay.ide.project.core.SDKProjectConvertDataModelProvider;
import com.liferay.ide.project.ui.ProjectUIPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class SDKProjectConvertWizard extends DataModelWizard implements IWorkbenchWizard
{
    protected IProject project;
    protected SDKProjectConvertWizardPage sdkProjectConvertWizardPage;

    public SDKProjectConvertWizard()
    {
        this( null );
    }

    public SDKProjectConvertWizard( IProject project )
    {
        super( null );

        this.project = project;

        setWindowTitle( "Convert Project" );

        setDefaultPageImageDescriptor( ProjectUIPlugin.imageDescriptorFromPlugin(
            ProjectUIPlugin.PLUGIN_ID, "/icons/wizban/convert_wiz.png" ) );
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
        sdkProjectConvertWizardPage = new SDKProjectConvertWizardPage( getDataModel(), "pageOne" );

        addPage( sdkProjectConvertWizardPage );
    }

    @Override
    protected IDataModelProvider getDefaultProvider()
    {
        return new SDKProjectConvertDataModelProvider( project );
    }

    @Override
    protected boolean runForked()
    {
        return false;
    }

}
