/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.portal.core.model;

import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


/**
 * @author Gregory Amerson
 */
@GenerateImpl
public interface DynamicElement extends IModelElement
{

    ModelElementType TYPE = new ModelElementType( DynamicElement.class );

    // *** DataType ***

    @Label( standard = "data type" )
    @XmlBinding( path = "@dataType" )
    ValueProperty PROP_DATA_TYPE = new ValueProperty( TYPE, "DataType" ); //$NON-NLS-1$

    Value<String> getDataType();

    void setDataType( String value );

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

    ModelElementHandle<DynamicElementMetadata> getMetadata();

}
