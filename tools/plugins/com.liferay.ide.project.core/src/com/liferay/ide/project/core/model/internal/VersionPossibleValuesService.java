/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import java.util.Collections;
import java.util.Set;

import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.services.PossibleValuesService;


/**
 * @author Gregory Amerson
 */
public class VersionPossibleValuesService extends PossibleValuesService
{

    @Override
    protected void fillPossibleValues( Set<String> values )
    {
        final ILiferayProjectProvider projectProvider = op().getProjectProvider().content();

        final String[] versions = projectProvider.getPossibleVersions();

        Collections.addAll( values, versions );
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }

    @Override
    public boolean ordered()
    {
        return true;
    }

    @Override
    public Severity getInvalidValueSeverity( String invalidValue )
    {
        return Severity.OK;
    }

}
