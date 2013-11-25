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
package com.liferay.ide.project.core.model;

import com.liferay.ide.project.core.model.internal.RuntimeNameDefaultValueService;
import com.liferay.ide.project.core.model.internal.RuntimeNamePossibleValuesService;
import com.liferay.ide.project.core.model.internal.NewLiferayProfileRuntimeValidationService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;


/**
 * @author Gregory Amerson
 */
public interface HasLiferayRuntime extends Element
{
    ElementType TYPE = new ElementType( HasLiferayRuntime.class );

    // *** RuntimeName ***

    @Label( standard = "runtime" )
    @Services
    (
        value =
        {
            @Service( impl = RuntimeNamePossibleValuesService.class ),
            @Service( impl = RuntimeNameDefaultValueService.class ),
        }
    )
    ValueProperty PROP_RUNTIME_NAME = new ValueProperty( TYPE, "RuntimeName" ); //$NON-NLS-1$

    Value<String> getRuntimeName();
    void setRuntimeName( String value );
}
