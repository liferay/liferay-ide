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

import com.liferay.ide.portlet.core.operation.INewServiceBuilderDataModelProperties;
import com.liferay.ide.portlet.core.operation.NewServiceBuilderDataModelProvider;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.portlet.ui.template.ServiceBuilderTemplateContextTypeIds;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jst.servlet.ui.internal.wizard.NewWebArtifactWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class NewServiceBuilderWizard extends NewWebArtifactWizard
    implements INewWizard, INewServiceBuilderDataModelProperties
{

    public NewServiceBuilderWizard()
    {
        this( null );
    }

    public NewServiceBuilderWizard( IDataModel model )
    {
        super( model );

        setDefaultPageImageDescriptor( getImage() );
    }

    @Override
    protected void doAddPages()
    {
        addPage( new NewServiceBuilderWizardPage(
            getDataModel(), "pageOne", "New Liferay Service Builder",
            "Create a new service builder xml file in a project." ) );
    }

    @Override
    protected IDataModelProvider getDefaultProvider()
    {
        TemplateStore templateStore = PortletUIPlugin.getDefault().getTemplateStore();

        TemplateContextType contextType =
            PortletUIPlugin.getDefault().getTemplateContextRegistry().getContextType(
                ServiceBuilderTemplateContextTypeIds.NEW );

        return new NewServiceBuilderDataModelProvider( templateStore, contextType );
    }

    protected ImageDescriptor getImage()
    {
        return PortletUIPlugin.imageDescriptorFromPlugin( PortletUIPlugin.PLUGIN_ID, "/icons/wizban/service_wiz.png" );
    }

    @Override
    protected String getTitle()
    {
        return "New Service Builder";
    }

    @Override
    protected void postPerformFinish() throws InvocationTargetException
    {
        Object file = getDataModel().getProperty( CREATED_SERVICE_FILE );

        if( file instanceof IFile )
        {
            openEditor( (IFile) file );
        }
    }

}
