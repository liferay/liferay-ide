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

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;


/**
 * @author Gregory Amerson
 */
@GenerateImpl
public interface DynamicElementMetadata extends IModelElement
{

    ModelElementType TYPE = new ModelElementType( DynamicElementMetadata.class );

    // *** Locale ***

    @Label( standard = "locale" )
    @XmlBinding( path = "@locale" )
    ValueProperty PROP_LOCALE = new ValueProperty( TYPE, "Locale" ); //$NON-NLS-1$

    Value<String> getLocale();

    void setLocale( String value );

    // *** Entries ***

    @Type( base = Entry.class )
    @Label( standard = "entries" )
    @XmlListBinding
    ( 
        mappings = 
        { 
            @XmlListBinding.Mapping
            (
                element = "entry",
                type = Entry.class 
            ) 
        }
    )
    ListProperty PROP_ENTRIES = new ListProperty( TYPE, "Entries" ); //$NON-NLS-1$

    ModelElementList<Entry> getEntries();

}
