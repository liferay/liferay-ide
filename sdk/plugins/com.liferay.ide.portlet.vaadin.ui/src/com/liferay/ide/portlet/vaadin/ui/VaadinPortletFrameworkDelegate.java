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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portlet.vaadin.ui;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.vaadin.core.VaadinPortletFrameworkWizardProvider;
import com.liferay.ide.portlet.vaadin.ui.wizard.NewVaadinPortletWizard;
import com.liferay.ide.project.core.IPortletFrameworkWizardProvider;
import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.ui.AbstractPortletFrameworkDelegate;
import com.liferay.ide.project.ui.wizard.IPluginWizardFragment;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.sdk.SDKManager;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
public class VaadinPortletFrameworkDelegate extends AbstractPortletFrameworkDelegate
{
    protected IPluginWizardFragment wizardFragment;

    private final static Version v61 = new Version( "6.1" ); //$NON-NLS-1$

    public VaadinPortletFrameworkDelegate()
    {
        super();
    }

    public Composite createNewProjectOptionsComposite( Composite parent )
    {
        return null;
    }

    @Override
    public IPluginWizardFragment getWizardFragment()
    {
        if( wizardFragment == null )
        {
            wizardFragment = new NewVaadinPortletWizard();
            wizardFragment.setFragment( true );
        }

        return wizardFragment;
    }

    public void updateFragmentEnabled( IDataModel dataModel )
    {
        String frameworkId = dataModel.getStringProperty( IPluginProjectDataModelProperties.PORTLET_FRAMEWORK_ID );

        IPortletFrameworkWizardProvider framework = ProjectCorePlugin.getPortletFramework( frameworkId );

        if( framework instanceof VaadinPortletFrameworkWizardProvider )
        {
            String sdkName = dataModel.getStringProperty( IPluginProjectDataModelProperties.LIFERAY_SDK_NAME );

            SDK sdk = SDKManager.getInstance().getSDK( sdkName );

            if( sdk != null )
            {
                Version sdkVersion = new Version( sdk.getVersion() );

                if( CoreUtil.compareVersions( v61, sdkVersion ) > 0 )
                {
                    // user has selected sdk 6.0.x so use vaadin portlet wizard
                    setFragmentEnabled( true );
                }
                else
                {
                    setFragmentEnabled( false );
                }
            }
        }
    }
}
