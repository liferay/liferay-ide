/**
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
 */

package com.liferay.ide.xml.search.ui.service;

import org.eclipse.wst.xml.search.core.statics.DefaultStaticValueVisitor;
import org.eclipse.wst.xml.search.core.statics.StaticValueQuerySpecification;

/**
 * @author Terry Jia
 */
public class ColumnTypeQuerySpecification extends StaticValueQuerySpecification {

	public ColumnTypeQuerySpecification() {
		super(_visitor);

		_visitor.registerValue("BigDecimal", "");
		_visitor.registerValue("Blob", "");
		_visitor.registerValue("boolean", "");
		_visitor.registerValue("Boolean", "");
		_visitor.registerValue("Collection", "");
		_visitor.registerValue("Date", "");
		_visitor.registerValue("double", "");
		_visitor.registerValue("Double", "");
		_visitor.registerValue("float", "");
		_visitor.registerValue("Float", "");
		_visitor.registerValue("int", "");
		_visitor.registerValue("Integer", "");
		_visitor.registerValue("long", "");
		_visitor.registerValue("Long", "");
		_visitor.registerValue("Map", "");
		_visitor.registerValue("short", "");
		_visitor.registerValue("Short", "");
		_visitor.registerValue("String", "");
	}

	private static DefaultStaticValueVisitor _visitor = new DefaultStaticValueVisitor();

}