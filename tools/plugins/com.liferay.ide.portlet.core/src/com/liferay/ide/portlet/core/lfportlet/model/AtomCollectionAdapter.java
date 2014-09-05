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
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


/**
 * @author Simon Jiang
 */
public interface AtomCollectionAdapter extends Element
{
    ElementType TYPE = new ElementType( AtomCollectionAdapter.class );

    // Atom Collection Adapter

    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.atom.AtomCollectionAdapter")
    @Label( standard = "Atom Collection Adatper" )
    @MustExist
    @XmlBinding( path = "" )
    ValueProperty PROP_VALUE = new ValueProperty( TYPE, "Value" );

    ReferenceValue<JavaTypeName, JavaType> getValue();

    void seValue( JavaTypeName value );

    void setValue( String value );
}
