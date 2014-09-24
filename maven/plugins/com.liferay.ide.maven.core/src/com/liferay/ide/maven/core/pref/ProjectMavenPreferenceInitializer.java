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
package com.liferay.ide.maven.core.pref;

import com.liferay.ide.maven.core.LiferayMavenCore;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Simon Jiang
 */

public class ProjectMavenPreferenceInitializer extends AbstractPreferenceInitializer
{

    public ProjectMavenPreferenceInitializer()
    {
        super();
    }

    @Override
    public void initializeDefaultPreferences()
    {
        final IEclipsePreferences defaultPrefs = LiferayMavenCore.getDefaultPrefs();
        defaultPrefs.putBoolean(
            LiferayMavenCore.PREF_DISABLE_CUSTOM_JSP_VALIDATION, true );
    }

}
