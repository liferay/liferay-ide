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
