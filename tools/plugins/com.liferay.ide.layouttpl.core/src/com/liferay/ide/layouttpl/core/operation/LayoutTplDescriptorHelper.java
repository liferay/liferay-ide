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

package com.liferay.ide.layouttpl.core.operation;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;

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
 */
@SuppressWarnings("restriction")
public class LayoutTplDescriptorHelper extends LiferayDescriptorHelper implements INewLayoutTplDataModelProperties {

	public static final String DESCRIPTOR_FILE = ILiferayConstants.LIFERAY_LAYOUTTPL_XML_FILE;

	public LayoutTplDescriptorHelper(IProject project) {
		super(project);
	}

	public IStatus addNewLayoutTemplate(IDataModel dm) {
		DOMModelOperation operation = new DOMModelEditOperation(getDescriptorFile()) {

			protected void createDefaultFile() {
				createDefaultDescriptor(_LAYOUT_DESCRIPTOR_TEMPLATE, getDescriptorVersion());
			}

			protected IStatus doExecute(IDOMDocument document) {
				return doAddLayoutTemplate(document, dm);
			}

		};

		IStatus status = operation.execute();

		if (!status.isOK()) {
			return status;
		}

		return status;
	}

	public IStatus doAddLayoutTemplate(IDOMDocument document, IDataModel model) {
		Element rootElement = document.getDocumentElement();

		Element layoutTemplateElement = document.createElement("layout-template");

		layoutTemplateElement.setAttribute("id", model.getStringProperty(LAYOUT_TEMPLATE_ID));
		layoutTemplateElement.setAttribute("name", model.getStringProperty(LAYOUT_TEMPLATE_NAME));

		// find the <custom> element and if it doesn't exist create it

		Node customElement = NodeUtil.getFirstNamedChildNode(rootElement, "custom");

		if (customElement == null) {

			// if we are going to create a new <custom> it must be after the <standard>

			Node standardElement = NodeUtil.getFirstNamedChildNode(rootElement, "standard");

			customElement = document.createElement("custom");

			rootElement.insertBefore(customElement, standardElement);

			NodeUtil.appendTextNode(rootElement, "\n");
		}

		customElement.appendChild(layoutTemplateElement);

		/**
		 * now that we have the new <layout-template> element added to custom element,
		 * add the child nodes to layout
		 */
		String templatePath = model.getStringProperty(LAYOUT_TEMPLATE_FILE);
		String wapTemplatePath = model.getStringProperty(LAYOUT_WAP_TEMPLATE_FILE);
		String thumbnailPath = model.getStringProperty(LAYOUT_THUMBNAIL_FILE);

		NodeUtil.appendChildElement(layoutTemplateElement, "template-path", templatePath);

		if (getDescriptorVersion().equals("6.2.0")) {
			NodeUtil.appendChildElement(layoutTemplateElement, "wap-template-path", wapTemplatePath);
		}

		NodeUtil.appendChildElement(layoutTemplateElement, "thumbnail-path", thumbnailPath);

		// format the new node added to the model;

		FormatProcessorXML processor = new FormatProcessorXML();

		processor.formatNode(customElement);

		return Status.OK_STATUS;
	}

	@Override
	public IFile getDescriptorFile() {
		return super.getDescriptorFile(DESCRIPTOR_FILE);
	}

	public boolean hasTemplateId(String templateId) {
		if (CoreUtil.isNullOrEmpty(templateId)) {
			return false;
		}

		boolean[] retval = new boolean[1];

		DOMModelOperation operation = new DOMModelReadOperation(getDescriptorFile()) {

			@Override
			protected IStatus doExecute(IDOMDocument document) {
				NodeList layoutTemplates = document.getElementsByTagName("layout-template");

				if ((layoutTemplates != null) && (layoutTemplates.getLength() > 0)) {
					for (int i = 0; i < layoutTemplates.getLength(); i++) {
						Element layoutTemplate = (Element)layoutTemplates.item(i);

						if (templateId.equals(layoutTemplate.getAttribute("id"))) {
							retval[0] = true;

							break;
						}
					}
				}

				return Status.OK_STATUS;
			}

		};

		operation.execute();

		return retval[0];
	}

	@Override
	protected void addDescriptorOperations() {
	}

	private static final String _ADOPT_PUBLIC_ID =
		"<!DOCTYPE layout-templates PUBLIC \"-//Liferay//DTD Layout Templates {0}//EN\" ";

	private static final String _ADOPT_SYSTEM_ID =
		"\"http://www.liferay.com/dtd/liferay-layout-templates_{1}.dtd\">\n\n<layout-templates>\n</layout-templates>\n";

	private static final String _ENCODING_AND_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

	private static final String _LAYOUT_DESCRIPTOR_TEMPLATE =
		_ENCODING_AND_VERSION + _ADOPT_PUBLIC_ID + _ADOPT_SYSTEM_ID;

}