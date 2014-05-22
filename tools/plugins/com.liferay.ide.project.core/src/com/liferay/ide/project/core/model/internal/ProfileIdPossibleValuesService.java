/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;


/**
 * @author Gregory Amerson
 */
public class ProfileIdPossibleValuesService extends PossibleValuesService
{

    private List<String> possibleValues = new ArrayList<String>();

    private void fillPossibleValues()
    {
        final NewLiferayPluginProjectOp op = op();

        Set<String> possibleProfileIds = NewLiferayPluginProjectOpMethods.getPossibleProfileIds( op, true );

        possibleValues.clear();
        possibleValues.addAll( possibleProfileIds );
    }

    @Override
    protected void compute( Set<String> values )
    {
        values.addAll( possibleValues );
    }


    @Override
    protected void initPossibleValuesService()
    {
        super.initPossibleValuesService();

        fillPossibleValues();
    }


    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }

    @Override
    public Status problem( Value<?> value )
    {
        return Status.createOkStatus();
    }

}
