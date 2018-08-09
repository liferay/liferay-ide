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

package com.liferay.ide.xml.search.ui;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.search.editor.contentassist.ElementContentAssistAdditionalProposalInfoProvider;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Custom content assist proposal info for portlet.
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class PortletContentAssistInfoProvider extends ElementContentAssistAdditionalProposalInfoProvider {

	public Image getImage(Node node) {
		LiferayXMLSearchUI plugin = LiferayXMLSearchUI.getDefault();

		ImageRegistry imageRegistry = plugin.getImageRegistry();

		return imageRegistry.get(LiferayXMLSearchUI.portletImg);
	}

	@Override
	protected String doGetTextInfo(IDOMElement portletNameElt) {
		IDOMElement portletElt = (IDOMElement)portletNameElt.getParentNode();

		StringBuilder buf = new StringBuilder();

		buf.append("<b>------------------------ Portlet ------------------------</b> ");

		// description

		buf.append("<br><b>Portlet name:</b> ");

		String portletName = _getTextContent(portletElt, "portlet-name");

		if (portletName != null) {
			buf.append(portletName);
		}

		// display-name

		buf.append("<br><b>Display name:</b> ");

		String displayName = _getTextContent(portletElt, "display-name");

		if (displayName != null) {
			buf.append(displayName);
		}

		// portlet-class

		buf.append("<br><b>Portlet class:</b> ");

		String portletClass = _getTextContent(portletElt, "portlet-class");

		if (portletClass != null) {
			buf.append(portletClass);
		}

		buf.append("<br><b>File:</b> ");

		IDOMModel model = portletElt.getModel();

		buf.append(model.getBaseLocation());

		return buf.toString();
	}

	private String _getTextContent(IDOMElement element, String elementName) {
		NodeList nodes = element.getElementsByTagName(elementName);

		if (nodes.getLength() < 1) {
			return "";
		}

		Element childElement = (Element)nodes.item(0);

		Text text = (Text)childElement.getFirstChild();

		if (text == null) {
			return "";
		}

		return text.getData();
	}

}