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
 *******************************************************************************/

package com.liferay.ide.portlet.core.lfportlet.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;


/**
 * @author Simon Jiang
 */
@Image( path = "images/elcl16/custom.png" )
public interface CustomUserAttribute extends Element
{

    ElementType TYPE = new ElementType( CustomUserAttribute.class );

    // *** Custom Name ***

    @Type( base = CutomUserAttributeName.class )
    @Label( standard = "Custom Name" )
    @XmlListBinding
    (
        mappings = @XmlListBinding.Mapping
        (
            element = "name",
            type = CutomUserAttributeName.class
        )
    )    
    ListProperty PROP_CUSTOM_USER_ATTRIBUTE_NAMES = new ListProperty( TYPE, "CustomUserAttributeNames" );

    ElementList<CutomUserAttributeName> getCustomUserAttributeNames();

    // *** Custom Class ***

    @Label( standard = "Custom Class" )
    @XmlBinding( path = "custom-class" )
    @Type( base = JavaTypeName.class )
    @MustExist
    @Reference( target = JavaType.class )
    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portlet.CustomUserAttributes" )
    ValueProperty PROP_CUSTOM_CLASS = new ValueProperty( TYPE, "CustomClass" );

    ReferenceValue<JavaTypeName, JavaType> getCustomClass();

    void setCustomClass( JavaTypeName value );

    void setCustomClass( String value );

}
