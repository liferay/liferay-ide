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
 * Contributors:
 *      Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/
package com.liferay.ide.portal.core.structures.model;

import com.liferay.ide.portal.core.structures.model.internal.CDATAValueBinding;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


/**
 * @author Gregory Amerson
 */
public interface Structure extends Element
{

    ElementType TYPE = new ElementType( Structure.class );

    // *** Name ***

    @Label( standard = "name" )
    @XmlBinding( path = "name" )
    @CustomXmlValueBinding( impl = CDATAValueBinding.class )
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" ); //$NON-NLS-1$

    Value<String> getName();

    void setName( String value );

    // *** DynamicStructure ***

    @Type( base = Boolean.class )
    @Label( standard = "dynamic structure" )
    @XmlBinding( path = "dynamic-structure" )
    @DefaultValue( text = "true" )
    ValueProperty PROP_DEFAULT_LOCALE = new ValueProperty( TYPE, "DynamicStructure" ); //$NON-NLS-1$

    Value<Boolean> isDynamicStructure();

    void setDynamicStructure( String value );

    void setDynamicStructure( Boolean value );


    // *** Description ***

    @Label( standard = "description" )
    @XmlBinding( path = "description" )
    @CustomXmlValueBinding( impl = CDATAValueBinding.class )
    ValueProperty PROP_DESCRIPTION = new ValueProperty( TYPE, "Description" ); //$NON-NLS-1$

    Value<String> getDescription();

    void setDescription( String value );

    // *** StructureRoot ***

    @Type( base = StructureRoot.class )
    @Label( standard = "structure root" )
    @XmlBinding( path = "root" )
    ElementProperty PROP_ROOT = new ElementProperty( TYPE, "Root" ); //$NON-NLS-1$

    ElementHandle<StructureRoot> getRoot();

}
