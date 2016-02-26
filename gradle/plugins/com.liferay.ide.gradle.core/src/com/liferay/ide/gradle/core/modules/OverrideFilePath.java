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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.gradle.core.modules;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Terry Jia
 */
@Image( path = "images/jsp_type_16x16.gif" )
public interface OverrideFilePath extends Element
{

    ElementType TYPE = new ElementType( OverrideFilePath.class );

    // *** Value ***

    @Label( standard = "Override File Path" )
    @Unique
    @Service( impl = OverrideFilePathPossibleValuesService.class )
    @Required
    ValueProperty PROP_VALUE = new ValueProperty( TYPE, "Value" );

    Value<String> getValue();
    void setValue( String value );
}
