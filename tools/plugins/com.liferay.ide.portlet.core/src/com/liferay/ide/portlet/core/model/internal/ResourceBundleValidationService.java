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

package com.liferay.ide.portlet.core.model.internal;

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.portlet.core.model.ResourceBundle;

import org.apache.commons.lang.StringEscapeUtils;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class ResourceBundleValidationService extends ValidationService {

	public Status compute() {
		Element modelElement = context(Element.class);

		if (!modelElement.disposed() && modelElement instanceof ResourceBundle) {
			String bundle = SapphireUtil.getText(modelElement.property(context(ValueProperty.class)), false);

			if ((bundle != null) && (bundle.indexOf("/") != -1)) {
				bundle = bundle.replace("/", ".");

				String correctBundle = bundle.replaceAll("\\.properties$", "");

				Object[] objects = {"'" + bundle + "'", "'" + correctBundle + "'"};

				return Status.createErrorStatus(
					Resources.bind(StringEscapeUtils.unescapeJava(Resources.invalidResourceBundleWithSlash), objects));
			}
			else if ((bundle != null) && (bundle.startsWith(".") || bundle.contains(".."))) {
				Object[] objects = {"'" + bundle + "'"};

				return Status.createErrorStatus(
					Resources.bind(StringEscapeUtils.unescapeJava(Resources.invalidResourceBundleFileName), objects));
			}
		}

		return Status.createOkStatus();
	}

	protected void initValidationService() {
		_listener = new FilteredListener<PropertyContentEvent>() {

			protected void handleTypedEvent(PropertyContentEvent event) {
				if (!context(ResourceBundle.class).disposed()) {
					refresh();
				}
			}

		};

		context(ResourceBundle.class).attach(_listener);
	}

	private FilteredListener<PropertyContentEvent> _listener;

	private static class Resources extends NLS {

		public static String invalidResourceBundleFileName;
		public static String invalidResourceBundleWithSlash;

		static {
			initializeMessages(ResourceBundleValidationService.class.getName(), Resources.class);
		}

	}

}