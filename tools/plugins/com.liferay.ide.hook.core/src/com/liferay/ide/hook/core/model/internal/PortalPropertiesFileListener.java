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

import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.model.PortalPropertiesFile;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Gregory Amerson
 */
public class PortalPropertiesFileListener extends FilteredListener<PropertyContentEvent> {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		Property prop = event.property();

		if (Hook.PROP_PORTAL_PROPERTIES_FILE.equals(prop.definition())) {
			Element element = prop.element();

			Hook hook = element.nearest(Hook.class);

			ElementHandle<PortalPropertiesFile> handle = hook.getPortalPropertiesFile();

			PortalPropertiesFile ppf = handle.content(false);

			if (ppf != null) {
				Value<Path> value = ppf.getValue();

				if (value != null) {
					Path path = value.content(false);

					if (path == null) {
						ppf.initialize();
					}
				}
			}
		}
	}

}