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

package com.liferay.ide.portlet.vaadin7.ui;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.vaadin7.core.Vaadin7PortletFrameworkWizardProvider;
import com.liferay.ide.portlet.vaadin7.ui.wizard.NewVaadin7PortletWizard;
import com.liferay.ide.project.core.IPortletFrameworkWizardProvider;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.ui.AbstractPortletFrameworkDelegate;
import com.liferay.ide.project.ui.wizard.IPluginWizardFragment;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
public class Vaadin7PortletFrameworkDelegate extends AbstractPortletFrameworkDelegate
{
    protected IPluginWizardFragment wizardFragment;

    private final static Version v61 = new Version( "6.1" ); //$NON-NLS-1$

    public Vaadin7PortletFrameworkDelegate()
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
            wizardFragment = new NewVaadin7PortletWizard();
            wizardFragment.setFragment( true );
        }

        return wizardFragment;
    }

    public void updateFragmentEnabled( IDataModel dataModel )
    {
        String frameworkId = dataModel.getStringProperty( IPluginProjectDataModelProperties.PORTLET_FRAMEWORK_ID );

        IPortletFrameworkWizardProvider framework = LiferayProjectCore.getPortletFramework( frameworkId );

        if( framework instanceof Vaadin7PortletFrameworkWizardProvider )
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
