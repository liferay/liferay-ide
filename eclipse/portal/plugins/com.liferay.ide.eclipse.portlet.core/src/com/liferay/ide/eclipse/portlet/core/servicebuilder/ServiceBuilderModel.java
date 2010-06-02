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

package com.liferay.ide.eclipse.portlet.core.servicebuilder;

import com.liferay.ide.eclipse.core.model.AbstractModel;
import com.liferay.ide.eclipse.core.model.IModelChangedEvent;
import com.liferay.ide.eclipse.core.model.ModelChangedEvent;
import com.liferay.ide.eclipse.core.util.NodeUtil;
import com.liferay.ide.eclipse.portlet.core.IServiceBuilderModel;
import com.liferay.ide.eclipse.portlet.core.PortletCore;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.document.NodeImpl;
import org.eclipse.wst.xml.core.internal.document.TextImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class ServiceBuilderModel extends AbstractModel implements IServiceBuilderModel, INodeAdapter {

	protected static final long serialVersionUID = 1L;

	protected Node authorTextNode;

	protected IDOMDocument document;

	protected ElementImpl documentElement;

	protected IDOMModel domModel;

	protected IFile file;

	protected Node namespaceTextNode;

	public ServiceBuilderModel(IFile file) {
		this.file = file;

		try {
			load();
		}
		catch (CoreException e) {
			PortletCore.logError(e);
		}
	}

	@Override
	public void dispose() {
		if (domModel != null) {
			domModel.releaseFromEdit();
		}

		super.dispose();
	}

	public String getAuthor() {
		NodeList elements = document.getDocumentElement().getElementsByTagName(IServiceBuilderModel.ELEMENT_AUTHOR);

		if (elements.getLength() > 0) {
			Node text = elements.item(0).getFirstChild();

			if (text != null) {
				return text.getNodeValue();
			}
		}

		return null;
	}

	public String getNamespace() {
		NodeList elements = document.getDocumentElement().getElementsByTagName(IServiceBuilderModel.ELEMENT_NAMESPACE);

		if (elements.getLength() > 0) {
			Node text = elements.item(0).getFirstChild();

			if (text instanceof NodeImpl) {
				((NodeImpl) text).addAdapter(this);
				namespaceTextNode = text;
			}

			if (text != null) {
				return text.getNodeValue();
			}
		}

		return null;
	}

	public String getPackagePath() {
		return document.getDocumentElement().getAttribute(IServiceBuilderModel.ATTR_PACKAGE_PATH);
	}

	public boolean isAdapterForType(Object type) {
		return type != null && INodeAdapter.class == type;
	}

	public boolean isEditable() {
		return true;
	}

	public boolean isInSync() {
		return false;
	}

	public void load()
		throws CoreException {

		try {
			domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(file);

			document = domModel.getDocument();

			fireModelChanged(new ModelChangedEvent(this, IModelChangedEvent.WORLD_CHANGED, null, null));

			// documentElement = (ElementImpl)document.getDocumentElement();
			// documentElement.addAdapter(this);

			// NodeList elements =
			// document.getDocumentElement().getElementsByTagName("author");
			// if (elements.getLength() > 0) {
			// Node text = elements.item(0).getFirstChild();
			// if (text instanceof Text) {
			// authorTextNode = text;
			// ((TextImpl)authorTextNode).addAdapter(this);
			// }
			// }
			//			
			// elements =
			// document.getDocumentElement().getElementsByTagName("namespace");
			// if (elements.getLength() > 0) {
			// Node text = elements.item(0).getFirstChild();
			// if (text instanceof Text) {
			// namespaceTextNode = text;
			// ((TextImpl)namespaceTextNode).addAdapter(this);
			// }
			// }

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load(InputStream source, boolean outOfSync)
		throws CoreException {

	}

	public void notifyChanged(
		INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {

		// if (notifier != null && notifier.equals(namespaceTextNode)) {
		// fireModelChanged(new ModelChangedEvent(this, namespaceTextNode,
		// PROPERTY_NAMESPACE, oldValue, newValue));
		// } else if (notifier != null && notifier.equals(authorTextNode)) {
		// fireModelChanged(new ModelChangedEvent(this, authorTextNode,
		// PROPERTY_AUTHOR, oldValue, newValue));
		// } else if (notifier != null && notifier.equals(documentElement) &&
		// changedFeature instanceof Attr) {
		// Attr attr = (Attr)changedFeature;
		// if (attr.getNodeName().equals(PROPERTY_PACKAGE_PATH)) {
		// fireModelChanged(new ModelChangedEvent(this, documentElement,
		// PROPERTY_PACKAGE_PATH, oldValue, newValue));
		// }
		// }

	}

	public void reload(InputStream source, boolean outOfSync)
		throws CoreException {
	}

	public void setAuthor(String namespace) {
		if (authorTextNode != null) {
			authorTextNode.setNodeValue(namespace);
		}
		else {
			NodeList namespaces = document.getDocumentElement().getElementsByTagName(IServiceBuilderModel.ELEMENT_NAMESPACE);

			Node refChild = null;

			if (namespaces.getLength() > 0) {
				refChild = namespaces.item(0);
			}

			if (refChild == null) {
				NodeList entities = document.getDocumentElement().getElementsByTagName(IServiceBuilderModel.ELEMENT_ENTITY);

				if (entities.getLength() > 0) {
					refChild = entities.item(0);
				}
			}

			Node authorNode = NodeUtil.getFirstNamedChildNode(document.getDocumentElement(), IServiceBuilderModel.ELEMENT_AUTHOR);

			if (authorNode == null) {
				authorNode = document.createElement(IServiceBuilderModel.ELEMENT_AUTHOR);

				document.getDocumentElement().insertBefore(authorNode, refChild);
			}

			authorTextNode = NodeUtil.setTextContent(authorNode, namespace);

			((TextImpl) authorTextNode).addAdapter(this);
		}
	}

	public void setNamespace(String namespace) {
		if (namespaceTextNode != null) {
			namespaceTextNode.setNodeValue(namespace);
		}
		else {
			NodeList entities = document.getDocumentElement().getElementsByTagName(IServiceBuilderModel.ELEMENT_ENTITY);

			Node refChild = null;

			if (entities.getLength() > 0) {
				refChild = entities.item(0);
			}

			Node namespaceNode = NodeUtil.getFirstNamedChildNode(document.getDocumentElement(), IServiceBuilderModel.ELEMENT_NAMESPACE);

			if (namespaceNode == null) {
				namespaceNode = document.createElement(IServiceBuilderModel.ELEMENT_NAMESPACE);

				document.getDocumentElement().insertBefore(namespaceNode, refChild);
			}

			namespaceTextNode = NodeUtil.setTextContent(namespaceNode, namespace);

			((TextImpl) namespaceTextNode).addAdapter(this);
		}
	}

	public void setPackagePath(String packagePath) {
		document.getDocumentElement().setAttribute(IServiceBuilderModel.ATTR_PACKAGE_PATH, packagePath);
	}

	@Override
	protected void updateTimeStamp() {

	}

}
