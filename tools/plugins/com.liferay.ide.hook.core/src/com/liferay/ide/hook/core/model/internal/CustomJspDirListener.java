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

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.hook.core.model.CustomJspDir;
import com.liferay.ide.hook.core.model.Hook;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Kuo Zhang
 * @author Gregory Amerson
 */
public class CustomJspDirListener extends FilteredListener<PropertyContentEvent> {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		Property prop = event.property();

		Hook hook = prop.element().nearest(Hook.class);

		if (hook != null) {
			if (CustomJspDir.PROP_VALUE.equals(prop.definition())) {

				// IDE-1132, Listen the change of Property CustomJspDir, and refresh the
				// Property CustomJsps.

				hook.property(Hook.PROP_CUSTOM_JSPS).refresh();
			}
			else if (Hook.PROP_CUSTOM_JSP_DIR.equals(prop.definition())) {

				// IDE-1251 listen for changes to custom_jsp_dir and if it is empty initialize
				// initial content @InitialValue

				CustomJspDir customJspDir = hook.getCustomJspDir().content(false);

				if (customJspDir != null) {
					Value<Path> value = customJspDir.getValue();

					if (value != null) {
						Path path = value.content(false);

						if (path == null) {
							customJspDir.initialize();
						}
					}
				}
			}
		}
	}

}