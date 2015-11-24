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

package com.liferay.ide.project.core.modules;

import com.liferay.blade.api.ProjectTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;


/**
 * @author Simon Jiang
 */
public class ModuleProjectTypePossibleValuesService extends PossibleValuesService
{
    private List<String> possibleValues;

    @Override
    protected void initPossibleValuesService()
    {
        possibleValues = new ArrayList<String>();

        final ProjectTemplate[] templates = ModulesUtil.getProjectTemplates();

        for( ProjectTemplate template : templates )
        {
            possibleValues.add( template.name() );
        }
    }

    @Override
    protected void compute( Set<String> values )
    {
        values.addAll( possibleValues );
    }

    @Override
    public boolean ordered()
    {
        return true;
    }
}
