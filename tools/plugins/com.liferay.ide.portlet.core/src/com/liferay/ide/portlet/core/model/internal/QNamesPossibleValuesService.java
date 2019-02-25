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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.portlet.core.model.EventDefinition;
import com.liferay.ide.portlet.core.model.EventDefinitionRef;
import com.liferay.ide.portlet.core.model.PortletApp;
import com.liferay.ide.portlet.core.model.PublicRenderParameter;
import com.liferay.ide.portlet.core.model.SupportedPublicRenderParameter;

import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Kamesh Sampath
 */
public class QNamesPossibleValuesService extends PossibleValuesService implements SapphireContentAccessor {

	@Override
	protected void compute(Set<String> values) {
		Element element = context(Element.class);

		PortletApp portletApp = context(Element.class).nearest(PortletApp.class);

		if (element instanceof EventDefinitionRef) {
			ElementList<EventDefinition> eventDefs = portletApp.getEventDefinitions();

			for (EventDefinition eventDefinition : eventDefs) {
				String nsURI = get(eventDefinition.getNamespaceURI());
				String localPart = get(eventDefinition.getLocalPart());

				if ((nsURI != null) && (localPart != null)) {
					String qname = _getQName(
						get(eventDefinition.getNamespaceURI(), false), get(eventDefinition.getLocalPart()));

					values.add(qname);
				}
			}
		}
		else if (element instanceof SupportedPublicRenderParameter) {
			ElementList<PublicRenderParameter> publicRenderParameters = portletApp.getPublicRenderParameters();

			for (PublicRenderParameter publicRenderParam : publicRenderParameters) {
				if ((get(publicRenderParam.getNamespaceURI()) != null) &&
					(get(publicRenderParam.getLocalPart()) != null)) {

					String qname = _getQName(
						get(publicRenderParam.getNamespaceURI(), false), get(publicRenderParam.getLocalPart()));

					values.add(qname);
				}
			}
		}
	}

	private String _getQName(String nsURI, String localPart) {
		QName qName = null;

		qName = new QName(nsURI, localPart);

		return qName.toString();
	}

}