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
import com.liferay.ide.core.util.NodeUtil;
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
public class LiferayDisplayDescriptorHelper
	extends LiferayDescriptorHelper implements INewPortletClassDataModelProperties {

	public static final String DESCRIPTOR_FILE = ILiferayConstants.LIFERAY_DISPLAY_XML_FILE;

	public LiferayDisplayDescriptorHelper() {
	}

	public LiferayDisplayDescriptorHelper(IProject project) {
		super(project);
	}

	public IStatus configureLiferayDisplayXml(String newPortletName) {
		IStatus status = new DOMModelEditOperation(getDescriptorFile()) {

			protected IStatus doExecute(IDOMDocument document) {
				Element rootElement = document.getDocumentElement();

				NodeList portletNodes = rootElement.getElementsByTagName("category");

				if (portletNodes.getLength() > 0) {
					Element lastPortletElement = (Element)portletNodes.item(portletNodes.getLength() - 1);

					Element portletName = NodeUtil.findChildElement(lastPortletElement, "portlet");

					portletName.setAttribute("id", newPortletName);
				}

				return Status.OK_STATUS;
			}

		}.execute();

		return status;
	}

	public String[] getAllPortletCategories() {
		List<String> allPortletCategories = new ArrayList<>();

		IFile descriptorFile = getDescriptorFile();

		if (descriptorFile != null) {
			DOMModelOperation op = new DOMModelReadOperation(descriptorFile) {

				protected IStatus doExecute(IDOMDocument document) {
					NodeList nodeList = document.getElementsByTagName("category");

					if ((nodeList != null) && (nodeList.getLength() > 0)) {
						for (int i = 0; i < nodeList.getLength(); i++) {
							Element categoryElemnt = (Element)nodeList.item(i);

							String categoryName = categoryElemnt.getAttribute("name");

							if ((categoryName != null) && !categoryName.matches("\\s*")) {
								allPortletCategories.add(categoryName);
							}
						}
					}

					return Status.OK_STATUS;
				}

			};

			op.execute();
		}

		return allPortletCategories.toArray(new String[0]);
	}

	public IFile getDescriptorFile() {
		return super.getDescriptorFile(DESCRIPTOR_FILE);
	}

	@Override
	protected void addDescriptorOperations() {
		AddNewPortletOperation anpOperation = new AddNewPortletOperation() {

			@Override
			public IStatus addNewPortlet(IDataModel model) {
				IStatus status = Status.OK_STATUS;

				IFile descriptorFile = getDescriptorFile();

				if (descriptorFile != null) {
					DOMModelEditOperation domModelOperation = new DOMModelEditOperation(descriptorFile) {

						protected void createDefaultFile() {
							createDefaultDescriptor(_DESCRIPTOR_TEMPLATE, getDescriptorVersion());
						}

						protected IStatus doExecute(IDOMDocument document) {
							return doAddNewPortlet(document, model);
						}

					};

					status = domModelOperation.execute();
				}

				return status;
			}

		};

		addDescriptorOperation(anpOperation);

		RemoveAllPortletsOperation rapOperation = new RemoveAllPortletsOperation() {

			@Override
			public IStatus removeAllPortlets() {
				return removeAllPortlets();
			}

		};

		addDescriptorOperation(rapOperation);

		RemoveSampleElementsOperation rseOperation = new RemoveSampleElementsOperation() {

			@Override
			public IStatus removeSampleElements() {
				return removeAllPortlets();
			}

		};

		addDescriptorOperation(rseOperation);
	}

	protected IStatus doAddNewPortlet(IDOMDocument document, IDataModel model) {

		// <display> element

		Element rootElement = document.getDocumentElement();

		// for the category assignment check to see if there is already a
		// category element with that id

		Element category = null;

		String modelCategory = model.getStringProperty(CATEGORY);

		for (Element child : getChildElements(rootElement)) {
			if ("category".equals(child.getNodeName()) && modelCategory.equals(child.getAttribute("name"))) {
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
				if ("portlet".equals(child.getNodeName()) && modelId.equals(child.getAttribute("id"))) {
					id = child;

					break;
				}
			}
		}
		else {
			category = document.createElement("category");

			category.setAttribute("name", modelCategory);

			rootElement.appendChild(category);

			Node newline = document.createTextNode(System.getProperty("line.separator"));

			rootElement.appendChild(newline);
		}

		if (id == null) {
			Element element = NodeUtil.appendChildElement(category, "portlet");

			element.setAttribute("id", modelId);
		}

		// format the new node added to the model;

		FormatProcessorXML processor = new FormatProcessorXML();

		processor.formatNode(category);

		return Status.OK_STATUS;
	}

	protected IStatus removeAllPortlets() {
		String categoryTagName = "category";

		DOMModelEditOperation domModelOperation = new DOMModelEditOperation(getDescriptorFile()) {

			protected IStatus doExecute(IDOMDocument document) {
				return removeAllElements(document, categoryTagName);
			}

		};

		IStatus status = domModelOperation.execute();

		return status;
	}

	private static final String _DESCRIPTOR_TEMPLATE =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE display PUBLIC \"-//Liferay//DTD Display {0}//EN\\\" " +
			"\"http://www.liferay.com/dtd/liferay-display_{1}.dtd\">\n\n<display>\n</display>";

}