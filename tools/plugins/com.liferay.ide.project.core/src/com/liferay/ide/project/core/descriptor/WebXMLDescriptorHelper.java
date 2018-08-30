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

package com.liferay.ide.project.core.descriptor;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jst.j2ee.jsp.JSPConfig;
import org.eclipse.jst.j2ee.jsp.JspFactory;
import org.eclipse.jst.j2ee.jsp.TagLibRefType;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.jst.j2ee.webapplication.WebApp;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings({"restriction", "rawtypes", "unchecked"})
public class WebXMLDescriptorHelper extends LiferayDescriptorHelper {

	public WebXMLDescriptorHelper(IProject project) {
		super(project);

		setDescriptorPath(ILiferayConstants.WEB_XML_FILE);
	}

	public IStatus addTagLib(TagLibRefType tagLibRefType) {
		IFile file = getDescriptorFile(getDescriptorPath());

		IStatus status = null;

		if (FileUtil.exists(file)) {
			DOMModelOperation op = new DOMModelEditOperation(file) {

				protected void createDefaultFile() {

					// never create the file

				}

				protected IStatus doExecute(IDOMDocument document) {
					return doAddTagLib(document, tagLibRefType);
				}

			};

			status = op.execute();
		}
		else {
			WebArtifactEdit webArtifactEdit = WebArtifactEdit.getWebArtifactEditForWrite(project);

			WebApp webApp = webArtifactEdit.getWebApp();

			if (tagLibReferenceExists(webApp, tagLibRefType)) {
				return Status.OK_STATUS;
			}

			// webApp.setFileList(null);

			JSPConfig jspConfig = webApp.getJspConfig();

			if ((jspConfig == null) && (webApp.getVersionID() != 23)) {
				jspConfig = JspFactory.eINSTANCE.createJSPConfig();
			}

			if (jspConfig != null) {
				EList eList = jspConfig.getTagLibs();

				eList.add(tagLibRefType);
			}
			else {
				EList tagLibs = webApp.getTagLibs();

				tagLibs.add(tagLibRefType);
			}

			if (jspConfig != null) {
				webApp.setJspConfig(jspConfig);
			}

			webArtifactEdit.save(null);

			webArtifactEdit.dispose();

			status = Status.OK_STATUS;
		}

		if (!status.isOK()) {
			return status;
		}

		return status;
	}

	public IStatus deleteWelcomeFileListElements() {
		IFile file = getDescriptorFile(getDescriptorPath());

		if (FileUtil.notExists(file)) {
			return null;
		}

		IStatus status = new DOMModelEditOperation(file) {

			protected void createDefaultFile() {

				// never create the file

			}

			protected IStatus doExecute(IDOMDocument document) {
				try {
					NodeList welcomeFileLists = document.getElementsByTagName("welcome-file-list");

					for (int i = 0; i < welcomeFileLists.getLength(); i++) {
						Node welcomeFileList = welcomeFileLists.item(i);

						Node parentNode = welcomeFileList.getParentNode();

						parentNode.removeChild(welcomeFileList);
					}
				}
				catch (Exception e) {
					return ProjectCore.createErrorStatus(e);
				}

				return Status.OK_STATUS;
			}

		}.execute();

		return status;
	}

	@Override
	public IFile getDescriptorFile() {
		return super.getDescriptorFile(_DESCRIPTOR_FILE);
	}

	@Override
	protected void addDescriptorOperations() {

		// currently, no descriptor operations for this descriptor

	}

	protected IStatus doAddTagLib(IDOMDocument document, TagLibRefType tagLibRefType) {
		if (tagLibReferenceExists(document, tagLibRefType)) {
			return Status.OK_STATUS;
		}

		String typeId = document.getDocumentTypeId();

		Element rootElement = document.getDocumentElement();

		if ((typeId != null) && typeId.contains("2.3")) {
			Element taglibNextSibling = NodeUtil.findChildElement(rootElement, "resource-env-ref");

			if (taglibNextSibling == null) {
				taglibNextSibling = NodeUtil.findChildElement(rootElement, "resource-ref");
			}

			if (taglibNextSibling == null) {
				taglibNextSibling = NodeUtil.findChildElement(rootElement, "security-constraint");
			}

			if (taglibNextSibling == null) {
				taglibNextSibling = NodeUtil.findChildElement(rootElement, "login-config");
			}

			if (taglibNextSibling == null) {
				taglibNextSibling = NodeUtil.findChildElement(rootElement, "security-role");
			}

			if (taglibNextSibling == null) {
				taglibNextSibling = NodeUtil.findChildElement(rootElement, "env-entry");
			}

			if (taglibNextSibling == null) {
				taglibNextSibling = NodeUtil.findChildElement(rootElement, "ejb-ref");
			}

			if (taglibNextSibling == null) {
				taglibNextSibling = NodeUtil.findChildElement(rootElement, "ejb-local-ref");
			}

			Element taglib = NodeUtil.insertChildElement(rootElement, taglibNextSibling, "taglib", StringPool.EMPTY);

			NodeUtil.appendChildElement(taglib, "taglib-uri", tagLibRefType.getTaglibURI());

			NodeUtil.appendChildElement(taglib, "taglib-location", tagLibRefType.getTaglibLocation());

			if (taglibNextSibling == null) {
				rootElement.appendChild(document.createTextNode(System.getProperty("line.separator")));
			}

			// format the new node added to the model;

			FormatProcessorXML processor = new FormatProcessorXML();

			processor.formatNode(taglib);
		}
		else {
			Element jspConfig = NodeUtil.findChildElement(rootElement, "jsp-config");

			if (jspConfig == null) {
				jspConfig = NodeUtil.appendChildElement(rootElement, "jsp-config");
			}

			Element taglib = NodeUtil.appendChildElement(jspConfig, "taglib");

			NodeUtil.appendChildElement(taglib, "taglib-uri", tagLibRefType.getTaglibURI());

			NodeUtil.appendChildElement(taglib, "taglib-location", tagLibRefType.getTaglibLocation());

			rootElement.appendChild(document.createTextNode(System.getProperty("line.separator")));

			// format the new node added to the model;

			FormatProcessorXML processor = new FormatProcessorXML();

			processor.formatNode(jspConfig);
		}

		return Status.OK_STATUS;
	}

	protected boolean tagLibReferenceExists(IDOMDocument document, TagLibRefType tagLibRefType) {
		NodeList taglibs = document.getElementsByTagName("taglib");

		for (int i = 0; i < taglibs.getLength(); i++) {
			Node taglib = taglibs.item(i);

			String taglibUri = StringUtil.trim(NodeUtil.getChildElementContent(taglib, "taglib-uri"));
			String taglibLocation = StringUtil.trim(NodeUtil.getChildElementContent(taglib, "taglib-location"));

			boolean taglibUriEquals = taglibUri.equals(StringUtil.trim(tagLibRefType.getTaglibURI()));
			boolean taglibLocationEquals = taglibLocation.equals(StringUtil.trim(tagLibRefType.getTaglibLocation()));

			if (taglibUriEquals && taglibLocationEquals) {
				return true;
			}
		}

		return false;
	}

	protected boolean tagLibReferenceExists(WebApp webApp, TagLibRefType tagLibRefType) {
		EList taglibs = webApp.getTagLibs();

		if (taglibs != null) {
			for (Object taglib : taglibs) {
				if (taglib instanceof TagLibRefType) {
					TagLibRefType existingTaglib = (TagLibRefType)taglib;

					if (existingTaglib.equals(tagLibRefType)) {
						return true;
					}
				}
			}
		}

		JSPConfig config = webApp.getJspConfig();

		if (config == null) {
			return false;
		}

		taglibs = config.getTagLibs();

		if (taglibs == null) {
			return false;
		}

		for (Object taglib : taglibs) {
			if (taglib instanceof TagLibRefType) {
				TagLibRefType existingTaglib = (TagLibRefType)taglib;

				if (existingTaglib.equals(tagLibRefType)) {
					return true;
				}
			}
		}

		return false;
	}

	private static final String _DESCRIPTOR_FILE = ILiferayConstants.WEB_XML_FILE;

}