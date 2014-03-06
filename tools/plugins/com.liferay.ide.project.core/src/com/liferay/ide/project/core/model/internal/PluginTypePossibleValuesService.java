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

import com.liferay.ide.project.core.model.PluginType;

import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;


/**
 * @author Kuo Zhang
 */
public class PluginTypePossibleValuesService extends PossibleValuesService
{

    @Override
    protected void compute( Set<String> values )
    {
        values.add( PluginType.portlet.name() );

        values.add( PluginType.servicebuilder.name() );

        values.add( PluginType.hook.name() );

        values.add( PluginType.layouttpl.name() );

        values.add( PluginType.theme.name() );

        values.add( PluginType.ext.name() );

        values.add( PluginType.web.name() );
    }

    @Override
    public boolean ordered()
    {
        return true;
    }

}
