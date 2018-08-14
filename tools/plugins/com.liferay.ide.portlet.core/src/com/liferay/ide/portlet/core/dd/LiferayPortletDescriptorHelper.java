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
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.project.core.descriptor.AddNewPortletOperation;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;
import com.liferay.ide.project.core.descriptor.RemoveAllPortletsOperation;
import com.liferay.ide.project.core.descriptor.RemoveSampleElementsOperation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
 */
@SuppressWarnings("restriction")
public class LiferayPortletDescriptorHelper
	extends LiferayDescriptorHelper implements INewPortletClassDataModelProperties {

	public static final String DESCRIPTOR_FILE = ILiferayConstants.LIFERAY_PORTLET_XML_FILE;

	public LiferayPortletDescriptorHelper() {
	}

	public LiferayPortletDescriptorHelper(IProject project) {
		super(project);
	}

	public IStatus configureLiferayPortletXml(String newPortletName) {
		IStatus status = Status.OK_STATUS;

		IFile descriptorFile = getDescriptorFile();

		if (descriptorFile != null) {
			DOMModelEditOperation operation = new DOMModelEditOperation(descriptorFile) {

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

			};

			status = operation.execute();
		}

		return status;
	}

	public IFile getDescriptorFile() {
		return super.getDescriptorFile(DESCRIPTOR_FILE);
	}

	@Override
	protected void addDescriptorOperations() {
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE liferay-portlet-app PUBLIC \"-//Liferay//");
		sb.append("DTD Portlet Application {0}//EN\" \"http://www.liferay.com/dtd/liferay-portlet-app_{1}.dtd");
		sb.append("\">\n\n<liferay-portlet-app>\n\u0009<role-mapper>\n\u0009\u0009<role-name>administrator</role-");
		sb.append("name>\n\u0009\u0009<role-link>Administrator</role-link>\n\u0009</role-mapper>\n\u0009<role-");
		sb.append("mapper>\n\u0009\u0009<role-name>guest</role-name>\n\u0009\u0009<role-link>Guest</role-link>\n");
		sb.append("\u0009</role-mapper>\n\u0009<role-mapper>\n\u0009\u0009<role-name>power-user</role-name>\n");
		sb.append("\u0009\u0009<role-link>Power User</role-link>\n\u0009</role-mapper>\n\u0009<role-mapper>\n");
		sb.append("\u0009\u0009<role-name>user</role-name>\n\u0009\u0009<role-link>User</role-link>\n\u0009</");
		sb.append("role-mapper>\n</liferay-portlet-app>");

		String descriptorTemplate = sb.toString();

		AddNewPortletOperation apOperation = new AddNewPortletOperation() {

			@Override
			public IStatus addNewPortlet(IDataModel model) {
				IStatus status = Status.OK_STATUS;

				if (canAddNewPortlet(model)) {
					IFile descriptorFile = getDescriptorFile();

					if (descriptorFile != null) {
						DOMModelEditOperation domModelOperation = new DOMModelEditOperation(descriptorFile) {

							protected void createDefaultFile() {
								createDefaultDescriptor(descriptorTemplate, getDescriptorVersion());
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

		RemoveSampleElementsOperation rseOperation = new RemoveSampleElementsOperation() {

			@Override
			public IStatus removeSampleElements() {
				return doRemoveAllPortlets();
			}

		};

		addDescriptorOperation(rseOperation);
	}

	protected boolean canAddNewPortlet(IDataModel model) {
		return StringUtil.contains(model.getID(), "NewPortlet");
	}

	protected IStatus doAddNewPortlet(IDOMDocument document, IDataModel model) {

		// <liferay-portlet-app> element

		Element rootElement = document.getDocumentElement();

		// new <portlet> element

		Element newPortletElement = document.createElement("portlet");

		NodeUtil.appendChildElement(newPortletElement, "portlet-name", model.getStringProperty(LIFERAY_PORTLET_NAME));

		NodeUtil.appendChildElement(newPortletElement, "icon", model.getStringProperty(ICON_FILE));

		if (model.getBooleanProperty(ADD_TO_CONTROL_PANEL)) {
			String entryCategory = model.getStringProperty(ENTRY_CATEGORY);

			entryCategory = entryCategory.replaceAll("^category\\.", StringPool.EMPTY);

			NodeUtil.appendChildElement(newPortletElement, "control-panel-entry-category", entryCategory);

			NodeUtil.appendChildElement(
				newPortletElement, "control-panel-entry-weight", model.getStringProperty(ENTRY_WEIGHT));

			String javaPackage = model.getStringProperty(JAVA_PACKAGE);

			if (!CoreUtil.isNullOrEmpty(javaPackage)) {
				javaPackage = javaPackage + StringPool.PERIOD;
			}

			if (model.getBooleanProperty(CREATE_ENTRY_CLASS)) {
				NodeUtil.appendChildElement(
					newPortletElement, "control-panel-entry-class",
					javaPackage + model.getStringProperty(ENTRY_CLASS_NAME));
			}
		}

		if (model.getBooleanProperty(ALLOW_MULTIPLE)) {
			NodeUtil.appendChildElement(
				newPortletElement, "instanceable", Boolean.toString(model.getBooleanProperty(ALLOW_MULTIPLE)));
		}

		NodeUtil.appendChildElement(newPortletElement, "header-portlet-css", model.getStringProperty(CSS_FILE));

		NodeUtil.appendChildElement(
			newPortletElement, "footer-portlet-javascript", model.getStringProperty(JAVASCRIPT_FILE));

		NodeUtil.appendChildElement(newPortletElement, "css-class-wrapper", model.getStringProperty(CSS_CLASS_WRAPPER));

		// must append this before any role-mapper elements

		Element firstRoleMapper = null;

		for (Element child : getChildElements(rootElement)) {
			if ("role-mapper".equals(child.getNodeName())) {
				firstRoleMapper = child;

				break;
			}
		}

		Node newline = document.createTextNode(System.getProperty("line.separator"));

		if (firstRoleMapper != null) {
			rootElement.insertBefore(newPortletElement, firstRoleMapper);

			rootElement.insertBefore(newline, firstRoleMapper);
		}
		else {
			rootElement.appendChild(newPortletElement);

			rootElement.appendChild(newline);
		}

		// format the new node added to the model;

		FormatProcessorXML processor = new FormatProcessorXML();

		processor.formatNode(newPortletElement);

		return Status.OK_STATUS;
	}

	protected IStatus doRemoveAllPortlets() {
		String portletTagName = "portlet";

		DOMModelEditOperation domModelOperation = new DOMModelEditOperation(getDescriptorFile()) {

			protected IStatus doExecute(IDOMDocument document) {
				return removeAllElements(document, portletTagName);
			}

		};

		IStatus status = domModelOperation.execute();

		return status;
	}

}