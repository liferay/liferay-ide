/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
import com.liferay.ide.ui.util.UIUtil;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class LiferayProjectImportWizard extends DataModelWizard implements IWorkbenchWizard, INewWizard
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

        setWindowTitle( Msgs.importProject );
        setDefaultPageImageDescriptor( ProjectUIPlugin.imageDescriptorFromPlugin(
            ProjectUIPlugin.PLUGIN_ID, "/icons/wizban/import_wiz.png" ) ); //$NON-NLS-1$
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

        liferayProjectImportWizardPage = new LiferayProjectImportWizardPage( getDataModel(), "pageOne", this ); //$NON-NLS-1$

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
    protected void postPerformFinish() throws InvocationTargetException
    {
        UIUtil.switchToLiferayPerspective();

        super.postPerformFinish();
    }

    @Override
    protected boolean runForked()
    {
        return false;
    }

    public void setProjectType( String projectType )
    {

    }

    private static class Msgs extends NLS
    {
        public static String importProject;

        static
        {
            initializeMessages( LiferayProjectImportWizard.class.getName(), Msgs.class );
        }
    }
}
