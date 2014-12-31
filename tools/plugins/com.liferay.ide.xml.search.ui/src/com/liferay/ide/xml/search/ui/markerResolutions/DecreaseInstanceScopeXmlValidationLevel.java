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
 *******************************************************************************/

package com.liferay.ide.xml.search.ui.markerResolutions;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.ValidationPreferences;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;


/**
 * @author Kuo Zhang
 */
public class DecreaseInstanceScopeXmlValidationLevel extends DecreaseXMLValidationLevel
{

    public DecreaseInstanceScopeXmlValidationLevel()
    {
    }

    private final static String MESSAGE = "Decrease validation level of this marker for all projects to Ignore";

    @Override
    public String getDescription()
    {
        return getLabel();
    }

    @Override
    public String getLabel()
    {
        return MESSAGE;
    }

    @Override
    protected void resolve( IMarker marker )
    {
        // if the project scope is used, set its validation level to "Ignore" first.
        final IEclipsePreferences node =
            new ProjectScope( marker.getResource().getProject() ).getNode( ProjectCore.PLUGIN_ID );

        if( node.getBoolean( ProjectCore.USE_PROJECT_SETTINGS ,false ) )
        {
            ValidationPreferences.setProjectScopeValLevel( marker.getResource().getProject(), -1 );
        }

        ValidationPreferences.setInstanceScopeValLevel( -1 );
    }

}
