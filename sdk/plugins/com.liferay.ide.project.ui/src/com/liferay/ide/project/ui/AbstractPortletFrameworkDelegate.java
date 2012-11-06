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

package com.liferay.ide.project.ui;

import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.ui.wizard.IPluginWizardFragment;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;

/**
 * @author Greg Amerson
 */
public abstract class AbstractPortletFrameworkDelegate implements IPortletFrameworkDelegate, IDataModelListener
{
    protected String bundleId = null;
    protected IDataModel dataModel;
    protected boolean fragmentEnabled = false;
    protected String frameworkId = null;
    protected String iconUrl = null;

    public Composite createNewProjectOptionsComposite( Composite parent, IDataModel newProjectDataModel )
    {
        this.dataModel = newProjectDataModel;

        this.dataModel.addListener( this );

        Composite composite = createNewProjectOptionsComposite( parent );

        updateFragmentEnabled( this.dataModel );

        return composite;
    }

    public String getBundleId()
    {
        return bundleId;
    }

    public IDataModel getDataModel()
    {
        return dataModel;
    }

    public String getFrameworkId()
    {
        return frameworkId;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public IPluginWizardFragment getWizardFragment()
    {
        return null;
    }

    public boolean isFragmentEnabled()
    {
        return fragmentEnabled;
    }

    public void propertyChanged( DataModelEvent event )
    {
        if( event.getPropertyName().equals( IPluginProjectDataModelProperties.LIFERAY_SDK_NAME ) ||
            event.getPropertyName().equals( IPluginProjectDataModelProperties.PORTLET_FRAMEWORK_ID ) )
        {

            updateFragmentEnabled( event.getDataModel() );
        }
    }

    public void setBundleId( String bundleId )
    {
        this.bundleId = bundleId;
    }

    public void setFragmentEnabled( boolean fragmentEnabled )
    {
        this.fragmentEnabled = fragmentEnabled;
    }

    public void setFrameworkId( String id )
    {
        this.frameworkId = id;
    }

    public void setIconUrl( String iconUrl )
    {
        this.iconUrl = iconUrl;
    }

    protected abstract Composite createNewProjectOptionsComposite( Composite parent );

    protected abstract void updateFragmentEnabled( IDataModel dataModel );

}
