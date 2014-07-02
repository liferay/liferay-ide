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
package com.liferay.ide.adt.core;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;


/**
 * @author Gregory Amerson
 */
public class DefaultPreferenceInitializer extends AbstractPreferenceInitializer
{

    public DefaultPreferenceInitializer()
    {
        super();
    }

    @Override
    public void initializeDefaultPreferences()
    {
        DefaultScope.INSTANCE.getNode( ADTCore.PLUGIN_ID ).put(
            ADTCore.PREF_PROJECT_TEMPLATE_LOCATION_OPTION, ADTCore.VALUE_USE_EMBEDDED_TEMPLATE );
    }

}
