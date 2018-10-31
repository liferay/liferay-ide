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

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.InitialValueService;
import org.eclipse.sapphire.Value;

/**
 * @author Kuo Zhang
 */
public class PortletColumnWeightInitialValueService extends InitialValueService {

	@Override
	protected String compute() {
		PortletColumnElement column = (PortletColumnElement)context(Element.class);

		PortletLayoutElement parentLayout = column.nearest(PortletLayoutElement.class);
		LayoutTplElement layoutTpl = column.nearest(LayoutTplElement.class);

		int weightSum = 0;

		ElementList<PortletColumnElement> portletColumns = parentLayout.getPortletColumns();

		for (PortletColumnElement col : portletColumns) {
			if (col != column) {
				Integer colWeightContent = SapphireUtil.getContent(col.getWeight());

				weightSum += colWeightContent.intValue();
			}
		}

		Integer fullWeightContent = SapphireUtil.getContent(column.getFullWeight());

		int fullWeight = fullWeightContent.intValue();

		int initialWeight = SapphireUtil.getContent(layoutTpl.getBootstrapStyle()) ? 3 : 25;

		if ((weightSum >= 0) && (weightSum < fullWeight)) {
			initialWeight = fullWeight - weightSum;
		}
		else if (weightSum == fullWeight) {

			/**
			 * the index of last valid column is portletColumns.size() - 2, because
			 * portletColumns().size() -1 is the new inserted column
			 */
			PortletColumnElement lastValidColumn = portletColumns.get(portletColumns.size() - 2);

			Value<Integer> lvColumnWeight = lastValidColumn.getWeight();

			Integer lvWeightContent = lvColumnWeight.content();

			int lastValidWeight = lvWeightContent.intValue();

			if (lastValidWeight > 1) {
				initialWeight = lastValidWeight / 2;

				lastValidWeight = lastValidWeight - initialWeight;

				lastValidColumn.setWeight(lastValidWeight);
			}
		}

		return String.valueOf(initialWeight);
	}

}