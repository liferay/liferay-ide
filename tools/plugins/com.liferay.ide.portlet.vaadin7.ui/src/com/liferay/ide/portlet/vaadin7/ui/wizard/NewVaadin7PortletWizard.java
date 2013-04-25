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

package com.liferay.ide.portlet.vaadin7.ui.wizard;

import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.portlet.ui.template.PortletTemplateContextTypeIds;
import com.liferay.ide.portlet.ui.wizard.NewLiferayPortletWizardPage;
import com.liferay.ide.portlet.ui.wizard.NewPortletWizard;
import com.liferay.ide.portlet.vaadin7.core.operation.NewVaadin7PortletClassDataModelProvider;
import com.liferay.ide.portlet.vaadin7.ui.Vaadin7UI;
import com.liferay.ide.project.ui.wizard.ValidProjectChecker;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

/**
 * @author Henri Sara
 */
@SuppressWarnings( "restriction" )
public class NewVaadin7PortletWizard extends NewPortletWizard
{

    public static final String ID = "com.liferay.ide.eclipse.portlet.vaadin7.ui.wizard.portlet"; //$NON-NLS-1$

    public NewVaadin7PortletWizard()
    {
        this( null );
    }

    public NewVaadin7PortletWizard( IDataModel model )
    {
        super( model );
    }

    @Override
    public String getTitle()
    {
        return Msgs.newLiferayVaadin7Portlet;
    }

    @Override
    protected void doAddPages()
    {
        addPage( new NewVaadin7UIClassWizardPage(
            getDataModel(), "pageOne", Msgs.createVaadin7PortletUIClass, getDefaultPageTitle(), fragment ) ); //$NON-NLS-1$
        addPage( new NewVaadin7PortletOptionsWizardPage(
            getDataModel(), "pageTwo", Msgs.specifyVaadin7PortletDeployment, getDefaultPageTitle(), //$NON-NLS-1$
            fragment ) );
        addPage( new NewLiferayPortletWizardPage(
            getDataModel(), "pageThree", Msgs.specifyLiferayPortletDeployment, //$NON-NLS-1$
            getDefaultPageTitle(), fragment ) );
    }

    @Override
    protected String getDefaultPageTitle()
    {
        return Msgs.createLiferayVaadin7Portlet;
    }

    @Override
    protected IDataModelProvider getDefaultProvider()
    {
        // for now, no need for own template store and context type
        final TemplateStore templateStore = PortletUIPlugin.getDefault().getTemplateStore();

        final TemplateContextType contextType =
            PortletUIPlugin.getDefault().getTemplateContextRegistry().getContextType( PortletTemplateContextTypeIds.NEW );

        return new NewVaadin7PortletClassDataModelProvider( fragment )
        {
            @Override
            public IDataModelOperation getDefaultOperation()
            {
                return new AddVaadin7UIOperation( this.model, templateStore, contextType );
            }
        };
    }

    @Override
    protected ImageDescriptor getImage()
    {
        return ImageDescriptor.createFromURL( Vaadin7UI.getDefault().getBundle().getEntry(
            "/icons/wizban/vaadin_wiz.png" ) ); //$NON-NLS-1$
    }

    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        getDataModel();
        ValidProjectChecker checker = new ValidProjectChecker( ID );
        checker.checkValidProjectTypes();
    }

    private static class Msgs extends NLS
    {
        public static String createLiferayVaadin7Portlet;
        public static String createVaadin7PortletUIClass;
        public static String newLiferayVaadin7Portlet;
        public static String specifyLiferayPortletDeployment;
        public static String specifyVaadin7PortletDeployment;

        static
        {
            initializeMessages( NewVaadin7PortletWizard.class.getName(), Msgs.class );
        }
    }
}
