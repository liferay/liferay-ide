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

package com.liferay.ide.portlet.ui.navigator;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.PortletApp;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class PortletsNode {

	public PortletsNode(PortletResourcesRootNode parent) {
		_parent = parent;
	}

	public Object[] getChildren() {
		if (_getPortletAppModelElement() != null) {
			List<PortletNode> portletNodes = new ArrayList<>();

			for (Portlet portlet : _getPortletAppModelElement().getPortlets()) {
				portletNodes.add(new PortletNode(this, portlet));
			}

			return portletNodes.toArray(new PortletNode[0]);
		}

		return _EMPTY;
	}

	public PortletResourcesRootNode getParent() {
		return _parent;
	}

	public boolean hasChildren() {
		PortletApp model = _getPortletAppModelElement();

		if (model != null) {
			ElementList<Portlet> portlets = model.getPortlets();

			if (!portlets.isEmpty()) {
				return true;
			}

			return false;
		}

		return false;
	}

	private PortletApp _getPortletAppModelElement() {
		if (_modelElement == null) {
			IFile portletXmlFile = ProjectUtil.getPortletXmlFile(this._parent.getProject());

			if (FileUtil.exists(portletXmlFile)) {
				try (InputStream inputStream = portletXmlFile.getContents()) {
					IModelManager modelManager = StructuredModelManager.getModelManager();

					IStructuredModel portletXmlModel = modelManager.getModelForRead(portletXmlFile);

					IModelStateListener listener = new IModelStateListener() {

						public void modelAboutToBeChanged(IStructuredModel model) {
						}

						public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
						}

						public void modelChanged(IStructuredModel model) {
							_refresh();
						}

						public void modelDirtyStateChanged(IStructuredModel model, boolean dirty) {
							_refresh();
						}

						public void modelReinitialized(IStructuredModel structuredModel) {
							_refresh();
						}

						public void modelResourceDeleted(IStructuredModel model) {
							_refresh();
						}

						public void modelResourceMoved(IStructuredModel oldModel, IStructuredModel newModel) {
							_refresh();
						}

						private void _refresh() {
							portletXmlModel.removeModelStateListener(this);

							if (!PortletsNode.this._modelElement.disposed()) {
								PortletsNode.this._modelElement.dispose();
							}

							PortletsNode.this._modelElement = null;
							PortletsNode.this._parent.refresh();
						}

					};

					portletXmlModel.addModelStateListener(listener);

					_modelElement = PortletApp.TYPE.instantiate(new RootXmlResource(new XmlResourceStore(inputStream)));
				}
				catch (Exception e) {
					PortletUIPlugin.logError(e);
				}
			}
		}

		return _modelElement;
	}

	private static final Object[] _EMPTY = {};

	private PortletApp _modelElement = null;
	private PortletResourcesRootNode _parent;

}