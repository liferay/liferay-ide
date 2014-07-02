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
 *      Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/
package com.liferay.ide.portal.core.structures.model;

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


/**
 * @author Gregory Amerson
 */
public interface DynamicElement extends HasDynamicElements
{

    ElementType TYPE = new ElementType( DynamicElement.class );

    // *** DataType ***

    @Label( standard = "data type" )
    @XmlBinding( path = "@dataType" )
    ValueProperty PROP_DATA_TYPE = new ValueProperty( TYPE, "DataType" ); //$NON-NLS-1$

    Value<String> getDataType();

    void setDataType( String value );

    // *** Required ***

    @Type( base = Boolean.class )
    @Label( standard = "required" )
    @XmlBinding( path = "@required" )
    ValueProperty PROP_REQUIRED = new ValueProperty( TYPE, "Required" ); //$NON-NLS-1$

    Value<Boolean> isRequired();

    void setRequired( String value );

    void setRequired( Boolean value );

    // *** ShowLabel ***

    @Type( base = Boolean.class )
    @Label( standard = "show label" )
    @XmlBinding( path = "@showLabel" )
    ValueProperty PROP_SHOW_LABEL = new ValueProperty( TYPE, "ShowLabel" ); //$NON-NLS-1$

    Value<Boolean> isShowLabel();

    void setShowLabel( String value );

    void setShowLabel( Boolean value );


    // *** Value ***

    @Label( standard = "value" )
    @XmlBinding( path = "@value" )
    ValueProperty PROP_VALUE = new ValueProperty( TYPE, "Value" ); //$NON-NLS-1$

    Value<String> getValue();

    void setValue( String value );


    // *** Multiple ***

    @Type( base = Boolean.class )
    @Label( standard = "multiple" )
    @XmlBinding( path = "@multiple" )
    ValueProperty PROP_MULTIPLE = new ValueProperty( TYPE, "Multiple" ); //$NON-NLS-1$

    Value<Boolean> isMultiple();

    void setMultiple( String value );

    void setMultiple( Boolean value );


    // *** ReadOnly ***

    @Type( base = Boolean.class )
    @Label( standard = "read only" )
    @XmlBinding( path = "@readOnly" )
    ValueProperty PROP_READ_ONLY = new ValueProperty( TYPE, "ReadOnly" ); //$NON-NLS-1$

    Value<Boolean> isReadOnly();

    void setReadOnly( String value );

    void setReadOnly( Boolean value );


    // *** IndexType ***

    @Label( standard = "index type" )
    @XmlBinding( path = "@indexType" )
    ValueProperty PROP_INDEX_TYPE = new ValueProperty( TYPE, "IndexType" ); //$NON-NLS-1$

    Value<String> getIndexType();

    void setIndexType( String value );

    // *** FieldNamespace ***

    @Label( standard = "field namespace" )
    @XmlBinding( path = "@fieldNamespace" )
    ValueProperty PROP_FIELD_NAMESPACE = new ValueProperty( TYPE, "FieldNamespace" ); //$NON-NLS-1$

    Value<String> getFieldNamespace();

    void setFieldNamespace( String value );

    // *** Name ***

    @Label( standard = "name" )
    @XmlBinding( path = "@name" )
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" ); //$NON-NLS-1$

    Value<String> getName();

    void setName( String value );

    // *** Type ***

    @Label( standard = "type" )
    @XmlBinding( path = "@type" )
    ValueProperty PROP_TYPE = new ValueProperty( TYPE, "Type" ); //$NON-NLS-1$

    Value<String> getType();

    void setType( String value );

    // *** Metadata ***

    @Type( base = DynamicElementMetadata.class )
    @Label( standard = "metadata" )
    @XmlBinding( path = "meta-data" )
    ElementProperty PROP_METADATA = new ElementProperty( TYPE, "Metadata" ); //$NON-NLS-1$

    ElementHandle<DynamicElementMetadata> getMetadata();

}
