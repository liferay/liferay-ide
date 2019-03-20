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

package com.liferay.ide.portlet.vaadin.core.dd;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.portlet.core.dd.LiferayPortletDescriptorHelper;
import com.liferay.ide.portlet.vaadin.core.operation.INewVaadinPortletClassDataModelProperties;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;

import org.osgi.framework.Version;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Henri Sara
 * @author Tao Tao
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class VaadinLiferayPortletDescriptorHelper
	extends LiferayPortletDescriptorHelper implements INewVaadinPortletClassDataModelProperties {

	public VaadinLiferayPortletDescriptorHelper() {
	}

	public VaadinLiferayPortletDescriptorHelper(IProject project) {
		super(project);
	}

	@Override
	public boolean canAddNewPortlet(IDataModel model) {
		return StringUtil.contains(model.getID(), "NewVaadinPortlet");
	}

	@Override
	protected void addDescriptorOperations() {
		super.addDescriptorOperations();
	}

	@Override
	protected IStatus doAddNewPortlet(IDOMDocument document, IDataModel model) {
		IStatus status = Status.OK_STATUS;

		status = super.doAddNewPortlet(document, model);

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
						return _updateVaadinLiferayPortletXMLTo62(document);
					}

				};

				return op.execute();
			}
		}

		return status;
	}

	private IStatus _updateVaadinLiferayPortletXMLTo62(IDOMDocument document) {
		Element rootElement = document.getDocumentElement();

		NodeList portletNodes = rootElement.getElementsByTagName("portlet");

		if (portletNodes.getLength() > 1) {
			Element lastPortletElement = (Element)portletNodes.item(portletNodes.getLength() - 1);

			Node rnpNode = NodeUtil.appendChildElement(lastPortletElement, "requires-namespaced-parameters", "false");
			Node ajaxNode = NodeUtil.appendChildElement(lastPortletElement, "ajaxable", "false");

			NodeList headerPortletCssNodeList = lastPortletElement.getElementsByTagName("header-portlet-css");

			Node hpcNode = headerPortletCssNodeList.item(0);

			NodeList footerPortletJavascriptNodeList = lastPortletElement.getElementsByTagName(
				"footer-portlet-javascript");

			Node fpjNode = footerPortletJavascriptNodeList.item(0);

			lastPortletElement.replaceChild(rnpNode, hpcNode);
			lastPortletElement.replaceChild(ajaxNode, fpjNode);
		}

		return Status.OK_STATUS;
	}

}