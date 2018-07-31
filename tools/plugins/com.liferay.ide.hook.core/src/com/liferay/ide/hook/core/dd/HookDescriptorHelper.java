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

package com.liferay.ide.hook.core.dd;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.hook.core.operation.INewHookDataModelProperties;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
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
 * @author Terry Jia
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class HookDescriptorHelper extends LiferayDescriptorHelper implements INewHookDataModelProperties {

	public static final String DESCRIPTOR_FILE = ILiferayConstants.LIFERAY_HOOK_XML_FILE;

	public HookDescriptorHelper(IProject project) {
		super(project);
	}

	public IStatus addActionItems(final List<String[]> actionItems) {
		IFile descriptorFile = getDescriptorFile();

		DOMModelOperation operation = new DOMModelEditOperation(descriptorFile) {

			protected void createDefaultFile() {
				createDefaultDescriptor(_HOOK_DESCRIPTOR_TEMPLATE, getDescriptorVersion());
			}

			protected IStatus doExecute(IDOMDocument document) {
				return doAddActionItems(document, actionItems);
			}

		};

		IStatus status = operation.execute();

		if (!status.isOK()) {
			return status;
		}

		return status;
	}

	public IStatus addLanguageProperties(final List<String> languageProperties) {
		IFile descriptorFile = getDescriptorFile();

		DOMModelOperation operation = new DOMModelEditOperation(descriptorFile) {

			protected void createDefaultFile() {
				createDefaultDescriptor(_HOOK_DESCRIPTOR_TEMPLATE, getDescriptorVersion());
			}

			protected IStatus doExecute(IDOMDocument document) {
				return doAddLanguageProperties(document, languageProperties);
			}

		};

		IStatus status = operation.execute();

		if (!status.isOK()) {
			return status;
		}

		return status;
	}

	public void createDefaultDescriptor() {
		IFile descriptorFile = getDescriptorFile();

		DOMModelEditOperation operation = new DOMModelEditOperation(descriptorFile) {

			@Override
			protected void createDefaultFile() {
				createDefaultDescriptor(_HOOK_DESCRIPTOR_TEMPLATE, getDescriptorVersion());
			}

			@Override
			protected IStatus doExecute(IDOMDocument document) {
				return Status.OK_STATUS;
			}

		};

		operation.execute();
	}

	public IStatus doSetCustomJSPDir(IDOMDocument document, IDataModel model) {

		// <hook> element

		Element rootElement = document.getDocumentElement();
		String customJSPsFolder = model.getStringProperty(CUSTOM_JSPS_FOLDER);
		IWebProject lrproject = LiferayCore.create(IWebProject.class, project);

		if (lrproject != null) {
			IFolder folder = lrproject.getDefaultDocrootFolder();

			IPath defaultWebappRootFolderFullPath = folder.getFullPath();

			String relativeJspFolderPath = ProjectUtil.getRelativePathFromDocroot(
				lrproject, FileUtil.toPortableString(defaultWebappRootFolderFullPath.append(customJSPsFolder)));

			Element customJspElement = null;

			// check for existing element

			NodeList nodeList = rootElement.getElementsByTagName("custom-jsp-dir");

			if ((nodeList != null) && (nodeList.getLength() > 0)) {
				customJspElement = (Element)nodeList.item(0);

				NodeUtil.removeChildren(customJspElement);

				Node textNode = document.createTextNode(relativeJspFolderPath);

				customJspElement.appendChild(textNode);
			}
			else {

				// need to insert customJspElement before any <service>

				NodeList serviceTags = rootElement.getElementsByTagName("service");

				if ((serviceTags != null) && (serviceTags.getLength() > 0)) {
					customJspElement = NodeUtil.insertChildElement(
						rootElement, serviceTags.item(0), "custom-jsp-dir", relativeJspFolderPath);
				}
				else {
					customJspElement = NodeUtil.appendChildElement(
						rootElement, "custom-jsp-dir", relativeJspFolderPath);

					// append a newline text node

					rootElement.appendChild(document.createTextNode(System.getProperty("line.separator")));
				}
			}

			// format the new node added to the model;

			FormatProcessorXML processor = new FormatProcessorXML();

			processor.formatNode(customJspElement);
		}

		return Status.OK_STATUS;
	}

	public String getCustomJSPFolder(final IDataModel model) {
		String[] retval = new String[1];

		IFile descriptorFile = getDescriptorFile();

		if (FileUtil.notExists(descriptorFile)) {
			return null;
		}

		DOMModelOperation operation = new DOMModelReadOperation(descriptorFile) {

			protected IStatus doExecute(IDOMDocument document) {
				retval[0] = readCustomJSPFolder(document, model);

				return Status.OK_STATUS;
			}

		};

		IStatus status = operation.execute();

		if (!status.isOK()) {
			return null;
		}

		return retval[0];
	}

	@Override
	public IFile getDescriptorFile() {
		return super.getDescriptorFile(DESCRIPTOR_FILE);
	}

	public String readCustomJSPFolder(IDOMDocument document, IDataModel model) {

		// <hook> element

		Element rootElement = document.getDocumentElement();

		Element customJspElement = null;

		// check for existing element

		NodeList nodeList = rootElement.getElementsByTagName("custom-jsp-dir");

		if ((nodeList != null) && (nodeList.getLength() > 0)) {
			customJspElement = (Element)nodeList.item(0);

			Node node = customJspElement.getFirstChild();

			return node.getNodeValue();
		}

		return null;
	}

	public IStatus setCustomJSPDir(final IDataModel model) {
		final IFile descriptorFile = getDescriptorFile();

		DOMModelOperation operation = new DOMModelEditOperation(descriptorFile) {

			protected void createDefaultFile() {
				createDefaultDescriptor(_HOOK_DESCRIPTOR_TEMPLATE, getDescriptorVersion());
			}

			protected IStatus doExecute(IDOMDocument document) {
				return doSetCustomJSPDir(document, model);
			}

		};

		IStatus status = operation.execute();

		if (!status.isOK()) {
			return status;
		}

		return status;
	}

	public IStatus setPortalProperties(final IDataModel model, final String propertiesFile) {
		final IFile descriptorFile = getDescriptorFile();

		DOMModelOperation operation = new DOMModelEditOperation(descriptorFile) {

			protected void createDefaultFile() {
				createDefaultDescriptor(_HOOK_DESCRIPTOR_TEMPLATE, getDescriptorVersion());
			}

			protected IStatus doExecute(IDOMDocument document) {
				return doSetPortalProperties(document, model, propertiesFile);
			}

		};

		IStatus status = operation.execute();

		if (!status.isOK()) {
			return status;
		}

		return status;
	}

	@Override
	protected void addDescriptorOperations() {

		// currently, no descriptor operations for this descriptor

	}

	protected IStatus doAddActionItems(IDOMDocument document, List<String[]> actionItems) {

		// <hook> element

		Element rootElement = document.getDocumentElement();

		FormatProcessorXML processor = new FormatProcessorXML();

		Element newServiceElement = null;

		if (actionItems != null) {
			for (String[] actionItem : actionItems) {
				newServiceElement = NodeUtil.appendChildElement(rootElement, "service");

				NodeUtil.appendChildElement(newServiceElement, "service-type", actionItem[0]);

				NodeUtil.appendChildElement(newServiceElement, "service-impl", actionItem[1]);

				processor.formatNode(newServiceElement);
			}

			if (newServiceElement != null) {

				// append a newline text node

				rootElement.appendChild(document.createTextNode(System.getProperty("line.separator")));

				processor.formatNode(newServiceElement);
			}
		}

		return Status.OK_STATUS;
	}

	protected IStatus doAddLanguageProperties(IDOMDocument document, List<String> languageProperties) {

		// <hook> element

		Element rootElement = document.getDocumentElement();

		FormatProcessorXML processor = new FormatProcessorXML();

		Element newLanguageElement = null;

		// check if we have existing custom_dir

		Node refChild = null;

		NodeList nodeList = rootElement.getElementsByTagName("custom-jsp-dir");

		if ((nodeList != null) && (nodeList.getLength() > 0)) {
			refChild = nodeList.item(0);
		}
		else {
			nodeList = rootElement.getElementsByTagName("service");

			if ((nodeList != null) && (nodeList.getLength() > 0)) {
				refChild = nodeList.item(0);
			}
		}

		if (languageProperties != null) {
			for (String languageProperty : languageProperties) {
				newLanguageElement = NodeUtil.insertChildElement(
					rootElement, refChild, "language-properties", languageProperty);

				processor.formatNode(newLanguageElement);
			}

			if (newLanguageElement != null) {

				// append a newline text node

				rootElement.appendChild(document.createTextNode(System.getProperty("line.separator")));

				processor.formatNode(newLanguageElement);
			}
		}

		return Status.OK_STATUS;
	}

	protected IStatus doSetPortalProperties(IDOMDocument document, IDataModel model, String propertiesFile) {

		// <hook> element

		Element rootElement = document.getDocumentElement();

		// check for existing element

		Element portalPropertiesElement = null;

		NodeList nodeList = rootElement.getElementsByTagName("portal-properties");

		if ((nodeList != null) && (nodeList.getLength() > 0)) {
			portalPropertiesElement = (Element)nodeList.item(0);

			NodeUtil.removeChildren(portalPropertiesElement);

			Node textNode = document.createTextNode(propertiesFile);

			portalPropertiesElement.appendChild(textNode);
		}
		else {
			portalPropertiesElement = NodeUtil.insertChildElement(
				rootElement, rootElement.getFirstChild(), "portal-properties", propertiesFile);
		}

		// format the new node added to the model;

		FormatProcessorXML processor = new FormatProcessorXML();

		processor.formatNode(portalPropertiesElement);

		return Status.OK_STATUS;
	}

	private static final String _HOOK_DESCRIPTOR_TEMPLATE =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE hook PUBLIC \"-//Liferay//DTD Hook {0}//EN\" " +
			"\"http://www.liferay.com/dtd/liferay-hook_{1}.dtd\">\n\n<hook>\n</hook>";

}