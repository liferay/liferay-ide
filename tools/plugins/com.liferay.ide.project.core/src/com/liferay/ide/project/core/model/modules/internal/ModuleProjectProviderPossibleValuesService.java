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
package com.liferay.ide.project.core.model.modules.internal;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.ILiferayModuleProjectProvider;
import com.liferay.ide.project.core.model.internal.ProjectProviderPossibleValuesService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * @author Simon Jiang
 */
public class ModuleProjectProviderPossibleValuesService extends ProjectProviderPossibleValuesService
{
    private List<String> possibleValues;

    @Override
    protected void initPossibleValuesService()
    {
        super.initPossibleValuesService();

        possibleValues = new ArrayList<String>();

        for( final ILiferayProjectProvider provider : LiferayCore.getProviders() )
        {
            if( provider instanceof ILiferayModuleProjectProvider )
            {
                possibleValues.add( provider.getShortName() );
            }
        }

        Collections.sort( possibleValues );
    }

    @Override
    protected void compute( Set<String> values )
    {
        values.addAll( this.possibleValues );
    }

    @Override
    public boolean ordered()
    {
        return true;
    }

}
