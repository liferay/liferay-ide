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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;

import java.util.HashSet;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Kuo Zhang
 */
public class PortletColumnsValidtionSerivce extends ValidationService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		for (PortletColumnElement column : _columnsAttachedListener) {
			if ((column != null) && !column.disposed()) {
				column.detach(_listener);
			}
		}

		_listener = null;

		super.dispose();
	}

	// store PortletColumns who have attached the listener

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		PortletLayoutElement portletLayout = context(PortletLayoutElement.class);

		LayoutTplElement layoutTpl = portletLayout.nearest(LayoutTplElement.class);

		int actualWeightSum = 0;
		int exceptedweightSum = get(layoutTpl.getBootstrapStyle()) ? 12 : 100;

		for (PortletColumnElement col : portletLayout.getPortletColumns()) {

			/**
			 * attach listener for the newly added PortletColumn there should be a better
			 * way to do this which makes more sense
			 */
			if (!_columnsAttachedListener.contains(col)) {
				SapphireUtil.attachListener(col.getWeight(), _listener);

				_columnsAttachedListener.add(col);
			}

			Value<Integer> weight = col.getWeight();

			Integer content = weight.content();

			actualWeightSum += content.intValue();
		}

		// we need allow 99% ?

		if (!((actualWeightSum == exceptedweightSum) || ((exceptedweightSum == 100) && (actualWeightSum == 99)))) {
			retval = Status.createErrorStatus("The sum of weight of columns should be: " + exceptedweightSum);
		}

		return retval;
	}

	@Override
	protected void initValidationService() {
		_columnsAttachedListener = new HashSet<>();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		super.initValidationService();
	}

	private HashSet<PortletColumnElement> _columnsAttachedListener;
	private FilteredListener<PropertyContentEvent> _listener;

}