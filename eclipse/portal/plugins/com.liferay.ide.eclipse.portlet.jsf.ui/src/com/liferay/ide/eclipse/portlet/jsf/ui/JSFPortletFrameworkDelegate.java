/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.portlet.jsf.ui;

import com.liferay.ide.eclipse.portlet.jsf.core.JSFPortletFramework;
import com.liferay.ide.eclipse.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.eclipse.project.ui.AbstractPortletFrameworkDelegate;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
public class JSFPortletFrameworkDelegate extends AbstractPortletFrameworkDelegate {

	public JSFPortletFrameworkDelegate() {
		super();
	}

	public Composite createNewProjectOptionsComposite( Composite parent ) {
		return null;
	}

	@Override
	protected void updateFragmentEnabled( IDataModel dataModel ) {
		Object framework = dataModel.getProperty( IPluginProjectDataModelProperties.PORTLET_FRAMEWORK );

		if ( framework instanceof JSFPortletFramework ) {
			dataModel.setBooleanProperty( IPluginProjectDataModelProperties.PLUGIN_FRAGMENT_ENABLED, false );
		}
	}

}
