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
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/
package com.liferay.ide.portal.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;


/**
 * @author Gregory Amerson
 */
@XmlBinding( path = "root" )
public interface StructuresRoot extends Element
{

    ElementType TYPE = new ElementType( StructuresRoot.class );

    // *** AvailableLocales ***

    @Label( standard = "available locales" )
    @XmlBinding( path = "@available-locales" )
    ValueProperty PROP_AVAILABLE_LOCALES = new ValueProperty( TYPE, "AvailableLocales" ); //$NON-NLS-1$

    Value<String> getAvailableLocales();

    void setAvailableLocales( String value );

    // *** DefaultLocale ***

    @Label( standard = "default locale" )
    @XmlBinding( path = "@default-locale" )
    ValueProperty PROP_DEFAULT_LOCALE = new ValueProperty( TYPE, "DefaultLocale" ); //$NON-NLS-1$

    Value<String> getDefaultLocale();

    void setDefaultLocale( String value );

    // *** DynamicElements ***

    @Type( base = DynamicElement.class )
    @Label( standard = "dynamic elements" )
    @XmlListBinding
    (
        mappings =
        {
            @XmlListBinding.Mapping
            (
                element = "dynamic-element",
                type = DynamicElement.class
            )
        }
    )
    ListProperty PROP_DYNAMIC_ELEMENTS = new ListProperty( TYPE, "DynamicElements" ); //$NON-NLS-1$

    ElementList<DynamicElement> getDynamicElements();




}
