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

package com.liferay.ide.portlet.jsf.core.operation;

import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
public class JSFPortletDescriptorHelper extends PortletDescriptorHelper
    implements INewJSFPortletClassDataModelProperties
{

    public JSFPortletDescriptorHelper( IProject project )
    {
        super( project );
    }

    @Override
    protected String getPortletClassText( IDataModel model )
    {
        return model.getStringProperty( JSF_PORTLET_CLASS );
    }

}
