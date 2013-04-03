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

package com.liferay.ide.layouttpl.ui.model;

import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;


/**
 * @author Gregory Amerson
 */
public class PortletLayout extends PortletLayoutElement implements IPropertySource
{
    /** An empty property descriptor. */
    private static final IPropertyDescriptor[] EMPTY_ARRAY = new IPropertyDescriptor[0];

    public IPropertyDescriptor[] getPropertyDescriptors()
    {
        return EMPTY_ARRAY;
    }
}
