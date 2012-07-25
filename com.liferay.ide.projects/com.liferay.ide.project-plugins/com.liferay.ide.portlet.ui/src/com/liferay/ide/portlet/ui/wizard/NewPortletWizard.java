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
import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.portlet.core.operation.NewPortletClassDataModelProvider;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.portlet.ui.template.PortletTemplateContextTypeIds;
import com.liferay.ide.project.core.IPluginWizardFragmentProperties;
import com.liferay.ide.project.ui.wizard.IPluginWizardFragment;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jst.servlet.ui.internal.wizard.NewWebArtifactWizard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class NewPortletWizard extends NewWebArtifactWizard
    implements IPluginWizardFragment, INewPortletClassDataModelProperties
{
    protected boolean fragment;
    protected IWizardPage hostPage;
    protected IProject initialProject;

    public NewPortletWizard()
    {
        this( (IDataModel) null );
    }

    public NewPortletWizard( IDataModel model )
    {
        super( model );
        setDefaultPageImageDescriptor( getImage() );
    }

    public NewPortletWizard( IProject project )
    {
        this( (IDataModel) null );

        this.initialProject = project;
    }

    @Override
    protected void doAddPages()
    {
        addPage( new NewPortletClassWizardPage(
            getDataModel(), "pageOne", "Create a portlet class.", getDefaultPageTitle(), this.fragment,
            ( initialProject != null ) ) );
        addPage( new NewPortletOptionsWizardPage(
            getDataModel(), "pageTwo", "Specify portlet deployment descriptor details.", getDefaultPageTitle(),
            this.fragment ) );
        addPage( new NewLiferayPortletWizardPage(
            getDataModel(), "pageThree", "Specify Liferay portlet deployment descriptor details.",
            getDefaultPageTitle(), this.fragment ) );
        addPage( new NewPortletClassOptionsWizardPage(
            getDataModel(), "pageFour",
            "Specify modifiers, interfaces, and method stubs to generate in Portlet class.", getDefaultPageTitle(),
            this.fragment ) );
    }

    public IDataModelProvider getDataModelProvider()
    {
        return getDefaultProvider();
    }

    protected String getDefaultPageTitle()
    {
        return "Create Liferay Portlet";
    }

    @Override
    protected IDataModelProvider getDefaultProvider()
    {
        TemplateStore templateStore = PortletUIPlugin.getDefault().getTemplateStore();

        TemplateContextType contextType =
            PortletUIPlugin.getDefault().getTemplateContextRegistry().getContextType( PortletTemplateContextTypeIds.NEW );

        return new NewPortletClassDataModelProvider( templateStore, contextType, this.fragment, initialProject );
    }

    protected ImageDescriptor getImage()
    {
        return PortletUIPlugin.imageDescriptorFromPlugin( PortletUIPlugin.PLUGIN_ID, "/icons/wizban/portlet_wiz.png" );
    }

    public String getTitle()
    {
        return "New Liferay Portlet";
    }

    public void initFragmentDataModel( IDataModel parentDataModel, String projectName )
    {
        getDataModel().setBooleanProperty( IPluginWizardFragmentProperties.REMOVE_EXISTING_ARTIFACTS, true );
        getDataModel().setProperty(
            IPluginWizardFragmentProperties.FACET_RUNTIME,
            parentDataModel.getProperty( IFacetProjectCreationDataModelProperties.FACET_RUNTIME ) );
        getDataModel().setStringProperty( IArtifactEditOperationDataModelProperties.PROJECT_NAME, projectName );
    }

    @Override
    protected void openJavaClass()
    {
        if( getDataModel().getBooleanProperty( USE_DEFAULT_PORTLET_CLASS ) )
        {
            try
            {
                String jspsFolder = getDataModel().getStringProperty( CREATE_JSPS_FOLDER );
                IProject project =
                    ResourcesPlugin.getWorkspace().getRoot().getProject(
                        getDataModel().getStringProperty( PROJECT_NAME ) );

                IFolder docroot = CoreUtil.getDocroot( project );

                IFile viewFile = docroot.getFile( jspsFolder + "/view.jsp" );

                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                IDE.openEditor( page, viewFile, true );
            }
            catch( Exception e )
            {
                // eat this exception this is just best effort
            }
        }
        else
        {
            super.openJavaClass();
        }
    }

    @Override
    protected void postPerformFinish() throws InvocationTargetException
    {
        openJavaClass();
    }

    @Override
    protected boolean prePerformFinish()
    {
        if( this.fragment )
        {
            // if this is added to plugin wizard as fragment we don't want this to execute performFinish
            return false;
        }
        else
        {
            return true;
        }
    }

    public void setFragment( boolean fragment )
    {
        this.fragment = fragment;
    }

    public void setHostPage( IWizardPage hostPage )
    {
        this.hostPage = hostPage;
    }
}
