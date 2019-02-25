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

import java.io.File;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public class SapphireUtil {

	public static void attachListener(Value<?> value, Listener listener) {
		if ((value == null) || (listener == null)) {
			return;
		}

		value.attach(listener);
	}

	public static void clear(ElementList<?> list) {
		list.clear();
	}

	public static void detachListener(ElementList<?> values, Listener listener) {
		if ((values == null) || (listener == null)) {
			return;
		}

		values.detach(listener);
	}

	public static void detachListener(Value<?> value, Listener listener) {
		if ((value == null) || (listener == null)) {
			return;
		}

		value.detach(listener);
	}

	public static boolean exists(Path path) {
		if (path != null) {
			File file = path.toFile();

			if (file.exists()) {
				return true;
			}
		}

		return false;
	}

	public static Element getElement(PropertyContentEvent event) {
		Property property = event.property();

		return property.element();
	}

	public static PropertyDef getPropertyDef(PropertyContentEvent event) {
		Property property = event.property();

		return property.definition();
	}

	public static <T> String getText(Value<T> value) {
		if (value != null) {
			return value.text();
		}

		return "";
	}

	public static <T> String getText(Value<T> value, boolean useDefaultValue) {
		if (value != null) {
			return value.text(useDefaultValue);
		}

		return "";
	}

	public static boolean ok(Element element) {
		if (element == null) {
			return false;
		}

		Status status = element.validation();

		return status.ok();
	}

	public static void refresh(Value<?> value) {
		value.refresh();
	}

}