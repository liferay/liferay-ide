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
public class AuiValidatorNameQuerySpecification extends StaticValueQuerySpecification
{

    private static DefaultStaticValueVisitor visitor = new DefaultStaticValueVisitor();

    public AuiValidatorNameQuerySpecification()
    {
        super( visitor );

        //For validator list to see the alloyui website:http://alloyui.com/api/files/alloy-ui_src_aui-form-validator_js_aui-form-validator.js.html
        visitor.registerValue( "acceptFiles", "The acceptFiles type validator." );
        visitor.registerValue( "alpha", "The alpha type validato." );
        visitor.registerValue( "iri", "The iri type validator." );
        visitor.registerValue( "alphanum", "The alphanum type validator." );
        visitor.registerValue( "date", "The date type validator." );
        visitor.registerValue( "digits", "The digits type validator." );
        visitor.registerValue( "email", "The email type validator." );
        visitor.registerValue( "equalTo", "The equalTo type validator." );
        visitor.registerValue( "max", "The max type validator." );
        visitor.registerValue( "maxLength", "The maxLength type validator." );
        visitor.registerValue( "min", "The min type validator." );
        visitor.registerValue( "minLength", "The minLength type validator." );
        visitor.registerValue( "number", "The number type validator." );
        visitor.registerValue( "range", "The range type validator." );
        visitor.registerValue( "rangeLength", "The rangeLength type validator." );
        visitor.registerValue( "required", "The required type validator." );
        visitor.registerValue( "url", "The url type validator." );
    }

}
