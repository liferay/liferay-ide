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

package com.liferay.ide.layouttpl.core.model;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Required;

/**
 * @author Kuo Zhang
 * @author Joye Luo
 */
public interface LayoutTplElement extends CanAddPortletLayouts
{

    ElementType TYPE = new ElementType( LayoutTplElement.class );

    // *** Role ***

    @DefaultValue( text = "main" )
    ValueProperty PROP_ROLE = new ValueProperty( TYPE, "Role" );

    Value<String> getRole();
    void setRole( String role );

    // *** Id ***

    @DefaultValue( text = "main-content" )
    ValueProperty PROP_ID = new ValueProperty( TYPE, "Id" );

    Value<String> getId();
    void setId( String id );

    // *** Class Name ***

    @Required
    ValueProperty PROP_ClASS_NAME = new ValueProperty( TYPE, "ClassName" );

    Value<String> getClassName();
    void setClassName( String className );

    // *** Style ***

    // only two styles, use Boolean, if there are more styles in the future, use subclasses
    @DefaultValue( text = "true" )
    @Required
    @Type( base = Boolean.class )
    ValueProperty PROP_BOOTSTRAP_STYLE = new ValueProperty( TYPE, "BootstrapStyle" );

    Value<Boolean> getBootstrapStyle();
    void setBootstrapStyle( String value );
    void setBootstrapStyle( Boolean value );

 // *** Version ***

    @Required
    @Type( base = Boolean.class )
    ValueProperty PROP_IS_62 = new ValueProperty( TYPE, "Is62" );

    Value<Boolean> getIs62();
    void setIs62( String value );
    void setIs62( Boolean value );

}
