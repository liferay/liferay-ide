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

package com.liferay.ide.layouttpl.core.model.internal;

import com.liferay.ide.layouttpl.core.model.CanAddPortletLayouts;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Kuo Zhang
 */
public class PortletColumnsListener extends FilteredListener<PropertyContentEvent> {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		Property property = event.property();

		Element element = property.element();

		LayoutTplElement layouttpl = element.nearest(LayoutTplElement.class);

		updateColumns(layouttpl, 1);
	}

	protected int updateColumns(CanAddPortletLayouts columnsParent, int numId) {
		for (PortletLayoutElement portletLayout : columnsParent.getPortletLayouts()) {
			ElementList<PortletColumnElement> columns = portletLayout.getPortletColumns();

			int size = columns.size();

			for (int i = 0; i < size; i++) {
				PortletColumnElement column = columns.get(i);

				ElementList<PortletLayoutElement> layouts = column.getPortletLayouts();

				if (layouts.isEmpty()) {
					column.setNumId(String.valueOf(numId++));
				}
				else {

					// when new child is added, the parent column will have no numId

					column.setNumId("N/A");

					numId = updateColumns(column, numId++);
				}

				column.setOnly(false);
				column.setFirst(false);
				column.setLast(false);

				column.setColumnDescriptor("");
				column.setColumnContentDescriptor("");

				if (size == 1) {
					column.setOnly(true);
					column.setColumnDescriptor("portlet-column-only");
					column.setColumnContentDescriptor("portlet-column-content-only");
				}
				else if (size > 1) {
					if (i == 0) {
						column.setFirst(true);
						column.setColumnDescriptor("portlet-column-first");
						column.setColumnContentDescriptor("portlet-column-content-first");
					}
					else if (i == (size - 1)) {
						column.setLast(true);
						column.setColumnDescriptor("portlet-column-last");
						column.setColumnContentDescriptor("portlet-column-content-last");
					}
				}
			}
		}

		return numId;
	}

}