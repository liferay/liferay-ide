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

package com.liferay.ide.portlet.core.dd;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.project.core.descriptor.AddNewPortletOperation;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;
import com.liferay.ide.project.core.descriptor.RemoveAllPortletsOperation;
import com.liferay.ide.project.core.descriptor.RemoveSampleElementsOperation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
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
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Simon Jiang
 * @author Kuo Zhang
 * @author Terry Jia
 */
@SuppressWarnings({"restriction", "unchecked"})
public class PortletDescriptorHelper extends LiferayDescriptorHelper implements INewPortletClassDataModelProperties {

	public static final String DESCRIPTOR_FILE = ILiferayConstants.PORTLET_XML_FILE;

	public PortletDescriptorHelper() {
	}

	public PortletDescriptorHelper(IProject project) {
		super(project);
	}

	public IStatus addResourceBundle(String resourceBundle, String portletName) {
		IFile descriptorFile = getDescriptorFile();

		DOMModelOperation operation = new DOMModelEditOperation(descriptorFile) {

			@Override
			protected IStatus doExecute(IDOMDocument document) {
				return doAddResourceBundle(document, resourceBundle, portletName);
			}

		};

		return operation.execute();
	}

	public boolean canAddNewPortlet(IDataModel model) {
		return model.getID().contains("NewPortlet");
	}

	public IStatus configurePortletXml(String newPortletName) {
		IFile descriptorFile = getDescriptorFile();

		IStatus status = new DOMModelEditOperation(descriptorFile) {

			protected IStatus doExecute(IDOMDocument document) {
				Element rootElement = document.getDocumentElement();

				NodeList portletNodes = rootElement.getElementsByTagName("portlet");

				if (portletNodes.getLength() > 0) {
					Element lastPortletElement = (Element)portletNodes.item(portletNodes.getLength() - 1);

					Element portletName = NodeUtil.findChildElement(lastPortletElement, "portlet-name");

					portletName.replaceChild(document.createTextNode(newPortletName), portletName.getFirstChild());
				}

				return Status.OK_STATUS;
			}

		}.execute();

		return status;
	}

	public IStatus doAddNewPortlet(IDOMDocument document, IDataModel model) {

		// <portlet-app> element

		Element rootElement = document.getDocumentElement();

		// new <portlet> element

		Element newPortletElement = document.createElement("portlet");

		NodeUtil.appendChildElement(newPortletElement, "portlet-name", model.getStringProperty(PORTLET_NAME));

		NodeUtil.appendChildElement(newPortletElement, "display-name", model.getStringProperty(DISPLAY_NAME));

		NodeUtil.appendChildElement(newPortletElement, "portlet-class", getPortletClassText(model));

		// add <init-param> elements as needed

		List<ParamValue> initParams = (List<ParamValue>)model.getProperty(INIT_PARAMS);

		for (ParamValue initParam : initParams) {
			Element newInitParamElement = NodeUtil.appendChildElement(newPortletElement, "init-param");

			NodeUtil.appendChildElement(newInitParamElement, "name", initParam.getName());

			NodeUtil.appendChildElement(newInitParamElement, "value", initParam.getValue());
		}

		// expiration cache

		NodeUtil.appendChildElement(newPortletElement, "expiration-cache", "0");

		// supports node

		Element newSupportsElement = NodeUtil.appendChildElement(newPortletElement, "supports");

		NodeUtil.appendChildElement(newSupportsElement, "mime-type", "text/html");

		// for all support modes need to add into

		for (String portletMode : ALL_PORTLET_MODES) {
			if (model.getBooleanProperty(portletMode)) {
				NodeUtil.appendChildElement(
					newSupportsElement, "portlet-mode",
					model.getPropertyDescriptor(portletMode).getPropertyDescription());
			}
		}

		if (model.getBooleanProperty(CREATE_RESOURCE_BUNDLE_FILE)) {

			// need to remove .properties off the end of the bundle_file_path

			String bundlePath = model.getStringProperty(CREATE_RESOURCE_BUNDLE_FILE_PATH);

			String bundleValue = bundlePath.replaceAll("\\.properties$", StringPool.EMPTY);

			String validBuildValue = bundleValue.replaceAll("\\/", ".");

			NodeUtil.appendChildElement(newPortletElement, "resource-bundle", validBuildValue);
		}

		// add portlet-info

		Element newPortletInfoElement = NodeUtil.appendChildElement(newPortletElement, "portlet-info");

		NodeUtil.appendChildElement(newPortletInfoElement, "title", model.getStringProperty(TITLE));

		NodeUtil.appendChildElement(newPortletInfoElement, "short-title", model.getStringProperty(SHORT_TITLE));

		NodeUtil.appendChildElement(newPortletInfoElement, "keywords", model.getStringProperty(KEYWORDS));

		// security role refs

		for (String roleName : DEFAULT_SECURITY_ROLE_NAMES) {
			NodeUtil.appendChildElement(
				NodeUtil.appendChildElement(newPortletElement, "security-role-ref"), "role-name", roleName);
		}

		// check for event-definition elements

		Node refNode = null;

		String[] refElementNames = {
			"custom-portlet-mode", "custom-window-state", "user-attribute", "security-constraint", "resource-bundle",
			"filter", "filter-mapping", "default-namespace", "event-definition", "public-render-parameter", "listener",
			"container-runtime-option"
		};

		for (int i = 0; i < refElementNames.length; i++) {
			refNode = NodeUtil.findFirstChild(rootElement, refElementNames[i]);

			if (refNode != null) {
				break;
			}
		}

		rootElement.insertBefore(newPortletElement, refNode);

		// append a newline text node

		rootElement.appendChild(document.createTextNode(System.getProperty("line.separator")));

		// format the new node added to the model;

		FormatProcessorXML processor = new FormatProcessorXML();

		processor.formatNode(newPortletElement);

		return Status.OK_STATUS;
	}

	public IStatus doRemoveAllPortlets() {
		String portletTagName = "portlet";

		DOMModelEditOperation domModelOperation = new DOMModelEditOperation(getDescriptorFile()) {

			protected IStatus doExecute(IDOMDocument document) {
				return removeAllElements(document, portletTagName);
			}

		};

		IStatus status = domModelOperation.execute();

		return status;
	}

	public String[] getAllPortletNames() {
		List<String> allPortletNames = new ArrayList<>();

		IFile descriptorFile = getDescriptorFile();

		if (descriptorFile != null) {
			DOMModelOperation op = new DOMModelReadOperation(descriptorFile) {

				protected IStatus doExecute(IDOMDocument document) {
					NodeList nodeList = document.getElementsByTagName("portlet-name");

					for (int i = 0; i < nodeList.getLength(); i++) {
						Element portletName = (Element)nodeList.item(i);

						allPortletNames.add(NodeUtil.getTextContent(portletName));
					}

					return Status.OK_STATUS;
				}

			};

			op.execute();
		}

		return allPortletNames.toArray(new String[0]);
	}

	public String[] getAllResourceBundles() {
		List<String> allResourceBundles = new ArrayList<>();

		IFile descriptorFile = getDescriptorFile();

		if (descriptorFile != null) {
			DOMModelOperation op = new DOMModelReadOperation(descriptorFile) {

				protected IStatus doExecute(IDOMDocument document) {
					NodeList nodeList = document.getElementsByTagName("resource-bundle");

					for (int i = 0; i < nodeList.getLength(); i++) {
						Element resourceBundle = (Element)nodeList.item(i);

						allResourceBundles.add(NodeUtil.getTextContent(resourceBundle));
					}

					return Status.OK_STATUS;
				}

			};

			op.execute();
		}

		return allResourceBundles.toArray(new String[0]);
	}

	public IFile getDescriptorFile() {
		return super.getDescriptorFile(DESCRIPTOR_FILE);
	}

	protected void addDescriptorOperations() {
		RemoveSampleElementsOperation rseOperation = new RemoveSampleElementsOperation() {

			@Override
			public IStatus removeSampleElements() {
				return doRemoveAllPortlets();
			};

		};

		addDescriptorOperation(rseOperation);

		AddNewPortletOperation apOperation = new AddNewPortletOperation() {

			@Override
			public IStatus addNewPortlet(IDataModel model) {
				IStatus status = Status.OK_STATUS;

				if (canAddNewPortlet(model)) {
					IFile descriptorFile = getDescriptorFile();

					if (descriptorFile != null) {
						DOMModelOperation domModelOperation = new DOMModelEditOperation(descriptorFile) {

							protected void createDefaultFile() {
								createDefaultDescriptor(_DESCRIPTOR_TEMPLATE, "");
							}

							protected IStatus doExecute(IDOMDocument document) {
								return doAddNewPortlet(document, model);
							}

						};

						status = domModelOperation.execute();
					}
				}

				return status;
			}

		};

		addDescriptorOperation(apOperation);

		RemoveAllPortletsOperation rapOperation = new RemoveAllPortletsOperation() {

			@Override
			public IStatus removeAllPortlets() {
				return doRemoveAllPortlets();
			}

		};

		addDescriptorOperation(rapOperation);
	}

	protected IStatus doAddResourceBundle(IDOMDocument document, String resourceBundle, String portletName) {
		FormatProcessorXML processor = new FormatProcessorXML();

		NodeList portletNameList = document.getElementsByTagName("portlet-name");

		if ((portletNameList != null) && (portletNameList.getLength() > 0) && !CoreUtil.isNullOrEmpty(resourceBundle)) {
			Node portletNameNode = null;

			for (int i = 0; i < portletNameList.getLength(); i++) {
				if (NodeUtil.getTextContent(portletNameList.item(i)).equals(portletName)) {
					portletNameNode = portletNameList.item(i);
				}
			}

			if (portletNameNode == null) {
				return Status.CANCEL_STATUS;
			}

			Element newResourceBundleElement = null;

			Node portlet = portletNameNode.getParentNode();

			Node refNode = null;

			Node supports = NodeUtil.findLastChild((Element)portlet, "supports");

			if (supports != null) {
				Node supportedLocale = NodeUtil.findLastChild((Element)portlet, "supported-locale");

				if (supportedLocale != null) {
					refNode = supportedLocale;
				}
				else {
					refNode = supports;
				}
			}
			else {
				return Status.CANCEL_STATUS;
			}

			newResourceBundleElement = NodeUtil.insertChildElementAfter(
				(Element)portlet, refNode, "resource-bundle", resourceBundle);

			processor.formatNode(newResourceBundleElement);
		}

		return Status.OK_STATUS;
	}

	protected String getPortletClassText(IDataModel model) {
		return model.getStringProperty(QUALIFIED_CLASS_NAME);
	}

	private static final String _DESCRIPTOR_TEMPLATE =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<portlet-app xmlns=\"http://java.sun.com/xml/ns/portlet" +
			"/portlet-app_2_0.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:" +
				"schemaLocation=\"http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd http://java.sun.com/xml/ns" +
					"/portlet/portlet-app_2_0.xsd\" version=\"2.0\">\n</portlet-app>";

}