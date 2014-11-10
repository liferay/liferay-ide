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

package com.liferay.ide.xml.search.ui.jsp;

import com.liferay.ide.xml.search.ui.base.StaticValueQuerySpecification;

import org.eclipse.wst.xml.search.core.statics.DefaultStaticValueVisitor;

/**
 * @author Terry Jia
 */
public class AuiInputTypeQuerySpecification extends StaticValueQuerySpecification
{

    private static DefaultStaticValueVisitor visitor = new DefaultStaticValueVisitor();

    public AuiInputTypeQuerySpecification()
    {
        super( visitor );

        visitor.registerValue( "hidden", "The hidden type input field." );
        visitor.registerValue( "assetCategories", "The assetCategories type input field." );
        visitor.registerValue( "assetTags", "The assetTags type input field." );
        visitor.registerValue(
            "checkbox",
            "The checkbox type input field.<br/>Notice when the \"type\" is checkbok, the \"value\" should been only \"true\" or \"false\"." );
        visitor.registerValue( "radio", "The radio type input field." );
        visitor.registerValue( "timeZone", "The timeZone type input field." );
        visitor.registerValue( "text", "The text type input field." );
        visitor.registerValue( "textarea", "The textarea type input field." );
    }

}
