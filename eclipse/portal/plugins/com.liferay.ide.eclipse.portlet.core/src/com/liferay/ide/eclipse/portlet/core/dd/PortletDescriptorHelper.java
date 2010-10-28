/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.portlet.core.dd;

import com.liferay.ide.eclipse.core.util.DescriptorHelper;
import com.liferay.ide.eclipse.core.util.NodeUtil;
import com.liferay.ide.eclipse.portlet.core.PortletCore;
import com.liferay.ide.eclipse.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.eclipse.server.core.IPortalConstants;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.j2ee.common.ParamValue;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( {
	"restriction", "unchecked"
})
public class PortletDescriptorHelper extends DescriptorHelper implements INewPortletClassDataModelProperties {

	public PortletDescriptorHelper(IProject project) {
		super(project);
	}

	public IStatus addNewPortlet(final IDataModel model) {
		DOMModelEditOperation domModelOperation =
			new DOMModelEditOperation(getDescriptorFile(IPortalConstants.PORTLET_XML_FILE)) {

				protected IStatus doExecute(IDOMDocument document) {
					return updatePortletXML(document, model);
				}

			};

		IStatus status = domModelOperation.execute();

		if (!status.isOK()) {
			return status;
		}

		domModelOperation = new DOMModelEditOperation(getDescriptorFile(IPortalConstants.LIFERAY_PORTLET_XML_FILE)) {

			protected IStatus doExecute(IDOMDocument document) {
				return updateLiferayPortletXML(document, model);
			}

		};

		status = domModelOperation.execute();

		if (!status.isOK()) {
			return status;
		}

		domModelOperation = new DOMModelEditOperation(getDescriptorFile(IPortalConstants.LIFERAY_DISPLAY_XML_FILE)) {

			protected IStatus doExecute(IDOMDocument document) {
				return updateLiferayDisplayXML(document, model);
			}

		};

		status = domModelOperation.execute();

		return status;
	}

	public IStatus removeAllPortlets() {
		DOMModelEditOperation domModelOperation =
			new DOMModelEditOperation(getDescriptorFile(IPortalConstants.PORTLET_XML_FILE)) {

				protected IStatus doExecute(IDOMDocument document) {
					return removeAllPortletElements(document);
				}

			};

		IStatus status = domModelOperation.execute();

		if (!status.isOK()) {
			return status;
		}

		domModelOperation = new DOMModelEditOperation(getDescriptorFile(IPortalConstants.LIFERAY_PORTLET_XML_FILE)) {

			protected IStatus doExecute(IDOMDocument document) {
				return removeAllPortletElements(document);
			}

		};

		status = domModelOperation.execute();

		if (!status.isOK()) {
			return status;
		}

		domModelOperation = new DOMModelEditOperation(getDescriptorFile(IPortalConstants.LIFERAY_DISPLAY_XML_FILE)) {

			protected IStatus doExecute(IDOMDocument document) {
				return removeAllPortletElements(document);
			}

		};

		status = domModelOperation.execute();

		return status;
	}

	public IStatus updateLiferayDisplayXML(IDOMDocument document, final IDataModel model) {
		// <display> element
		Element docRoot = document.getDocumentElement();

		// for the category assignment check to see if there is already a
		// category element with that id
		Element category = null;

		String modelCategory = model.getStringProperty(CATEGORY);

		for (Element child : getChildElements(docRoot)) {
			if (child.getNodeName().equals("category") && modelCategory.equals(child.getAttribute("name"))) {
				category = child;

				break;
			}
		}

		Element id = null;

		String modelId = model.getStringProperty(ID);

		if (category != null) {
			// check to make sure we don't aleady have a portlet with our id in
			// this category
			for (Element child : getChildElements(category)) {
				if (child.getNodeName().equals("portlet") && modelId.equals(child.getAttribute("id"))) {
					id = child;

					break;
				}
			}
		}
		else {
			category = document.createElement("category");
			category.setAttribute("name", modelCategory);

			docRoot.appendChild(category);

			Node newline = document.createTextNode(System.getProperty("line.separator"));

			docRoot.appendChild(newline);
		}

		if (id == null) {
			appendChildElement(category, "portlet").setAttribute("id", modelId);
		}

		// format the new node added to the model;
		FormatProcessorXML processor = new FormatProcessorXML();

		processor.formatNode(category);

		return Status.OK_STATUS;
	}

	public IStatus updateLiferayPortletXML(IDOMDocument document, final IDataModel model) {
		// <liferay-portlet-app> element
		Element docRoot = document.getDocumentElement();

		// new <portlet> element
		Element newPortletElement = document.createElement("portlet");

		appendChildElement(newPortletElement, "portlet-name", model.getStringProperty(LIFERAY_PORTLET_NAME));

		appendChildElement(newPortletElement, "icon", model.getStringProperty(ICON_FILE));

		appendChildElement(
			newPortletElement, "instanceable", Boolean.toString(model.getBooleanProperty(ALLOW_MULTIPLE)));

		appendChildElement(newPortletElement, "header-portlet-css", model.getStringProperty(CSS_FILE));

		appendChildElement(newPortletElement, "footer-portlet-javascript", model.getStringProperty(JAVASCRIPT_FILE));

		appendChildElement(newPortletElement, "css-class-wrapper", model.getStringProperty(CSS_CLASS_WRAPPER));

		// TODO make sure role mapper elements are available and correct in the
		// file.
		// role mapper elements
		// for (String roleName : DEFAULT_SECURITY_ROLE_NAMES) {
		// Element newRoleMapperElement = appendChildElement(newPortletElement,
		// "role-mapper");
		// appendChildElement(newRoleMapperElement, "role-name", roleName);
		// appendChildElement(newRoleMapperElement, "role-link",
		// WordUtils.capitalize(roleName));
		// }

		// must append this before any role-mapper elements
		Element firstRoleMapper = null;

		for (Element child : getChildElements(docRoot)) {
			if (child.getNodeName().equals("role-mapper")) {
				firstRoleMapper = child;

				break;
			}
		}
		Node newline = document.createTextNode(System.getProperty("line.separator"));

		if (firstRoleMapper != null) {
			docRoot.insertBefore(newPortletElement, firstRoleMapper);

			docRoot.insertBefore(newline, firstRoleMapper);
		}
		else {
			docRoot.appendChild(newPortletElement);

			docRoot.appendChild(newline);
		}

		// format the new node added to the model;
		FormatProcessorXML processor = new FormatProcessorXML();

		processor.formatNode(newPortletElement);

		return Status.OK_STATUS;
	}

	public IStatus updatePortletXML(IDOMDocument document, IDataModel model) {
		// <portlet-app> element
		Element docRoot = document.getDocumentElement();

		// new <portlet> element
		Element newPortletElement = document.createElement("portlet");

		appendChildElement(newPortletElement, "portlet-name", model.getStringProperty(PORTLET_NAME));

		appendChildElement(newPortletElement, "display-name", model.getStringProperty(DISPLAY_NAME));

		appendChildElement(newPortletElement, "portlet-class", getPortletClassText(model));

		// add <init-param> elements as needed
		List<ParamValue> initParams = (List<ParamValue>) model.getProperty(INIT_PARAMS);

		for (ParamValue initParam : initParams) {
			Element newInitParamElement = appendChildElement(newPortletElement, "init-param");

			appendChildElement(newInitParamElement, "name", initParam.getName());

			appendChildElement(newInitParamElement, "value", initParam.getValue());
		}

		// expiration cache
		appendChildElement(newPortletElement, "expiration-cache", "0");

		// supports node
		Element newSupportsElement = appendChildElement(newPortletElement, "supports");

		appendChildElement(newSupportsElement, "mime-type", "text/html");

		// for all support modes need to add into
		for (String portletMode : ALL_PORTLET_MODES) {
			if (model.getBooleanProperty(portletMode)) {
				appendChildElement(
					newSupportsElement, "portlet-mode",
					model.getPropertyDescriptor(portletMode).getPropertyDescription());
			}
		}

		if (model.getBooleanProperty(CREATE_RESOURCE_BUNDLE_FILE)) {
			// need to remove .properties off the end of the bundle_file_path
			String bundlePath = model.getStringProperty(CREATE_RESOURCE_BUNDLE_FILE_PATH);
			String bundleValue = bundlePath.replaceAll("\\.properties$", "");
			appendChildElement(newPortletElement, "resource-bundle", bundleValue);
		}

		// add portlet-info
		Element newPortletInfoElement = appendChildElement(newPortletElement, "portlet-info");

		appendChildElement(newPortletInfoElement, "title", model.getStringProperty(TITLE));

		appendChildElement(newPortletInfoElement, "short-title", model.getStringProperty(SHORT_TITLE));

		appendChildElement(newPortletInfoElement, "keywords", model.getStringProperty(KEYWORDS));

		// security role refs
		for (String roleName : DEFAULT_SECURITY_ROLE_NAMES) {
			appendChildElement(appendChildElement(newPortletElement, "security-role-ref"), "role-name", roleName);
		}

		// check for event-definition elements

		Node refNode = null;

		String[] refElementNames =
			new String[] {
				"custom-portlet-mode", "custom-window-state", "user-attribute", "security-constraint",
				"resource-bundle", "filter", "filter-mapping", "default-namespace", "event-definition",
				"public-render-parameter", "listener", "container-runtime-option"
			};

		for (int i = 0; i < refElementNames.length; i++) {
			refNode = NodeUtil.findFirstChild(docRoot, refElementNames[i]);

			if (refNode != null) {
				break;
			}
		}

		docRoot.insertBefore(newPortletElement, refNode);

		// append a newline text node
		docRoot.appendChild(document.createTextNode(System.getProperty("line.separator")));

		// format the new node added to the model;
		FormatProcessorXML processor = new FormatProcessorXML();

		processor.formatNode(newPortletElement);

		return Status.OK_STATUS;
	}

	protected String getPortletClassText(IDataModel model) {
		return model.getStringProperty(QUALIFIED_CLASS_NAME);
	}

	protected IStatus removeAllPortletElements(IDOMDocument document) {
		if (document == null) {
			return PortletCore.createErrorStatus("Could not remove portlet elements: null document");
		}

		NodeList portletElements = document.getElementsByTagName("portlet");

		try {
			if (portletElements != null && portletElements.getLength() > 0) {
				for (int i = 0; i < portletElements.getLength(); i++) {
					Node element = portletElements.item(i);
					element.getParentNode().removeChild(element);
				}
			}
		}
		catch (Exception ex) {
			return PortletCore.createErrorStatus("Could not remove portlet elements", ex);
		}

		return Status.OK_STATUS;
	}

}
