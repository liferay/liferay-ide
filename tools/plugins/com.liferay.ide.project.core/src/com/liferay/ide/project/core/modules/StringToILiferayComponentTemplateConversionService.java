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

import org.eclipse.sapphire.ConversionException;
import org.eclipse.sapphire.ConversionService;

import com.liferay.ide.project.core.ProjectCore;


/**
 * @author Simon Jiang
 */
@SuppressWarnings( "rawtypes" )
public class StringToILiferayComponentTemplateConversionService extends ConversionService<String, IComponentTemplate>
{

    public StringToILiferayComponentTemplateConversionService()
    {
        super( String.class, IComponentTemplate.class );
    }

    @Override
    public IComponentTemplate convert( String object ) throws ConversionException
    {
        return ProjectCore.getComponentTemplate( object );
    }
}
