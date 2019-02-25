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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.layouttpl.core.model.CanAddPortletLayouts;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Value;

/**
 * @author Kuo Zhang
 */
public class LayoutTemplatesFactory implements SapphireContentAccessor {

	public static void add_Row_2_III(CanAddPortletLayouts element) {
		ElementList<PortletLayoutElement> layouts = element.getPortletLayouts();

		PortletLayoutElement row = layouts.insert();

		ElementList<PortletColumnElement> columns = row.getPortletColumns();

		PortletColumnElement column1 = columns.insert();
		PortletColumnElement column2 = columns.insert();

		if (_isBootstrapStyle(element)) {
			column1.setWeight(8);
			column2.setWeight(4);
		}
		else {
			column1.setWeight(70);
			column2.setWeight(30);
		}
	}

	public void add_Layout_1(LayoutTplElement layoutTpl) {
		ElementList<PortletLayoutElement> layouts = layoutTpl.getPortletLayouts();

		PortletLayoutElement row = layouts.insert();

		ElementList<PortletColumnElement> columns = row.getPortletColumns();

		PortletColumnElement column = columns.insert();

		column.setWeight(get(column.getFullWeight()));
	}

	public void add_Layout_1_2_1(LayoutTplElement layoutTpl) {
		ElementList<PortletLayoutElement> layouts = layoutTpl.getPortletLayouts();

		PortletLayoutElement row1 = layouts.insert();

		ElementList<PortletColumnElement> columns1 = row1.getPortletColumns();

		PortletColumnElement column11 = columns1.insert();

		int fullWeight = get(column11.getFullWeight());

		column11.setWeight(fullWeight);

		PortletLayoutElement row2 = layouts.insert();

		ElementList<PortletColumnElement> columns2 = row2.getPortletColumns();

		PortletColumnElement column21 = columns2.insert();

		column21.setWeight(fullWeight / 2);

		PortletColumnElement column22 = columns2.insert();

		column22.setWeight(fullWeight / 2);

		PortletLayoutElement row3 = layouts.insert();

		ElementList<PortletColumnElement> columns3 = row3.getPortletColumns();

		PortletColumnElement column31 = columns3.insert();

		column31.setWeight(fullWeight);
	}

	public void add_Layout_1_2_I(LayoutTplElement layoutTpl) {
		ElementList<PortletLayoutElement> layouts = layoutTpl.getPortletLayouts();

		PortletLayoutElement row1 = layouts.insert();

		ElementList<PortletColumnElement> columns1 = row1.getPortletColumns();

		PortletColumnElement column11 = columns1.insert();

		column11.setWeight(get(column11.getFullWeight()));

		PortletLayoutElement row2 = layouts.insert();

		ElementList<PortletColumnElement> columns2 = row2.getPortletColumns();

		PortletColumnElement column21 = columns2.insert();
		PortletColumnElement column22 = columns2.insert();

		if (_isBootstrapStyle(layoutTpl)) {
			column21.setWeight(4);
			column22.setWeight(8);
		}
		else {
			column21.setWeight(30);
			column22.setWeight(70);
		}
	}

	public void add_Layout_1_2_II(LayoutTplElement layoutTpl) {
		ElementList<PortletLayoutElement> layouts = layoutTpl.getPortletLayouts();

		PortletLayoutElement row1 = layouts.insert();

		ElementList<PortletColumnElement> columns1 = row1.getPortletColumns();

		PortletColumnElement column11 = columns1.insert();

		column11.setWeight(get(column11.getFullWeight()));

		PortletLayoutElement row2 = layouts.insert();

		ElementList<PortletColumnElement> columns2 = row2.getPortletColumns();

		PortletColumnElement column21 = columns2.insert();
		PortletColumnElement column22 = columns2.insert();

		if (_isBootstrapStyle(layoutTpl)) {
			column21.setWeight(8);
			column22.setWeight(4);
		}
		else {
			column21.setWeight(70);
			column22.setWeight(30);
		}
	}

	public void add_Layout_2_2(LayoutTplElement layoutTpl) {
		ElementList<PortletLayoutElement> layouts = layoutTpl.getPortletLayouts();

		PortletLayoutElement row1 = layouts.insert();

		ElementList<PortletColumnElement> columns1 = row1.getPortletColumns();

		PortletColumnElement column11 = columns1.insert();
		PortletColumnElement column12 = columns1.insert();

		PortletLayoutElement row2 = layouts.insert();

		ElementList<PortletColumnElement> columns2 = row2.getPortletColumns();

		PortletColumnElement column21 = columns2.insert();
		PortletColumnElement column22 = columns2.insert();

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

	public void add_Layout_2_I(LayoutTplElement layoutTpl) {
		add_Row_2_I(layoutTpl);
	}

	public void add_Layout_2_II(LayoutTplElement layoutTpl) {
		add_Row_2_II(layoutTpl);
	}

	public void add_Layout_2_III(LayoutTplElement layoutTpl) {
		add_Row_2_III(layoutTpl);
	}

	public void add_Layout_3(LayoutTplElement layoutTpl) {
		add_Row_3(layoutTpl);
	}

	public void add_Row_1(CanAddPortletLayouts element) {
		ElementList<PortletLayoutElement> layouts = element.getPortletLayouts();

		PortletLayoutElement row = layouts.insert();

		ElementList<PortletColumnElement> columns = row.getPortletColumns();

		PortletColumnElement column = columns.insert();

		column.setWeight(get(column.getFullWeight()));
	}

	public void add_Row_2_I(CanAddPortletLayouts element) {
		ElementList<PortletLayoutElement> layouts = element.getPortletLayouts();

		PortletLayoutElement row = layouts.insert();

		ElementList<PortletColumnElement> columns = row.getPortletColumns();

		PortletColumnElement column1 = columns.insert();
		PortletColumnElement column2 = columns.insert();

		int fullWeight = get(column1.getFullWeight());

		column1.setWeight(fullWeight / 2);
		column2.setWeight(fullWeight / 2);
	}

	public void add_Row_2_II(CanAddPortletLayouts element) {
		ElementList<PortletLayoutElement> layouts = element.getPortletLayouts();

		PortletLayoutElement row = layouts.insert();

		ElementList<PortletColumnElement> columns = row.getPortletColumns();

		PortletColumnElement column1 = columns.insert();
		PortletColumnElement column2 = columns.insert();

		if (_isBootstrapStyle(element)) {
			column1.setWeight(4);
			column2.setWeight(8);
		}
		else {
			column1.setWeight(30);
			column2.setWeight(70);
		}
	}

	public void add_Row_3(CanAddPortletLayouts element) {
		ElementList<PortletLayoutElement> layouts = element.getPortletLayouts();

		PortletLayoutElement row = layouts.insert();

		ElementList<PortletColumnElement> columns = row.getPortletColumns();

		PortletColumnElement column1 = columns.insert();
		PortletColumnElement column2 = columns.insert();
		PortletColumnElement column3 = columns.insert();

		int fullWeight = get(column1.getFullWeight());

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