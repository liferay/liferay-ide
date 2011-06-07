/*******************************************************************************
 * Copyright (c) 2010-2011 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/
package com.liferay.ide.eclipse.service.core.model.internal;

import org.eclipse.sapphire.modeling.xml.StandardRootElementController;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;


public class ServiceBuilderRootElementController extends StandardRootElementController {

	public ServiceBuilderRootElementController() {
		super("", "http://www.liferay.com/dtd/liferay-service-builder_6_0_0.dtd", "", "service-builder");
	}

	@Override
	protected void createRootElement(Document document, RootElementInfo rinfo) {
		final Element root = document.createElementNS(null, rinfo.elementName);
		final DocumentType doctype =
			document.getImplementation().createDocumentType(
				rinfo.elementName, "-//Liferay//DTD Service Builder 6.0.0//EN", rinfo.schemaLocation);

		document.appendChild(doctype);
		document.insertBefore(document.createTextNode("\n"), doctype);
		document.appendChild(document.createTextNode("\n"));
		document.appendChild(root);
	}
}
