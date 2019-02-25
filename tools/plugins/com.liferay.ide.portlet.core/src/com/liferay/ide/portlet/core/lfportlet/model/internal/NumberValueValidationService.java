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

package com.liferay.ide.portlet.core.lfportlet.model.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;

import org.apache.commons.lang.StringEscapeUtils;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 * @Author Lu Li
 */
public class NumberValueValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		Element modelElement = context(Element.class);

		if (modelElement.disposed()) {
			return Status.createOkStatus();
		}

		String triggerValue = get(modelElement.property(context(ValueProperty.class)));

		if (CoreUtil.isNullOrEmpty(triggerValue)) {
			return Status.createOkStatus();
		}

		try {
			_value = Integer.valueOf(triggerValue);
		}
		catch (NumberFormatException nfe) {
			return Status.createErrorStatus(
				Resources.bind(
					StringEscapeUtils.unescapeJava(Resources.nonIntegerInvalid), new Object[] {triggerValue, ""}));
		}

		if (CoreUtil.isNotNullOrEmpty(_min)) {
			if (_value < Integer.valueOf(_min)) {
				return Status.createErrorStatus(
					Resources.bind(
						StringEscapeUtils.unescapeJava(Resources.minNumberValueInvalid), new Object[] {_value, _min}));
			}
		}

		if (CoreUtil.isNotNullOrEmpty(_max)) {
			if (_value > Integer.valueOf(_max)) {
				return Status.createErrorStatus(
					Resources.bind(
						StringEscapeUtils.unescapeJava(Resources.maxNumberValueInvalid), new Object[] {_value, _max}));
			}
		}

		return Status.createOkStatus();
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		_min = param("min");
		_max = param("max");
	}

	private String _max;
	private String _min;
	private Integer _value;

	private static final class Resources extends NLS {

		public static String maxNumberValueInvalid;
		public static String minNumberValueInvalid;
		public static String nonIntegerInvalid;

		static {
			initializeMessages(NumberValueValidationService.class.getName(), Resources.class);
		}

	}

}