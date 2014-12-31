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

import com.liferay.ide.project.core.ValidationPreferences;

import org.eclipse.core.resources.IMarker;


/**
 * @author Kuo Zhang
 */
public class DecreaseProjectScopeXmlValidationLevel extends DecreaseXMLValidationLevel
{

    public DecreaseProjectScopeXmlValidationLevel()
    {
    }

    private final static String MESSAGE = "Decrease validation level of this marker for this project to Ignore";

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
        ValidationPreferences.setProjectScopeValLevel( marker.getResource().getProject(), -1 );
    }

}
