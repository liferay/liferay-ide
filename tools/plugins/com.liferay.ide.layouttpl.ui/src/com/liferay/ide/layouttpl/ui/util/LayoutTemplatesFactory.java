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

package com.liferay.ide.layouttpl.ui.util;

import com.liferay.ide.layouttpl.core.model.CanAddPortletLayouts;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;

import org.eclipse.sapphire.Value;

/**
 * @author Kuo Zhang
 */
public class LayoutTemplatesFactory {

	public static void add_Layout_1(LayoutTplElement layoutTpl) {
		PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();

		PortletColumnElement column = row.getPortletColumns().insert();

		column.setWeight(column.getFullWeight().content());
	}

	public static void add_Layout_1_2_1(LayoutTplElement layoutTpl) {
		PortletLayoutElement row1 = layoutTpl.getPortletLayouts().insert();

		PortletColumnElement column11 = row1.getPortletColumns().insert();

		int fullWeight = column11.getFullWeight().content();

		column11.setWeight(fullWeight);

		PortletLayoutElement row2 = layoutTpl.getPortletLayouts().insert();

		PortletColumnElement column21 = row2.getPortletColumns().insert();

		column21.setWeight(fullWeight / 2);

		PortletColumnElement column22 = row2.getPortletColumns().insert();

		column22.setWeight(fullWeight / 2);

		PortletLayoutElement row3 = layoutTpl.getPortletLayouts().insert();

		PortletColumnElement column31 = row3.getPortletColumns().insert();

		column31.setWeight(fullWeight);
	}

	public static void add_Layout_1_2_I(LayoutTplElement layoutTpl) {
		PortletLayoutElement row1 = layoutTpl.getPortletLayouts().insert();

		PortletColumnElement column11 = row1.getPortletColumns().insert();

		column11.setWeight(column11.getFullWeight().content());

		PortletLayoutElement row2 = layoutTpl.getPortletLayouts().insert();

		PortletColumnElement column21 = row2.getPortletColumns().insert();
		PortletColumnElement column22 = row2.getPortletColumns().insert();

		if (_isBootstrapStyle(layoutTpl)) {
			column21.setWeight(4);
			column22.setWeight(8);
		}
		else {
			column21.setWeight(30);
			column22.setWeight(70);
		}
	}

	public static void add_Layout_1_2_II(LayoutTplElement layoutTpl) {
		PortletLayoutElement row1 = layoutTpl.getPortletLayouts().insert();

		PortletColumnElement column11 = row1.getPortletColumns().insert();

		column11.setWeight(column11.getFullWeight().content());

		PortletLayoutElement row2 = layoutTpl.getPortletLayouts().insert();

		PortletColumnElement column21 = row2.getPortletColumns().insert();
		PortletColumnElement column22 = row2.getPortletColumns().insert();

		if (_isBootstrapStyle(layoutTpl)) {
			column21.setWeight(8);
			column22.setWeight(4);
		}
		else {
			column21.setWeight(70);
			column22.setWeight(30);
		}
	}

	public static void add_Layout_2_2(LayoutTplElement layoutTpl) {
		PortletLayoutElement row1 = layoutTpl.getPortletLayouts().insert();

		PortletColumnElement column11 = row1.getPortletColumns().insert();
		PortletColumnElement column12 = row1.getPortletColumns().insert();

		PortletLayoutElement row2 = layoutTpl.getPortletLayouts().insert();

		PortletColumnElement column21 = row2.getPortletColumns().insert();
		PortletColumnElement column22 = row2.getPortletColumns().insert();

		if (_isBootstrapStyle(layoutTpl)) {
			column11.setWeight(8);
			column12.setWeight(4);
			column21.setWeight(4);
			column22.setWeight(8);
		}
		else {
			column11.setWeight(70);
			column12.setWeight(30);
			column21.setWeight(30);
			column22.setWeight(70);
		}
	}

	public static void add_Layout_2_I(LayoutTplElement layoutTpl) {
		add_Row_2_I(layoutTpl);
	}

	public static void add_Layout_2_II(LayoutTplElement layoutTpl) {
		add_Row_2_II(layoutTpl);
	}

	public static void add_Layout_2_III(LayoutTplElement layoutTpl) {
		add_Row_2_III(layoutTpl);
	}

	public static void add_Layout_3(LayoutTplElement layoutTpl) {
		add_Row_3(layoutTpl);
	}

	public static void add_Row_1(CanAddPortletLayouts element) {
		PortletLayoutElement row = element.getPortletLayouts().insert();

		PortletColumnElement column = row.getPortletColumns().insert();

		column.setWeight(column.getFullWeight().content());
	}

	public static void add_Row_2_I(CanAddPortletLayouts element) {
		PortletLayoutElement row = element.getPortletLayouts().insert();

		PortletColumnElement column1 = row.getPortletColumns().insert();
		PortletColumnElement column2 = row.getPortletColumns().insert();

		int fullWeight = column1.getFullWeight().content();

		column1.setWeight(fullWeight / 2);
		column2.setWeight(fullWeight / 2);
	}

	public static void add_Row_2_II(CanAddPortletLayouts element) {
		PortletLayoutElement row = element.getPortletLayouts().insert();

		PortletColumnElement column1 = row.getPortletColumns().insert();
		PortletColumnElement column2 = row.getPortletColumns().insert();

		if (_isBootstrapStyle(element)) {
			column1.setWeight(4);
			column2.setWeight(8);
		}
		else {
			column1.setWeight(30);
			column2.setWeight(70);
		}
	}

	public static void add_Row_2_III(CanAddPortletLayouts element) {
		PortletLayoutElement row = element.getPortletLayouts().insert();

		PortletColumnElement column1 = row.getPortletColumns().insert();
		PortletColumnElement column2 = row.getPortletColumns().insert();

		if (_isBootstrapStyle(element)) {
			column1.setWeight(8);
			column2.setWeight(4);
		}
		else {
			column1.setWeight(70);
			column2.setWeight(30);
		}
	}

	public static void add_Row_3(CanAddPortletLayouts element) {
		PortletLayoutElement row = element.getPortletLayouts().insert();

		PortletColumnElement column1 = row.getPortletColumns().insert();
		PortletColumnElement column2 = row.getPortletColumns().insert();
		PortletColumnElement column3 = row.getPortletColumns().insert();

		int fullWeight = column1.getFullWeight().content();

		column1.setWeight(fullWeight / 3);
		column2.setWeight(fullWeight / 3);
		column3.setWeight(fullWeight / 3);
	}

	private static boolean _isBootstrapStyle(CanAddPortletLayouts element) {
		LayoutTplElement layoutTplElement = element.nearest(LayoutTplElement.class);

		Value<Boolean> bootstrapStyle = layoutTplElement.getBootstrapStyle();

		return bootstrapStyle.content();
	}

}