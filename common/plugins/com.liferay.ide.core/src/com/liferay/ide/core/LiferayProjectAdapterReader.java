/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
package com.liferay.ide.core;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Gregory Amerson
 */
public class LiferayProjectAdapterReader extends ExtensionReader<ILiferayProjectAdapter>
{

    private static final String EXTENSION = "liferayProjectAdapters"; //$NON-NLS-1$
    private static final String PROVIDER_ELEMENT = "liferayProjectAdapter"; //$NON-NLS-1$

    public LiferayProjectAdapterReader()
    {
        super( LiferayCore.PLUGIN_ID, EXTENSION, PROVIDER_ELEMENT );
    }

    @Override
    protected ILiferayProjectAdapter initElement( IConfigurationElement configElement, ILiferayProjectAdapter adapter )
    {
        return adapter;
    }

}
