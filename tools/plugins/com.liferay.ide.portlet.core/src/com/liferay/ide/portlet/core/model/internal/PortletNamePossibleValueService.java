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

import com.liferay.ide.core.LiferayCore;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 * @author Tao Tao
 */
@SuppressWarnings("restriction")
public class PortletNamePossibleValueService extends PossibleValuesService {

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.sapphire.modeling.PossibleValuesService#fillPossibleValues(java.
	 *      util.SortedSet)
	 */
	@Override
	protected void compute(Set<String> values) {
		IFile displayXmlFile = context(Element.class).resource().adapt(IFile.class);

		IFolder resourceFolder = (IFolder)displayXmlFile.getParent();

		IFile portletXml = resourceFolder.getFile("portlet.xml");

		IDOMModel portletModel = null;

		try {
			portletModel = (IDOMModel)StructuredModelManager.getModelManager().getModelForRead(portletXml);

			IDOMDocument portletDocument = portletModel.getDocument();

			NodeList elements = portletDocument.getElementsByTagName(PORTLET_NAME_ELEMENT);

			List<String> portletNameList = new LinkedList<>();

			for (int i = 0; i < elements.getLength(); i++) {
				Node portletElement = elements.item(i);

				if (portletElement != null) {
					String portletName = portletElement.getTextContent();

					portletNameList.add(portletName);
				}
			}

			this.localPortletNames = portletNameList.toArray(new String[0]);
		}
		catch (Exception e) {
			LiferayCore.logError(e);
		}
		finally {
			if (portletModel != null) {
				portletModel.releaseFromRead();
			}
		}

		if (this.localPortletNames != null) {
			Collections.addAll(values, this.localPortletNames);
		}
	}

	private static final String PORTLET_NAME_ELEMENT = "portlet-name";

	private String[] localPortletNames;

}