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

package com.liferay.ide.portlet.ui;

import com.liferay.ide.portlet.core.MVCPortletFrameworkWizardProvider;
import com.liferay.ide.portlet.ui.wizard.NewPortletWizard;
import com.liferay.ide.project.core.IPortletFrameworkWizardProvider;
import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.ui.AbstractPortletFrameworkDelegate;
import com.liferay.ide.project.ui.wizard.IPluginWizardFragment;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
public class MVCPortletFrameworkDelegate extends AbstractPortletFrameworkDelegate
{

    protected IPluginWizardFragment wizardFragment;

    public MVCPortletFrameworkDelegate()
    {
        super();
    }

    public Composite createNewProjectOptionsComposite( Composite parent )
    {
        Group group = SWTUtil.createGroup( parent, "Additional Options", 1 );

        final Button createCustomClassButton = new Button( group, SWT.CHECK );
        createCustomClassButton.setText( "Create custom portlet class" );
        createCustomClassButton.addSelectionListener
        ( 
            new SelectionAdapter()
            {
                @Override
                public void widgetSelected( SelectionEvent e )
                {
                    setFragmentEnabled( createCustomClassButton.getSelection() );
                    MVCPortletFrameworkDelegate.this.getDataModel().setBooleanProperty(
                        IPluginProjectDataModelProperties.PLUGIN_FRAGMENT_ENABLED, createCustomClassButton.getSelection() );
                }
            }
        );

        return group;
    }

    @Override
    public IPluginWizardFragment getWizardFragment()
    {
        if( wizardFragment == null )
        {
            wizardFragment = new NewPortletWizard();
            wizardFragment.setFragment( true );
        }

        return wizardFragment;
    }

    @Override
    protected void updateFragmentEnabled( IDataModel dataModel )
    {
        String frameworkId = dataModel.getStringProperty( IPluginProjectDataModelProperties.PORTLET_FRAMEWORK_ID );

        IPortletFrameworkWizardProvider framework = ProjectCorePlugin.getPortletFramework( frameworkId );

        if( framework instanceof MVCPortletFrameworkWizardProvider )
        {
            dataModel.setBooleanProperty(
                IPluginProjectDataModelProperties.PLUGIN_FRAGMENT_ENABLED, isFragmentEnabled() );
        }
    }

}
