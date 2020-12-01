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

package com.liferay.ide.portlet.core.jsf;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.core.dd.LiferayPortletDescriptorHelper;
import com.liferay.ide.project.core.descriptor.UpdateDescriptorVersionOperation;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;

import org.osgi.framework.Version;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class JSFLiferayPortletDescriptorHelper
	extends LiferayPortletDescriptorHelper implements INewJSFPortletClassDataModelProperties {

	public JSFLiferayPortletDescriptorHelper() {
	}

	public JSFLiferayPortletDescriptorHelper(IProject project) {
		super(project);
	}

	@Override
	protected void addDescriptorOperations() {
		super.addDescriptorOperations();

		UpdateDescriptorVersionOperation operation = new UpdateDescriptorVersionOperation() {

			@Override
			public IStatus update(Version preVersion, Version postVersion) {
				if ((CoreUtil.compareVersions(preVersion, ILiferayConstants.V620) >= 0) &&
					(CoreUtil.compareVersions(postVersion, ILiferayConstants.V620) < 0)) {

					IFile descriptorFile = getDescriptorFile();

					IDOMModel domModel = null;

					try {
						IModelManager modelManager = StructuredModelManager.getModelManager();

						domModel = (IDOMModel)modelManager.getModelForEdit(descriptorFile);

						IStatus status = _downgradeJSFLiferayPortletXMLTo61(domModel.getDocument());

						if (!status.isOK()) {
							return status;
						}

						domModel.save();
					}
					catch (Exception e) {
						PortletCore.logError("Error editing liferay-portlet.xml", e);
					}
					finally {
						if (domModel != null) {
							domModel.releaseFromEdit();
						}
					}
				}

				return Status.OK_STATUS;
			}

		};

		addDescriptorOperation(operation);
	}

	@Override
	protected boolean canAddNewPortlet(IDataModel model) {
		return StringUtil.contains(model.getID(), "NewJSFPortlet");
	}

	@Override
	protected IStatus doAddNewPortlet(IDOMDocument document, IDataModel model) {
		IStatus status = super.doAddNewPortlet(document, model);

		if (!status.isOK()) {
			return status;
		}

		Version runtimeVersion = ServerUtil.getRuntimeVersion(project);

		// Runtime version should be equal or greater than 6.2.

		if (CoreUtil.compareVersions(runtimeVersion, ILiferayConstants.V620) >= 0) {
			IFile descriptorFile = getDescriptorFile();

			if (descriptorFile != null) {
				DOMModelOperation op = new DOMModelEditOperation(descriptorFile) {

					@Override
					protected void createDefaultFile() {

						// Getting document from super( descriptorFile );

					}

					@Override
					protected IStatus doExecute(IDOMDocument document) {
						return _updateJSFLiferayPortletXMLTo62(document);
					}

				};

				return op.execute();
			}
		}

		return Status.OK_STATUS;
	}

	private IStatus _downgradeJSFLiferayPortletXMLTo61(IDOMDocument document) {
		Element rootElement = document.getDocumentElement();

		NodeList requiresELements = rootElement.getElementsByTagName("requires-namespaced-parameters");

		for (int i = 0; i < requiresELements.getLength(); i++) {
			Node requiresElement = requiresELements.item(i);

			Node prevNode = requiresElement.getPreviousSibling();
			Node parentNode = requiresElement.getParentNode();

			String nodeValue = prevNode.getNodeValue();

			if ((prevNode != null) && (prevNode.getNodeType() == Node.TEXT_NODE) &&
				CoreUtil.isNullOrEmpty(nodeValue.trim())) {

				parentNode.removeChild(prevNode);
			}

			parentNode.removeChild(requiresElement);
		}

		new FormatProcessorXML().formatNode(rootElement);

		return Status.OK_STATUS;
	}

	private IStatus _updateJSFLiferayPortletXMLTo62(IDOMDocument document) {
		Element rootElement = document.getDocumentElement();

		NodeList portletNodes = rootElement.getElementsByTagName("portlet");

		if (portletNodes.getLength() > 1) {
			Element lastPortletElement = (Element)portletNodes.item(portletNodes.getLength() - 1);

			NodeList nodeList = lastPortletElement.getElementsByTagName("header-portlet-css");

			Node headerPortletClassElement = nodeList.item(0);

			NodeUtil.insertChildElement(
				lastPortletElement, headerPortletClassElement, "requires-namespaced-parameters", "false");

			new FormatProcessorXML().formatNode(lastPortletElement);
		}

		return Status.OK_STATUS;
	}

}