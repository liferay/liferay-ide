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

package com.liferay.ide.core.util;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public class SapphireUtil {

	public static void attachListener(Value<?> valueProperty, Listener listener) {
		if ((valueProperty == null) || (listener == null)) {
			return;
		}

		valueProperty.attach(listener);
	}

	public static <T> T getContent(Value<T> valueProperty) {
		if (valueProperty != null) {
			return valueProperty.content();
		}

		return null;
	}

	public static <T> T getContent(Value<T> valueProperty, boolean useDefaultValue) {
		if (valueProperty != null) {
			return valueProperty.content(useDefaultValue);
		}

		return null;
	}

	public static boolean ok(Element element) {
		if (element == null) {
			return false;
		}

		Status status = element.validation();

		return status.ok();
	}

}