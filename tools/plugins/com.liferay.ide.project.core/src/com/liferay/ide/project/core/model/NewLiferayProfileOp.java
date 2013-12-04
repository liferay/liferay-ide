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

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Validation;
import org.eclipse.sapphire.ValueProperty;


/**
 * @author Gregory Amerson
 */
public interface NewLiferayProfileOp extends NewLiferayPluginProjectOp
{
    ElementType TYPE = new ElementType( NewLiferayProfileOp.class );


    // we don't want to validated for missing project names
    @Validation( rule = "true", message = ""  )
    ValueProperty PROP_PROJECT_NAME = new ValueProperty( TYPE, "ProjectName" ); //$NON-NLS-1$

}
