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

package com.liferay.ide.project.core.jsf;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.DefaultValueService;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.ProjectCore;

/**
 * @author Simon Jiang
 */
public class JSFModuleProjectProviderDefaultValueService extends DefaultValueService
{
    @Override
    protected String compute()
    {
        String defaultProjectBuildType = "maven-jsf";

        final IScopeContext[] prefContexts = { DefaultScope.INSTANCE, InstanceScope.INSTANCE };
        final String buildType =
            Platform.getPreferencesService().getString(
                ProjectCore.PLUGIN_ID, ProjectCore.PREF_DEFAULT_JSF_MODULE_PROJECT_BUILD_TYPE_OPTION, null,
                    prefContexts );

        if( buildType != null )
        {
            final ILiferayProjectProvider provider = LiferayCore.getProvider( buildType );

            if (provider != null)
            {
                return provider.getShortName();
            }
        }

        return defaultProjectBuildType;
    }
}
