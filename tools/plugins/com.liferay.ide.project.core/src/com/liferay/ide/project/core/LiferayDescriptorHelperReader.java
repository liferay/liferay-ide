/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.project.core;

import com.liferay.ide.core.ExtensionReader;
import com.liferay.ide.project.core.util.LiferayDescriptorHelper;

import org.eclipse.core.runtime.IConfigurationElement;


/**
 * @author Kuo Zhang
 */
public class LiferayDescriptorHelperReader extends ExtensionReader<LiferayDescriptorHelper>
{
    private static final String EXTENSION = "liferayDescriptorHelpers";
    private static final String HELPER_ELEMENT = "liferayDescriptorHelper";

    public LiferayDescriptorHelperReader()
    {
        super( LiferayProjectCore.PLUGIN_ID, EXTENSION, HELPER_ELEMENT );
    }

    @Override
    protected LiferayDescriptorHelper initElement( IConfigurationElement configElement, LiferayDescriptorHelper helper )
    {
        return helper;
    }

    public LiferayDescriptorHelper[] getHelpers()
    {
        return getExtensions().toArray( new LiferayDescriptorHelper[0] );
    }
}
