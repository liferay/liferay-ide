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

package com.liferay.ide.xml.search.ui.service;

import org.eclipse.wst.xml.search.core.statics.DefaultStaticValueVisitor;
import org.eclipse.wst.xml.search.core.statics.StaticValueQuerySpecification;

/**
 * @author Terry Jia
 */
public class FinderColumnComparatorQuerySpecification extends StaticValueQuerySpecification
{

    private static DefaultStaticValueVisitor visitor = new DefaultStaticValueVisitor();

    public FinderColumnComparatorQuerySpecification()
    {
        super( visitor );

        visitor.registerValue( "=", "=" );
        visitor.registerValue( "!=", "!=" );
        visitor.registerValue( "&lt;", "<" );
        visitor.registerValue( "&lt;=", "<=" );
        visitor.registerValue( "&gt;", ">" );
        visitor.registerValue( "&gt=", ">=" );
        visitor.registerValue( "LIKE", "LIKE" );
    }

}
