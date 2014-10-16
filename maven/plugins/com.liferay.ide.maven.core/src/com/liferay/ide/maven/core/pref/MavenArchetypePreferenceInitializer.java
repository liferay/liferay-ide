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

public class MavenArchetypePreferenceInitializer extends AbstractPreferenceInitializer
{

    public MavenArchetypePreferenceInitializer()
    {
        super();
    }

    @Override
    public void initializeDefaultPreferences()
    {
        final IEclipsePreferences defaultPrefs = LiferayMavenCore.getDefaultPrefs();
        defaultPrefs.put( LiferayMavenCore.PREF_ARCHETYPE_GAV_MVC_PORTLET, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_ARCHETYPE_GAV_JSF, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_ARCHETYPE_GAV_VAADIN, "7.4.0.alpha2" );
        defaultPrefs.put( LiferayMavenCore.PREF_ARCHETYPE_GAV_FACES_ALLOY, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_ARCHETYPE_GAV_ICEFACES, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_ARCHETYPE_GAV_PRIMEFACES, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_ARCHETYPE_GAV_RICHFACES, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_ARCHETYPE_GAV_HOOK, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_ARCHETYPE_GAV_SERVICEBUILDER, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_ARCHETYPE_GAV_THEME, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_ARCHETYPE_GAV_LAYOUTTPL, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_ARCHETYPE_GAV_EXT, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_ARCHETYPE_GAV_WEB, "6.2.1" );
    }

}
