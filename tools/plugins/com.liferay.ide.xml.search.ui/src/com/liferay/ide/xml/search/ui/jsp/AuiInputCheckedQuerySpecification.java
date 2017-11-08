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
public class AuiInputCheckedQuerySpecification extends StaticValueQuerySpecification {

	public AuiInputCheckedQuerySpecification() {
		super(_visitor);

		_visitor.registerValue(
			"true", "True.<br/>The \"checked\" only work as the \"type\" is \"checkbox\" or \"radio\".");
		_visitor.registerValue(
			"false", "False.<br/>The \"checked\" only work as the \"type\" is \"checkbox\" or \"radio\".");
	}

	private static DefaultStaticValueVisitor _visitor = new DefaultStaticValueVisitor();

}