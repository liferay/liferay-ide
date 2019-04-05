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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.portlet.core.model.QName;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Kamesh Sampath
 */
public class NameOrQnameValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	public Status compute() {
		Element element = context(Element.class);

		ElementType type = element.type();

		String elementLabel = type.getLabel(false, CapitalizationType.FIRST_WORD_ONLY, false);

		QName iqName = null;
		String name = null;
		String nsURI = null;
		String localPart = null;

		if (element instanceof QName) {
			iqName = (QName)element;

			nsURI = getText(iqName.getNamespaceURI(), false);
			localPart = getText(iqName.getLocalPart(), false);
		}

		if (CoreUtil.isNullOrEmpty(name) && CoreUtil.isNullOrEmpty(nsURI) && CoreUtil.isNullOrEmpty(localPart)) {
			return Status.createErrorStatus(Resources.bind(Resources.message, elementLabel));
		}
		else if (CoreUtil.isNullOrEmpty(name) && (CoreUtil.isNullOrEmpty(nsURI) || CoreUtil.isNullOrEmpty(localPart))) {
			return Status.createErrorStatus(Resources.bind(Resources.invalidQname, elementLabel));
		}

		return Status.createOkStatus();
	}

	@Override
	protected void initValidationService() {
		_listener = new FilteredListener<PropertyContentEvent>() {

			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		SapphireUtil.attachListener(_op().getLocalPart(), _listener);
		SapphireUtil.attachListener(_op().getNamespaceURI(), _listener);
	}

	private QName _op() {
		return context(QName.class);
	}

	private Listener _listener;

	private static final class Resources extends NLS {

		public static String invalidQname;
		public static String message;

		static {
			initializeMessages(NameOrQnameValidationService.class.getName(), Resources.class);
		}

	}

}