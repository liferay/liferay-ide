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
public class AuiValidatorNameQuerySpecification extends StaticValueQuerySpecification {

	public AuiValidatorNameQuerySpecification() {
		super(_visitor);

		// For validator list to see the alloyui
		// website:http://alloyui.com/api/files/alloy-ui_src_aui-form-validator_js_aui-form-validator.js.html

		_visitor.registerValue("acceptFiles", "The acceptFiles type validator.");
		_visitor.registerValue("alpha", "The alpha type validato.");
		_visitor.registerValue("iri", "The iri type validator.");
		_visitor.registerValue("alphanum", "The alphanum type validator.");
		_visitor.registerValue("date", "The date type validator.");
		_visitor.registerValue("digits", "The digits type validator.");
		_visitor.registerValue("email", "The email type validator.");
		_visitor.registerValue("equalTo", "The equalTo type validator.");
		_visitor.registerValue("max", "The max type validator.");
		_visitor.registerValue("maxLength", "The maxLength type validator.");
		_visitor.registerValue("min", "The min type validator.");
		_visitor.registerValue("minLength", "The minLength type validator.");
		_visitor.registerValue("number", "The number type validator.");
		_visitor.registerValue("range", "The range type validator.");
		_visitor.registerValue("rangeLength", "The rangeLength type validator.");
		_visitor.registerValue("required", "The required type validator.");
		_visitor.registerValue("url", "The url type validator.");
	}

	private static DefaultStaticValueVisitor _visitor = new DefaultStaticValueVisitor();

}