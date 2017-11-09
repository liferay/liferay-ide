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

package com.liferay.ide.xml.search.ui.jsp;

import org.eclipse.wst.xml.search.core.statics.DefaultStaticValueVisitor;
import org.eclipse.wst.xml.search.core.statics.StaticValueQuerySpecification;

/**
 * @author Terry Jia
 */
public class AuiInputTypeQuerySpecification extends StaticValueQuerySpecification {

	public AuiInputTypeQuerySpecification() {
		super(_visitor);

		_visitor.registerValue("hidden", "The hidden type input field.");
		_visitor.registerValue("assetCategories", "The assetCategories type input field.");
		_visitor.registerValue("assetTags", "The assetTags type input field.");
		_visitor.registerValue(
			"checkbox",
			"The checkbox type input field.<br/>Notice when the \"type\" is checkbok" +
				", the \"value\" should been only \"true\" or \"false\".");
		_visitor.registerValue("radio", "The radio type input field.");
		_visitor.registerValue("timeZone", "The timeZone type input field.");
		_visitor.registerValue("text", "The text type input field.");
		_visitor.registerValue("textarea", "The textarea type input field.");
	}

	private static DefaultStaticValueVisitor _visitor = new DefaultStaticValueVisitor();

}