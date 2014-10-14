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
        defaultPrefs.put( LiferayMavenCore.PREF_MAVEN_MVC_ARCHETYPE, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_MAVEN_JSF_ARCHETYPE, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_MAVEN_VAADIN_ARCHETYPE, "7.4.0.alpha2" );
        defaultPrefs.put( LiferayMavenCore.PREF_MAVEN_FACES_ALLOY_ARCHETYPE, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_MAVEN_ICEFACES_ARCHETYPE, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_MAVEN_PRIMEFACES_ARCHETYPE, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_MAVEN_RICHFACES_ARCHETYPE, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_MAVEN_HOOK_ARCHETYPE, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_MAVEN_SERVICE_ARCHETYPE, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_MAVEN_THEME_ARCHETYPE, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_MAVEN_LAYOUTTPL_ARCHETYPE, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_MAVEN_EXT_ARCHETYPE, "6.2.1" );
        defaultPrefs.put( LiferayMavenCore.PREF_MAVEN_WEB_ARCHETYPE, "6.2.1" );
    }

}
