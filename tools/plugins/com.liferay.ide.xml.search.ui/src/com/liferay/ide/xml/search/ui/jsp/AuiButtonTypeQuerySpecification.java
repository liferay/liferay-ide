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
public class AuiButtonTypeQuerySpecification extends StaticValueQuerySpecification
{

    private static DefaultStaticValueVisitor visitor = new DefaultStaticValueVisitor();

    public AuiButtonTypeQuerySpecification()
    {
        super( visitor );

        visitor.registerValue( "submit", "The submit type button" );
        visitor.registerValue( "cancel", "The cancel type button" );
        visitor.registerValue( "reset", "The reset type button" );
    }

}
