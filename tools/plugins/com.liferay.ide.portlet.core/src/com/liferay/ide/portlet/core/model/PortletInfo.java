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
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
@Image( path = "images/obj16/info_obj.gif" )
public interface PortletInfo extends Element, Identifiable
{

    ElementType TYPE = new ElementType( PortletInfo.class );

    // *** Title ***

    @Label( standard = "Title" )
    @XmlBinding( path = "title" )
    @CountConstraint( min = 0, max = 1 )
    ValueProperty PROP_TITLE = new ValueProperty( TYPE, "Title" ); //$NON-NLS-1$

    Value<String> getTitle();

    void setTitle( String value );

    // *** ShortTitle ***

    @Label( standard = "Short Title" )
    @XmlBinding( path = "short-title" )
    @CountConstraint( min = 0, max = 1 )
    ValueProperty PROP_SHORT_TITLE = new ValueProperty( TYPE, "ShortTitle" ); //$NON-NLS-1$

    Value<String> getShortTitle();

    void setShortTitle( String value );

    // *** Keywords ***

    @Label( standard = "Keywords" )
    @XmlBinding( path = "keywords" )
    @CountConstraint( min = 0, max = 1 )
    ValueProperty PROP_KEYWORDS = new ValueProperty( TYPE, "Keywords" ); //$NON-NLS-1$

    Value<String> getKeywords();

    void setKeywords( String value );

}
