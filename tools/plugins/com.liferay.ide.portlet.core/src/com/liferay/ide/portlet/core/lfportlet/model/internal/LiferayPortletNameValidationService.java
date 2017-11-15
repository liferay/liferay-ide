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

import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;

import org.apache.commons.lang.StringEscapeUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class LiferayPortletNameValidationService extends ValidationService {

	@Override
	protected Status compute() {
		Element modelElement = context(Element.class);

		if (!modelElement.disposed()) {
			liferayPortletName = (String)modelElement.property(context(ValueProperty.class)).content();
			IProject project = modelElement.adapt(IProject.class);

			String[] portletNames = new PortletDescriptorHelper(project).getAllPortletNames();

			if (portletNames != null) {
				for (String portletName : portletNames) {
					if (portletName.equals(liferayPortletName)) {
						return Status.createOkStatus();
					}
				}
			}
		}

		return Status.createErrorStatus(
			Resources.bind(StringEscapeUtils.unescapeJava(Resources.portletNameInvalid), new Object[] {
				liferayPortletName
			}));
	}

	private String liferayPortletName;

	private static final class Resources extends NLS {

		public static String portletNameInvalid;

		static {
			initializeMessages(LiferayPortletNameValidationService.class.getName(), Resources.class);
		}

	}

}